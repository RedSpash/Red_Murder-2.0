package fr.red_spash.murder.spawn;

import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.RoleConfiguration;
import fr.red_spash.murder.game.roles.concrete_roles.*;
import fr.red_spash.murder.utils.ItemStackBuilder;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleManagerListener implements Listener {

    public static final String INVENTORY_NAME = "§d§lRôles de la partie";
    public static final String GAME_PRESET_COLOR = "§a§l";
    private final List<Role> allRoles;
    private final RoleConfiguration roleConfiguration;
    private final ArrayList<UUID> openedMenu;
    private final ArrayList<GamePreset> gamePresets;

    public RoleManagerListener(RoleConfiguration roleConfiguration) {
        this.roleConfiguration = roleConfiguration;
        this.allRoles = List.of(
                new Murder(),
                new Detective(),
                new Schizophrenic(),
                new Electrician(),
                new Ancient(),
                new Lucky(),
                new Spy(),
                new Vagabond(),
                new Innocent()
        );
        this.gamePresets = new ArrayList<>();
        this.openedMenu = new ArrayList<>();

        this.loadPresets();
    }

    private void loadPresets() {
        this.gamePresets.add(
                new GamePreset("Classique",
                        new Murder(),
                        new Detective(),
                        new Schizophrenic(),
                        new Ancient(),
                        new Electrician(),
                        new Lucky(),
                        new Spy())
        );
        ArrayList<Role> allRolePreset = new ArrayList<>();
        for(Role role : this.allRoles){
            allRolePreset.add(Utils.createNewInstance(role));
        }
        this.gamePresets.add(new GamePreset("Tous les rôles",
                allRolePreset)
        );
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        if(e.getItem() == null)return;
        if(e.getAction() != Action.RIGHT_CLICK_AIR
                && e.getAction() != Action.RIGHT_CLICK_BLOCK)return;
        ItemStack itemStack = e.getItem();
        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;

        Player p = e.getPlayer();
        String displayName = itemMeta.getDisplayName();
        if(displayName.equals(SpawnManager.VIEW_ROLES.getItemMeta().getDisplayName())){
            e.setCancelled(true);
            this.openInventory(p);
        }
    }

    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent e){
        if(e.getPlayer() instanceof Player p
                && (this.openedMenu.contains(p.getUniqueId()))){
            this.openedMenu.remove(p.getUniqueId());
        }
    }

    private void openInventory(Player p) {
        if(!this.openedMenu.contains(p.getUniqueId())){
            this.openedMenu.add(p.getUniqueId());
        }

        int sizeNeededForRoles = this.getNecessarySpaceFor(this.allRoles)*9;
        int sizeNeededForPreset = this.getNecessarySpaceFor(this.gamePresets)*9;


        Inventory inventory = Bukkit.createInventory(null,sizeNeededForRoles+sizeNeededForPreset+9, INVENTORY_NAME+" §7§l("+this.roleConfiguration.getRoles().size()+"/"+Bukkit.getOnlinePlayers().size()+")");
        if(p.getOpenInventory().getTitle().startsWith(INVENTORY_NAME)){
            inventory = p.getOpenInventory().getTopInventory();
            p.getOpenInventory().setTitle(INVENTORY_NAME+" §7§l("+this.roleConfiguration.getRoles().size()+"/"+Bukkit.getOnlinePlayers().size()+")");
        }

        inventory.setItem(sizeNeededForRoles+8, new ItemStackBuilder(Material.LAPIS_LAZULI,Math.min(1,this.roleConfiguration.getRoles().size()))
                .setName("§a"+this.roleConfiguration.getRoles()+" rôles attribué(s)")
                .setLore("§7Permet de savoir le","§7nombre de rôle attribué","§7avec cette configuration")
                .toItemStack());

        inventory.setItem(sizeNeededForRoles+7, new ItemStackBuilder(Material.REDSTONE,Math.min(1,Bukkit.getOnlinePlayers().size()-this.roleConfiguration.getRoles().size()))
                .setName("§a"+this.roleConfiguration.getRoles()+" rôles restants")
                .setLore("§7Permet de savoir le","§7nombre de rôle encore attribuable","§7avec cette configuration")
                .toItemStack());

        int index = 0;
        for(Role role : this.allRoles){
            int amount = this.roleConfiguration.getAmountRole(role);
            ArrayList<String> lore = new ArrayList<>(splitInSixWords(role.getDescription(),"§7"));
            if(p.isOp()){
                lore.add("§f");
                lore.add("§a + Clique gauche pour ajouter un "+role.getName());
                lore.add("§c - Clique droit pour retirer un "+role.getName());
            }
            lore.add("§f§7");
            lore.add("§fIl y a "+amount+" "+role.getName()+" dans la prochaine partie.");
            ItemStack item = new ItemStackBuilder(Material.PLAYER_HEAD, Math.max(1,amount))
                    .setName(ChatColor.of(role.getRoleColor()) +"§l"+role.getName())
                    .setLore(lore)
                    .setHeadTexture(role.getHeadUUID())
                    .toItemStack();
            inventory.setItem(index, item);
            index = index + 1;
        }
        index = inventory.getSize()-1;
        for(GamePreset gamePreset : this.gamePresets){
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7Rôle du preset:");
            lore.addAll(gamePreset.getDescription());
            inventory.setItem(index,
                    new ItemStackBuilder(Material.PLAYER_HEAD)
                            .setName(GAME_PRESET_COLOR +gamePreset.getName())
                            .setLore(lore)
                            .setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTJiMTcxMmI5MDdjZTZiMTQwMmVhYWMyOGVjMjRhNGQ5NTU2OGY0YWI4N2U1OTc5ODBjMTViMjJiYmJkN2E1In19fQ==")
                            .toItemStack()
            );
            index = index - 1;
        }
        if(!p.getOpenInventory().getTitle().equals(INVENTORY_NAME)){
            p.openInventory(inventory);
        }
    }

    private int getNecessarySpaceFor(List<?> list) {
        int size = list.size()/9;
        if(this.allRoles.size() % 9 != 0){
            size = size + 1;
        }
        return size;
    }

    public List<String> splitInSixWords(String phrase, String prefix) {
        ArrayList<String> splitWords = new ArrayList<>();
        int index = 0;
        StringBuilder sixWords = new StringBuilder();
        for(String mot : phrase.split(" ")){
            sixWords.append(mot).append(" ");
            index = index + 1;
            if(index >= 6){
                splitWords.add(prefix+sixWords);
                sixWords = new StringBuilder();
                index = 0;
            }
        }
        if(index != 0){
            splitWords.add(prefix+sixWords);
        }
        return splitWords;
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player p))return;
        if(!p.isOp())return;
        if(e.getCurrentItem() == null)return;
        ItemStack itemStack = e.getCurrentItem();
        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;
        if(!p.getOpenInventory().getTitle().startsWith(INVENTORY_NAME))return;

        if(e.getSlot() < this.allRoles.size()){
            Role clickedRole = this.allRoles.get(e.getSlot());

            e.setCancelled(true);
            if(e.getAction() == InventoryAction.PICKUP_ALL){
                this.roleConfiguration.addRole(Utils.createNewInstance(clickedRole));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,2);
                Bukkit.broadcastMessage("§aLe rôle "+ChatColor.of(clickedRole.getRoleColor())+clickedRole.getName()+" §avient d'être ajouté. Il reste "+this.roleConfiguration.getAmountRole(clickedRole)+" "+ChatColor.of(clickedRole.getRoleColor())+clickedRole.getName()+"(s)");
                this.updateInventories();
            }else if(e.getAction() == InventoryAction.PICKUP_HALF){
                Role findedRole = null;
                for(Role role : this.roleConfiguration.getRoles()){
                    if(role.getClass().equals(clickedRole.getClass())){
                        findedRole = role;
                        break;
                    }
                }
                if(findedRole != null){
                    this.roleConfiguration.removeRole(findedRole);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,1);
                    Bukkit.broadcastMessage("§cLe rôle "+ChatColor.of(clickedRole.getRoleColor())+clickedRole.getName()+" §cvient d'être retiré. Il reste "+this.roleConfiguration.getAmountRole(clickedRole)+" "+ChatColor.of(findedRole.getRoleColor())+findedRole.getName()+"(s)");
                }else{
                    p.sendMessage("§cImpossible de retirer un "+clickedRole.getName());
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT,1,1);
                }
                this.updateInventories();
            }
        }else{
            String displayName = itemMeta.getDisplayName();
            for(GamePreset gamePreset : this.gamePresets){
                if(displayName.equals(GAME_PRESET_COLOR+gamePreset.getName())){
                    this.roleConfiguration.loadPreset(gamePreset);
                    Bukkit.broadcastMessage("§bLe preset §l"+gamePreset.getName()+"§r§b vient d'être chargé!");
                    this.updateInventories();
                    return;
                }
            }
        }
    }

    private void updateInventories() {
        for(UUID uuid : (ArrayList<UUID>)this.openedMenu.clone()){
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()){
                this.openInventory(p);
                if(!this.openedMenu.contains(p.getUniqueId())){
                    this.openedMenu.add(p.getUniqueId());
                }
            }else{
                this.openedMenu.remove(uuid);
            }
        }
    }
}
