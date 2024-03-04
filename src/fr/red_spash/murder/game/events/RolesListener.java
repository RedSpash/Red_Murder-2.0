package fr.red_spash.murder.game.events;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.game.roles.*;
import fr.red_spash.murder.game.roles.concrete_roles.Lucky;
import fr.red_spash.murder.game.roles.concrete_roles.Murder;
import fr.red_spash.murder.game.roles.concrete_roles.Vagabond;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RolesListener implements Listener {

    private static final int GOLD_SLOT = 8;

    private final HashMap<UUID, Long> cooldownInteractions;
    private final PlayerManager playerManager;
    private final GameManager gameManager;
    private final List<GameActionListener> gamesListeners;

    public RolesListener(GameManager gameManager, List<GameActionListener> gamesListeners){
        this.playerManager = gameManager.getPlayerManager();
        this.gameManager = gameManager;
        this.gamesListeners = gamesListeners;
        this.cooldownInteractions = new HashMap<>();

    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        if(e.getTo() == null)return;
        if(e.getFrom().getBlock() == e.getTo().getBlock())return;

        Player p = e.getPlayer();
        PlayerData playerData = this.playerManager.getData(p);

        if(playerData.isSpectator())return;
        if(!(playerData.getVisualRole() instanceof Vagabond vagabond))return;

        if(vagabond.getLastTeleportation() + 1000 * Vagabond.MOTIONLESS_TIME <= System.currentTimeMillis())return;

        Location from = e.getFrom();
        Location to = e.getTo();
        if(from.getX() != to.getX()
                || from.getY() != to.getY()
                || from.getZ() != to.getZ()){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void itemMergeEvent(ItemMergeEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void entityPickupItemEvent(EntityPickupItemEvent e){
        if(!(e.getEntity() instanceof Player p))return;
        if(p.isOp() && p.getGameMode() == GameMode.CREATIVE)return;

        PlayerData playerData = this.playerManager.getData(p.getUniqueId());
        e.setCancelled(true);

        if(playerData.isSpectator())return;

        Item item = e.getItem();
        ItemStack itemStack = item.getItemStack();
        item.remove();

        if(itemStack.getType() != Material.GOLD_INGOT) return;

        ItemStack goldItemStack = p.getInventory().getItem(GOLD_SLOT);

        int increase = 1;
        if(playerData.getVisualRole() instanceof Lucky){
            increase = increase + 1;
        }

        int amount = increase;
        if(goldItemStack != null && !(playerData.getVisualRole() instanceof Murder)){
            amount = goldItemStack.getAmount()+increase;
        }

        if(amount >= 10){
            if(amount > 10){
                goldItemStack.setAmount(goldItemStack.getAmount()-10);
            }else{
                p.getInventory().setItem(GOLD_SLOT,null);
            }
            playerData.getVisualRole().giveBow(p);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,2,1);
        }else{
            p.getInventory().setItem(GOLD_SLOT, new ItemStackBuilder(Material.GOLD_INGOT,amount)
                    .setName("§6§lOr")
                    .setLore("§7Obtenez 10 d'or","§7pour avoir un arc!")
                    .toItemStack());

            p.sendMessage("§a+"+increase+" or !");
            p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP,2,1);
        }
    }

    @EventHandler
    public void interactItem(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(this.isInCooldown(p))return;
        if(e.getItem() == null)return;

        if(this.gameManager.getGameState() != GameState.IN_GAME)return;

        this.cooldownInteractions.put(p.getUniqueId(), System.currentTimeMillis()+100);

        PlayerData playerData = this.playerManager.getData(p.getUniqueId());
        for(GameActionListener gameActionListener: this.gamesListeners){
            gameActionListener.playerInteractEvent(e,p,playerData, e.getItem());
        }
    }

    @EventHandler
    public void interactItem(InventoryClickEvent e){
        if(this.gameManager.getGameState() != GameState.IN_GAME)return;
        if(!(e.getWhoClicked() instanceof Player p))return;
        if(e.getCurrentItem() == null)return;
        if(this.isInCooldown(p))return;

        this.cooldownInteractions.put(p.getUniqueId(), System.currentTimeMillis()+100);

        PlayerData playerData = this.playerManager.getData(p.getUniqueId());
        for(GameActionListener gameActionListener: this.gamesListeners){
            gameActionListener.inventoryClickEvent(e,p,playerData, e.getCurrentItem());
        }

    }

    private boolean isInCooldown(Player p) {
        return this.cooldownInteractions.containsKey(p.getUniqueId()) &&
                (this.cooldownInteractions.get(p.getUniqueId()) > System.currentTimeMillis());
    }

}
