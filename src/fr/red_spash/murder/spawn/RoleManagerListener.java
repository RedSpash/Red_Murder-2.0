package fr.red_spash.murder.spawn;

import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.RoleConfiguration;
import fr.red_spash.murder.game.roles.concrete_roles.*;
import fr.red_spash.murder.utils.ItemStackBuilder;
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

import javax.print.attribute.standard.MediaSize;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleManagerListener implements Listener {

    public static final String INVENTORY_NAME = "§d§lRôles de la partie";
    private final List<Role> allRoles;
    private final RoleConfiguration roleConfiguration;
    private final ArrayList<UUID> openedMenu;

    public RoleManagerListener(RoleConfiguration roleConfiguration) {
        this.roleConfiguration = roleConfiguration;
        allRoles = List.of(
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
        this.openedMenu = new ArrayList<>();
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
        int size = this.allRoles.size()/9;
        if(this.allRoles.size() % 9 != 0){
            size = size + 1;
        }
        Inventory inventory = Bukkit.createInventory(null,size*9, INVENTORY_NAME+" §7§l("+this.roleConfiguration.getRoles().size()+"/"+Bukkit.getOnlinePlayers().size()+")");
        if(p.getOpenInventory().getTopInventory() != null){
            if(p.getOpenInventory().getTitle().startsWith(INVENTORY_NAME)){
                inventory = p.getOpenInventory().getTopInventory();
                p.getOpenInventory().setTitle(INVENTORY_NAME+" §7§l("+this.roleConfiguration.getRoles().size()+"/"+Bukkit.getOnlinePlayers().size()+")");
            }
        }
        int index = 0;
        for(Role role : this.allRoles){
            int amount = this.roleConfiguration.getAmountRole(role);
            ArrayList<String> lore = new ArrayList<>(diviserEnGroupesDeSixMots(role.getDescription(),"§7"));
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
        p.openInventory(inventory);
    }

    public List<String> diviserEnGroupesDeSixMots(String phrase, String prefix) {
        ArrayList<String> splitWords = new ArrayList<>();
        int index = 0;
        String sixWords = "";
        for(String mot : phrase.split(" ")){
            sixWords += mot+" ";
            index = index + 1;
            if(index >= 6){
                splitWords.add(prefix+sixWords);
                sixWords = "";
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
                try {
                    this.roleConfiguration.addRole(clickedRole.getClass().newInstance());
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
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
            p.sendMessage("§cItem introuvable!");
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
