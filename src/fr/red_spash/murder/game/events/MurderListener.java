package fr.red_spash.murder.game.events;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.commands.CanThrowSwordCommand;
import fr.red_spash.murder.game.commands.DashCommand;
import fr.red_spash.murder.game.commands.GiveSwordCommand;
import fr.red_spash.murder.game.roles.concrete_roles.Murder;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.tasks.CooldownTask;
import fr.red_spash.murder.game.tasks.ItemKillerTask;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class MurderListener implements Listener {

    private final HashMap<UUID, Long> cooldownLaunchSword;
    private final PlayerManager playerManager;
    private final JavaPlugin main;
    private final GameManager gameManager;
    private final DeathManager deathManager;

    public MurderListener(GameManager gameManager, DeathManager deathManager, JavaPlugin main){
        this.playerManager = gameManager.getPlayerManager();
        this.deathManager = deathManager;
        this.main = main;
        this.gameManager = gameManager;
        this.cooldownLaunchSword = new HashMap<>();
    }

    @EventHandler
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent e){
        e.setCancelled(true);

        if(!(e.getEntity() instanceof Player target))return;
        if(!(e.getDamager() instanceof Player damager))return;

        ItemStack itemStack = damager.getInventory().getItemInMainHand();
        if(!itemStack.equals(Murder.MURDER_SWORD))return;

        PlayerData targetPlayerData = this.playerManager.getData(target);
        Role targetRole = targetPlayerData.getVisualRole();

        PlayerData damagerPlayerData = this.playerManager.getData(damager);
        Role damagerRole = damagerPlayerData.getVisualRole();

        if(!(damagerRole.isMurder()))return;
        if(targetRole.isMurder()){
            damager.playSound(damager.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 2, 0);
            damager.sendTitle("§c","§cVous ne pouvez pas taper votre allié!",0,30,0);
            return;
        }

        damager.playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT,1,1);

        this.deathManager.killPlayer(target,damager, "Un "+targetRole.getName()+" vient de mourir par un meurtrier!");
    }

    @EventHandler
    public void playerToggleSneakEvent(PlayerToggleSneakEvent e){
        if(!e.isSneaking())return;

        Player p = e.getPlayer();
        ItemStack itemStack = p.getInventory().getItemInMainHand();
        if(!itemStack.equals(Murder.MURDER_SWORD))return;
        PlayerData playerData = this.playerManager.getData(p);
        Role role = playerData.getVisualRole();

        if(!(role instanceof Murder murder))return;
        if(!murder.canDash())return;


        playerData.addCooldown(new CooldownTask("§c§lDASH", playerData, 10, new DashCommand(p, murder), this.main));

        murder.setDash(false);
        p.setVelocity(p.getLocation().getDirection().multiply(1.2));
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2,1);
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(this.cooldownLaunchSword.containsKey(p.getUniqueId())
                && (this.cooldownLaunchSword.get(p.getUniqueId()) > System.currentTimeMillis()))return;


        ItemStack itemStack = e.getItem();
        if(itemStack == null || (!itemStack.equals(Murder.MURDER_SWORD)))return;
        if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        PlayerData playerData = this.playerManager.getData(p);
        Role role = playerData.getVisualRole();

        if(!(role instanceof Murder murder))return;
        if(!murder.canThrowSword())return;
        murder.setThrowSword(false);
        this.cooldownLaunchSword.put(p.getUniqueId(), System.currentTimeMillis()+1000);

        playerData.addCooldown(new CooldownTask("§c§lÉPÉE", playerData, 10, new GiveSwordCommand(p, murder), this.main, false));
        playerData.addCooldown(new CooldownTask("§c§lLANCÉ D'ÉPÉE", playerData, 10, new CanThrowSwordCommand(p, murder), this.main));

        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2,1);
        p.getInventory().setItem(Murder.MURDER_SWORD_SLOT,null);
        p.getInventory().remove(Murder.MURDER_SWORD);

        Item item = p.getWorld().dropItem(p.getLocation().add(0,1,0), Murder.MURDER_SWORD);
        item.setPickupDelay(25*3);
        item.setVelocity(p.getLocation().getDirection().multiply(0.8).add(new Vector(0,0.3,0)));
        murder.setSwordOnGround(item);

        new ItemKillerTask(item, this.main, this.gameManager, p, this.deathManager);
    }

    @EventHandler
    public void entityPickupItemEvent(EntityPickupItemEvent e){
        if(!(e.getEntity() instanceof Player p))return;

        ItemStack itemStack = e.getItem().getItemStack();
        if(!itemStack.equals(Murder.MURDER_SWORD))return;
        e.setCancelled(true);

        PlayerData playerData = this.playerManager.getData(p);
        Role role = playerData.getVisualRole();

        if(!(role instanceof Murder murder))return;

        if(murder.getSwordOnGround() == null)return;
        if(!murder.getSwordOnGround().equals(e.getItem()))return;

        for(CooldownTask cooldownTask : playerData.getCooldownTasks()){
            if(cooldownTask.getCommand() instanceof GiveSwordCommand){
                CooldownTask removedTask = playerData.removeCooldownTask(cooldownTask);
                removedTask.stopTask();
            }
        }

        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2,1);
        p.getInventory().setItem(Murder.MURDER_SWORD_SLOT,Murder.MURDER_SWORD);
        e.getItem().remove();
    }

}
