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
import java.util.stream.Collectors;

public class RoleManagerListener implements Listener {

    public static final String INVENTORY_NAME = "§d§lRôles de la partie";
    public static final String GAME_PRESET_COLOR = "§a§l";
    public static final int SPACE_BEFORE_ROLES = 9;
    private final List<Role> allRoles;
    private final RoleConfiguration roleConfiguration;
    private final ArrayList<UUID> openedMenu;
    private final ArrayList<GamePreset> gamePresets;

    public RoleManagerListener(RoleConfiguration roleConfiguration, List<Role> allRoles) {
        this.roleConfiguration = roleConfiguration;
        this.allRoles = allRoles;
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
            if(!role.getClass().equals(Innocent.class)){
                allRolePreset.add(Utils.createNewInstance(role));
            }
        }
        GamePreset allRoleGamePreset = new GamePreset("Tous les rôles", allRolePreset);
        this.gamePresets.add(allRoleGamePreset
        );
        this.roleConfiguration.loadPreset(allRoleGamePreset);

        ArrayList<Role> roleWithoutSchizophrenic = new ArrayList<>();
        roleWithoutSchizophrenic.addAll(allRolePreset.stream().filter(role -> !(role instanceof Schizophrenic)).collect(Collectors.toList()));
        this.gamePresets.add(new GamePreset("Sans Schizophrène",
                roleWithoutSchizophrenic)
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

        int sizeNeededForRoles = this.getNecessarySpaceFor(this.allRoles)* SPACE_BEFORE_ROLES;
        int sizeNeededForPreset = this.getNecessarySpaceFor(this.gamePresets)* SPACE_BEFORE_ROLES;

        Inventory inventory = Bukkit.createInventory(null,sizeNeededForRoles+sizeNeededForPreset+(SPACE_BEFORE_ROLES *3), INVENTORY_NAME);
        if(p.getOpenInventory().getTitle().startsWith(INVENTORY_NAME)){
            inventory = p.getOpenInventory().getTopInventory();
        }

        inventory.setItem(sizeNeededForRoles+ SPACE_BEFORE_ROLES *2+2, new ItemStackBuilder(Material.LAPIS_LAZULI,Math.max(1,this.roleConfiguration.getRoles().size()))
                .setName("§a"+this.roleConfiguration.getRoles().size()+" rôles attribué(s)")
                .setLore("§7Permet de savoir le","§7nombre de rôle attribué","§7avec cette configuration")
                .toItemStack());
        int remainingRoles = Math.max(0,Bukkit.getOnlinePlayers().size()-this.roleConfiguration.getRoles().size());
        Material material = Material.REDSTONE;
        if(remainingRoles == 0){
            material = Material.BARRIER;
        }
        inventory.setItem(sizeNeededForRoles+ SPACE_BEFORE_ROLES *2+1, new ItemStackBuilder(material,Math.max(1,remainingRoles))
                .setName("§a"+remainingRoles+" rôles restants")
                .setLore("§7Permet de savoir le","§7nombre de rôle encore attribuable","§7avec cette configuration")
                .toItemStack());

        int index = SPACE_BEFORE_ROLES;
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
            String headURL = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==";
            if(amount >= 1){
                headURL = role.getHeadUUID();
            }
            ItemStack item = new ItemStackBuilder(Material.PLAYER_HEAD, Math.max(1,amount))
                    .setName(ChatColor.of(role.getRoleColor()) +"§l"+role.getName())
                    .setLore(lore)
                    .setHeadTexture(headURL)
                    .hideAttributes()
                    .toItemStack();


            inventory.setItem(index, item);
            index = index + 1;
        }
        index = inventory.getSize() - 1 - SPACE_BEFORE_ROLES - 4;
        for(GamePreset gamePreset : this.gamePresets){
            if(index >= inventory.getSize()){
                break;
            }
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
            index = index + 1;
        }

        material = Material.LIME_STAINED_GLASS_PANE;
        if(Bukkit.getOnlinePlayers().size()-this.roleConfiguration.getRoles().size() < 0){
            material = Material.RED_STAINED_GLASS_PANE;
        }
        ItemStack filledItem = new ItemStackBuilder(material).setName("§f").hideAttributes().toItemStack();
        for(int i =0; i< inventory.getSize(); i++){
            if(inventory.getItem(i) == null || inventory.getItem(i).getType().toString().contains("STAINED_GLASS_PANE")){
                inventory.setItem(i,filledItem);
            }
        }

        if(!p.getOpenInventory().getTitle().equals(INVENTORY_NAME)){
            p.openInventory(inventory);
        }
    }

    private int getNecessarySpaceFor(List<?> list) {
        int size = list.size()/ 9;
        if(list.size() % 9 != 0){
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
        e.setCancelled(true);
        if(e.getRawSlot() - SPACE_BEFORE_ROLES < this.allRoles.size() && e.getRawSlot() - SPACE_BEFORE_ROLES >= 0){
            Role clickedRole = this.allRoles.get(e.getRawSlot() - SPACE_BEFORE_ROLES);

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
                    p.playSound(p.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL,1,1);
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
