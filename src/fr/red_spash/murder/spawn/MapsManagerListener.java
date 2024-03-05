package fr.red_spash.murder.spawn;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.RoleConfiguration;
import fr.red_spash.murder.game.roles.concrete_roles.*;
import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.utils.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MapsManagerListener implements Listener {

    public static final String INVENTORY_NAME = "§b§lMaps disponibles";
    private final MapManager mapManager;
    private final ArrayList<UUID> openedMenu;
    private final HashMap<UUID, Integer> votes;
    private final GameManager gameManager;


    public MapsManagerListener(MapManager mapManager, GameManager gameManager) {
        this.mapManager = mapManager;
        this.gameManager = gameManager;
        openedMenu = new ArrayList<>();
        votes = new HashMap<>();
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
        if(displayName.equals(SpawnManager.VIEW_MAPS.getItemMeta().getDisplayName())){
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
        int size = this.mapManager.getMaps().size()/9;
        if(this.mapManager.getMaps().size() % 9 != 0){
            size = size + 1;
        }
        Inventory inventory = Bukkit.createInventory(null,size*9, INVENTORY_NAME);

        int indexMap = -1;
        int maxVote = 0;
        int index = 0;
        for(GameMap gameMap : this.mapManager.getMaps()){
            ArrayList<String> lore = new ArrayList<>();
            int voteFor = 0;
            for(int vote : this.votes.values()){
                if(vote == index){
                    voteFor = voteFor + 1;
                }
            }
            if(voteFor > maxVote){
                maxVote = voteFor;
                indexMap = index;
            }
            lore.add("§9§l"+voteFor+" votes enregistrés pour la map!");
            lore.add("§f");
            lore.add("§a Clique gauche pour voter pour "+gameMap.getName());

            if(p.isOp()){
                lore.add("§c Clique droit pour lancer la partie!");
            }

            Material material = Material.PAPER;
            if(voteFor > 0){
                material = Material.MAP;
            }
            inventory.setItem(index,
                    new ItemStackBuilder(material)
                            .setName("§b§l"+gameMap.getName())
                            .hideAttributes()
                            .setLore(lore)
                            .toItemStack()
            );
            index = index + 1;
        }

        if(indexMap != -1){
            ItemStack itemStack = inventory.getItem(indexMap);
            if(itemStack != null){
                itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
                itemStack.setType(Material.FILLED_MAP);
            }
        }

        p.openInventory(inventory);

    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player p))return;
        if(e.getCurrentItem() == null)return;
        ItemStack itemStack = e.getCurrentItem();
        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;
        if(!p.getOpenInventory().getTitle().equals(INVENTORY_NAME))return;

        e.setCancelled(true);
        if(e.getSlot() < this.mapManager.getMaps().size()){
            GameMap gameMap = mapManager.getMaps().get(e.getSlot());

            if(e.getAction() == InventoryAction.PICKUP_ALL){
                this.votes.put(p.getUniqueId(),e.getSlot());
                p.sendMessage("§aVotre vote pour la carte "+gameMap.getName()+" est enregistré!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,2);
                this.updateInventories();
            }else if(e.getAction() == InventoryAction.PICKUP_HALF){
                if(p.isOp()){
                    this.gameManager.startPreGame(gameMap);
                    this.votes.clear();
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
