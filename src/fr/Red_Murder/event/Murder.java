package fr.Red_Murder.event;

import fr.Red_Murder.Main;
import fr.Red_Murder.ScoreboardManager;
import fr.Red_Murder.roles.RoleManager;
import fr.Red_Murder.tasks.EndMurder;
import fr.Red_Murder.tasks.MurderGame;
import fr.Red_Murder.tasks.StartMurder;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.github.paperspigot.Title;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class Murder implements Listener {

    public static int temps = 8*60;

    public enum State {
        Waiting,Starting,Playing,End
    }
    public enum Role {
        Murder,Detective,Schizophrène,Innocent
    }

    public static HashMap<String,RoleManager> Roles = new HashMap<String,RoleManager>();
    public static HashMap<String,Integer> Detective_Cooldown = new HashMap<String,Integer>();
    public static HashMap<String, BukkitTask> Detective_Cooldown_Task = new HashMap<String, BukkitTask>();
    public static ArrayList<ArmorStand> Dead_Body = new ArrayList<ArmorStand>();


    public static State state = State.Waiting;
    public static Location BowLocation = null;
    public static String Schizo = null;
    public static ArmorStand BowArmor = null;


    public static void Start_Game(){
        Roles.clear();
        state = State.Playing;
        ArrayList<Location> Spawns = (ArrayList<Location>) Main.Spawn.clone();
        ArrayList<RoleManager> Roles_restant = new ArrayList<RoleManager>();
        Roles_restant.add(new RoleManager(Role.Murder));
        Roles_restant.add(new RoleManager(Role.Detective));
        Roles_restant.add(new RoleManager(Role.Schizophrène));
        Collection<? extends Player> Online = Bukkit.getOnlinePlayers();

        if(Online.size() > Roles_restant.size() ){
            for(int i = 0; i <= Online.size() -Roles_restant.size() ;i++){
                Roles_restant.add(new RoleManager(Role.Innocent));
            }
        }
        for(Player p : Online){
            p.getInventory().setHeldItemSlot(0);
            for(Player pl: Online){
                p.showPlayer(pl);
            }
            p.getInventory().clear();
            int index_l = (int)(Math.random() * Spawns.size());
            int index_r = (int)(Math.random() * Roles_restant.size());

            p.teleport(Spawns.get(index_l));
            Spawns.remove(index_l);
            RoleManager r = Roles_restant.get(index_r);
            r.set_Player(p.getName());
            Roles.put(p.getName(),r);
            Roles_restant.remove(index_r);

            p.setGameMode(GameMode.ADVENTURE);

            r.give_main_item();
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,25,1,true));
            p.playSound(p.getLocation(), Sound.NOTE_PLING,2,1);

            if(r.get_role_type() == Role.Schizophrène){
                Schizo = p.getName();
            }

            p.sendTitle(new Title("§e§lVous êtes "+r.get_role_color()+r.get_role_name(),"§e§lBonne chance !",0,3*20,20));
            p.sendMessage("\n§6§l------------------------------------------\n" +
                    "§6§l➥ §6Vous êtes "+r.get_role_color()+r.get_role_name()+" §6!\n§f\n§5\n§f" + r.get_description()+
                    "\n§6§l------------------------------------------\n");
        }
        MurderGame.time = 8*60;
        new MurderGame().runTaskTimer(Main.getInstance(), 0L, 20L);

    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        String Name = EventListener.getPrefix(p);
        p.setPlayerListName(Name+p.getName());
        ScoreboardManager.setScoreboard(p);

        if(state == State.Playing){
            if(Roles.containsKey(p.getName())){
                e.setJoinMessage(Name+p.getName()+" §aest de retour dans la partie !");
            }else{
                e.setJoinMessage(Name+p.getName()+" §apasse en spectateur !");
                for(Player pl : Bukkit.getOnlinePlayers()){
                    pl.hidePlayer(p);
                }
                p.setAllowFlight(true);
                p.setFlying(true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,10000*20,2,true));
            }
        }else{
            e.setJoinMessage(Name+p.getName()+" §avient de rejoindre le murder !");
        }

        if(p.getName().equals("Red_Spash")) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.playSound(pl.getLocation(), Sound.NOTE_PLING, 1, 2);
            }
        }



    }

    @EventHandler
    public void OnLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        String Name = EventListener.getPrefix(p);

        if(p.getName().equals("Red_Spash")){
            for(Player pl : Bukkit.getOnlinePlayers()){
                pl.playSound(pl.getLocation(), Sound.NOTE_PLING,1,0);
            }
        }
        e.setQuitMessage(Name+p.getName()+" §cvient de quitter le murder !");
    }

    @EventHandler
    public void OnFireBow(EntityShootBowEvent e){
        if (e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            e.getProjectile().setCustomName(p.getName());
            if(Roles.containsKey(p.getName())){
                RoleManager role = Roles.get(p.getName());
                if(role.get_role_type() == Role.Detective){
                    if(!Detective_Cooldown.containsKey(p.getName())){
                        p.setLevel(20);
                        Detective_Cooldown.put(p.getName(),20);

                        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new BukkitRunnable() {
                            @Override
                            public void run() {

                                if(role.get_specator() || Murder.state != State.Playing){
                                    Detective_Cooldown.remove(p.getName());
                                    Detective_Cooldown_Task.get(p.getName()).cancel();
                                    p.setLevel(0);
                                    return;
                                }

                                p.setLevel(Detective_Cooldown.get(p.getName()));
                                p.setExp(Detective_Cooldown.get(p.getName())/20);

                                if(Detective_Cooldown.get(p.getName()) <= 0){
                                    role.give_bow();
                                    p.playSound(p.getLocation(), Sound.ORB_PICKUP,2,2);
                                    Detective_Cooldown.remove(p.getName());
                                    Detective_Cooldown_Task.get(p.getName()).cancel();
                                    return;
                                }
                                Detective_Cooldown.put(p.getName(),Detective_Cooldown.get(p.getName())-1);

                            }
                        },10,20);

                        Detective_Cooldown_Task.put(p.getName(),task);

                    }

                }
            }

        }
    }
    @EventHandler
    public void PlayerDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPlayerDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(e.getDamager() instanceof Player){
                Player damager = (Player) e.getDamager();
                if(Roles.containsKey(damager.getName()) || Roles.containsKey(p.getName())){
                    if(damager.getInventory().getItemInHand() != null){
                        if(damager.getInventory().getItemInHand().getItemMeta() != null){
                            if(damager.getInventory().getItemInHand().getItemMeta().getDisplayName() != null) {
                                if (damager.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lArme du meurtrier")) {
                                    RoleManager player_role = Roles.get(p.getName());
                                    RoleManager damager_role = Roles.get(damager.getName());
                                    if (damager_role.get_role_type() == Role.Murder || damager_role.get_subrole_type() == Role.Murder) {
                                        if (player_role.get_role_type() == Role.Murder || player_role.get_subrole_type() == Role.Murder) {
                                            damager.sendTitle(new Title("§c", "§cVous ne pouvez pas tuer votre §c§lcoéquipier §c!", 0, 20 * 2, 0));
                                            damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 2, 1);
                                            e.setCancelled(true);
                                        } else {
                                            death(p, damager);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else if(e.getDamager() instanceof Arrow){
                if(e.getDamager().getCustomName() != null){
                    Player damager = Bukkit.getPlayer(e.getDamager().getCustomName());
                    if(Roles.containsKey(damager.getName()) || Roles.containsKey(p.getName())){
                        e.getDamager().remove();
                        death(p,damager);
                        if(Roles.containsKey(p.getName())){
                            if(Roles.get(p.getName()).get_role_type() != Role.Murder
                                    && Roles.get(p.getName()).get_subrole_type() != Role.Murder){
                                death(damager,p);
                            }
                        }
                    }
                }
            }
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void OnMoove(PlayerMoveEvent e){
        if(BowLocation != null){
            Player p = e.getPlayer();
            if(Roles.containsKey(p.getName())) {
                RoleManager roleManager = Roles.get(p.getName());
                if (!roleManager.get_specator()) {
                    if (roleManager.get_role_type() == Role.Innocent){
                        for (Entity entity : p.getWorld().getNearbyEntities(p.getLocation(), 1, 1, 1)) {
                            if (entity instanceof ArmorStand) {
                                if (entity.getCustomName() == "BowPosition") {
                                    if(BowLocation != null){
                                        BowLocation = null;
                                        BowArmor.remove();
                                        Bukkit.broadcastMessage("§2L'arc du détective vient d'être ramassé !");
                                        roleManager.give_main_item();
                                        roleManager.give_bow();

                                        for (Player pl : Bukkit.getOnlinePlayers()) {
                                            pl.getInventory().remove(Material.COMPASS);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
    }

    public void death(Player dead, Player killer){
        if(Roles.containsKey(dead.getName())){
            RoleManager RM_Dead = Roles.get(dead.getName());
            RM_Dead.set_specator(true);
            for(Player pl : Bukkit.getOnlinePlayers()){
                pl.hidePlayer(dead);
                //pl.playSound(pl.getLocation(), Sound.HURT_FLESH,5,1);
            }
            dead.sendTitle(new Title("§c§lVous êtes mort !","§c",0,20*4,20));
            dead.playSound(dead.getLocation(), Sound.HURT_FLESH,5,1);
            if(RM_Dead.get_role_type() == Role.Detective){
                Bukkit.broadcastMessage("§cL'arc du détective vient de tomber au sol !");
                ItemStack compass = new ItemStack(Material.COMPASS);
                ItemMeta itemMeta = compass.getItemMeta();
                itemMeta.setDisplayName("§aPosition de l'arc du détective");
                itemMeta.addEnchant(Enchantment.ARROW_FIRE,1,false);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                compass.setItemMeta(itemMeta);
                BowLocation = dead.getLocation();
                for(Player pl :Bukkit.getOnlinePlayers()){
                    pl.getInventory().setItem(4,compass);
                    pl.setCompassTarget(BowLocation);
                }
                Location loc = dead.getLocation();
                ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                ItemStack helmet = new ItemStack(Material.BOW);

                ItemMeta helmetMeta = helmet.getItemMeta();
                helmetMeta.addEnchant(Enchantment.ARROW_FIRE,1,true);
                helmet.setItemMeta(helmetMeta);
                armorStand.setHelmet(helmet);
                armorStand.setItemInHand(helmet);
                armorStand.setCustomNameVisible(false);
                armorStand.setCustomName("BowPosition");
                armorStand.setCanPickupItems(false);
                armorStand.getEyeLocation().setYaw(dead.getLocation().getYaw());
                BowArmor = armorStand;
            }

            dead.setGameMode(GameMode.ADVENTURE);

            dead.getInventory().clear();
            dead.setAllowFlight(true);
            dead.setFlying(true);


            corps(dead);

            dead.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*3,10,true));
            dead.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,100000*20,10,true));

            check_end_game();

        }


        

    }

    public static void check_end_game() {
        int innocent = 0;
        int murder = 0;
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(Roles.containsKey(pl.getName())){
                RoleManager pl_role = Roles.get(pl.getName());
                if(!pl_role.get_specator()){
                    if(pl_role.get_role_type() == Role.Murder || pl_role.get_subrole_type() == Role.Murder){
                        murder++;
                    }else{
                        innocent++;
                    }
                }
            }
        }
        if(innocent == 0){
            end_game(Role.Murder);
        }else if(murder == 0){
            end_game(Role.Innocent);
        }
    }

    public void corps(Player p) {
        Location loc = p.getLocation().add(0,-1.4,0);
        ArmorStand armorStand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        ItemStack helmet = new ItemStack(Material.SKULL_ITEM,1, (short) SkullType.PLAYER.ordinal());

        SkullMeta helmetMeta = (SkullMeta)helmet.getItemMeta();
        helmetMeta.setOwner(p.getName());
        helmet.setItemMeta(helmetMeta);
        armorStand.setHelmet(helmet);
        armorStand.setCustomNameVisible(false);
        armorStand.setCustomName("Death");
        armorStand.setCanPickupItems(false);
        armorStand.getEyeLocation().setYaw(p.getLocation().getYaw());
        Dead_Body.add(armorStand);
    }

    private static void end_game(Role winnerrole) {
        RoleManager winner_role = new RoleManager(winnerrole);
        new EndMurder().runTaskTimer(Main.getInstance(), 0L, 10L);
        for(Player p : Bukkit.getOnlinePlayers()){
            if(Roles.containsKey(p.getName())){
                RoleManager role = Roles.get(p.getName());
                if(role.get_role_type() == winner_role.get_role_type()
                        || role.get_subrole_type() == winner_role.get_role_type() || (role.get_role_type() == Role.Detective && winner_role.get_role_type() == Role.Innocent)){
                    p.sendTitle(new Title("§6§lVous avez gagné !","§eLes "+winner_role.get_role_color()+winner_role.get_role_name()+"s §egagnent la partie !"));
                }else{
                    p.sendTitle(new Title("§c§lVous avez perdu !","§eLes "+winner_role.get_role_color()+winner_role.get_role_name()+"s §egagnent la partie !"));
                }
            }
        }
    }


    @EventHandler
    public void OnInteraction(PlayerInteractEvent e){
        if(!e.getPlayer().isOp() || e.getPlayer().getGameMode() == GameMode.ADVENTURE){
            if(!e.getAction().equals(Action.PHYSICAL)){
                if (e.getClickedBlock() != null) {
                    if (Main.LockedBlock.contains(e.getClickedBlock().getType())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void ItemPickup(PlayerPickupItemEvent e){
        Player p = e.getPlayer();
        ItemStack gold_ingot = new ItemStack(Material.GOLD_INGOT);
        gold_ingot.setAmount(e.getItem().getItemStack().getAmount());
        ItemMeta gold_meta = gold_ingot.getItemMeta();
        gold_meta.setDisplayName("§6OR");
        gold_ingot.setItemMeta(gold_meta);
        if(e.getItem().getItemStack().getType() == Material.GOLD_INGOT){
            if(Roles.containsKey(p.getName())){
                RoleManager roleManager = Roles.get(p.getName());
                if(roleManager.get_specator()){
                    e.setCancelled(true);
                }else{
                    e.getItem().setPickupDelay(20*20);
                    e.setCancelled(true);
                    e.getItem().remove();
                    p.playSound(p.getLocation(), Sound.ORB_PICKUP,2,1);
                    p.sendMessage("§6+"+e.getItem().getItemStack().getAmount()+" lingot d'or");
                    if(p.getInventory().contains(Material.GOLD_INGOT)){
                        p.getInventory().addItem(gold_ingot);
                    }else{
                        p.getInventory().setItem(8,gold_ingot);
                    }
                    check_gold(p);
                }
            }
        }
    }

    private void check_gold(Player p) {
        RoleManager roleManager = Roles.get(p.getName());
        ItemStack gold = p.getInventory().getItem(8);
        if(gold != null){
            if(gold.getAmount() >= 10){
                roleManager.give_bow();
                if(p.getInventory().getItem(8).getAmount() > 10){
                    p.getInventory().getItem(8).setAmount(p.getInventory().getItem(8).getAmount()-10);
                    check_gold(p);
                }else{
                    p.getInventory().clear(8);
                }


            }
        }
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e){
        if(!e.getWhoClicked().isOp() || e.getWhoClicked().getGameMode() == GameMode.ADVENTURE){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDropEvent(PlayerDropItemEvent e){
        if(!e.getPlayer().isOp() || e.getPlayer().getGameMode() == GameMode.ADVENTURE){
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void OnBlockBreak(BlockBreakEvent e){
        if(!e.getPlayer().isOp() || e.getPlayer().getGameMode() == GameMode.ADVENTURE){
            e.setCancelled(true);
        }
    }

    public static void Spawn_Gold(){
        while(true){
            for(int i =0 ; i<= 6; i++){
                Location middle = new Location(Bukkit.getWorld("world"),37,80+i,-2);
                if(middle.getBlock() != null){
                    if(middle.getBlock().getType() == Material.AIR){
                        Item item = (Item) middle.getWorld().dropItemNaturally(middle.add(Main.getInstance().random_number(-50,50),0,Main.getInstance().random_number(-32,32) ),new ItemStack(Material.GOLD_INGOT));
                        return;
                    }
                }
            }
        }

    }

    @EventHandler
    public void OnItemMerge(ItemMergeEvent e){
        if(e.getEntity().getType().equals(Material.GOLD_INGOT )
                || e.getTarget().getType().equals(Material.GOLD_INGOT)){
            e.setCancelled(true);
        }
    }


}
