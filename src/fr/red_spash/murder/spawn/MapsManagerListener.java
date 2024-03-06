package fr.red_spash.murder.spawn;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.utils.ItemStackBuilder;
import fr.red_spash.murder.utils.Utils;
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
import java.util.UUID;
import java.util.stream.Collectors;

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
        int size = (this.mapManager.getMaps().size()+1)/9;
        if((this.mapManager.getMaps().size()+1) % 9 != 0){
            size = size + 1;
        }
        Inventory inventory = Bukkit.createInventory(null,size*9, INVENTORY_NAME);

        int indexMap = -1;
        int maxVote = 0;
        int index = 0;
        for(GameMap gameMap : this.mapManager.getMaps()){
            ArrayList<String> lore = new ArrayList<>();
            int voteFor = this.getVoteFor(index);
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
        inventory.setItem(inventory.getSize()-1,
                new ItemStackBuilder(Material.CLOCK)
                        .setName("§cAléatoire")
                        .setLore("§7Permet de jouer","§7sur une carte aléatoire.","§f","§9§l"+this.getVoteFor(inventory.getSize()-1)+" votes enregistrés pour la map!")
                        .toItemStack());

        if(indexMap != -1){
            ItemStack itemStack = inventory.getItem(indexMap);
            if(itemStack != null){
                itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
                itemStack.setType(Material.FILLED_MAP);
            }
        }

        p.openInventory(inventory);

    }

    private int getVoteFor(int index) {
        return
                this.votes.values()
                        .stream()
                        .filter(integer -> integer == index)
                        .collect(Collectors.toList())
                        .size();
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
        int vote = e.getRawSlot();
        if(vote == e.getInventory().getSize()-1){
            if(e.getAction() == InventoryAction.PICKUP_ALL){
                this.votes.put(p.getUniqueId(),e.getSlot());
                p.sendMessage("§aVotre vote pour le choix de la carte aléatoire est enregistré!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,0);
                this.updateInventories();
                return;
            }else if(e.getAction() == InventoryAction.PICKUP_HALF){
                if(p.isOp()){
                    this.gameManager.startPreGame(this.mapManager.getMaps().get(Utils.generateRandomNumber(0,this.mapManager.getMaps().size()-1)));
                    this.votes.clear();
                }else{
                    this.votes.remove(p.getUniqueId(),e.getSlot());
                    p.sendMessage("§cVotre vote pour la carte aléatoire vient d'être annulé!");
                    this.updateInventories();
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,0);
                }
            }
        }
        if(vote < this.mapManager.getMaps().size()){
            GameMap gameMap = mapManager.getMaps().get(e.getSlot());

            int actualVote = this.votes.getOrDefault(p.getUniqueId(),-1);
            if(e.getAction() == InventoryAction.PICKUP_ALL){
                if(actualVote != vote){
                    this.votes.put(p.getUniqueId(),e.getSlot());
                    p.sendMessage("§aVotre vote pour la carte "+gameMap.getName()+" est enregistré!");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,2);
                    this.updateInventories();
                }else{
                    p.sendMessage("§cVous avez déjà voté pour cette map!");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,1);
                }

            }else if(e.getAction() == InventoryAction.PICKUP_HALF){
                if(p.isOp()){
                    this.gameManager.startPreGame(gameMap);
                    this.votes.clear();
                }else{
                    if(actualVote == vote){
                        this.votes.remove(p.getUniqueId(),e.getSlot());
                        p.sendMessage("§cVotre vote pour la carte "+gameMap.getName()+" vient d'être annulé!");
                        this.updateInventories();
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT,1,0);

                    }else{
                        p.sendMessage("§cVous n'avez pas voté pour cette map!");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,1);
                    }
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
