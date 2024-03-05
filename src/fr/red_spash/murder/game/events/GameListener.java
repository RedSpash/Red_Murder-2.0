package fr.red_spash.murder.game.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.ArrayList;

public class GameListener implements Listener {

    private final ArrayList<String> unLockedBlockName;

    public GameListener() {
        this.unLockedBlockName = new ArrayList<>();

        this.initializeUnLockedBlockName();
    }

    private void initializeUnLockedBlockName() {
        this.unLockedBlockName.add("tripwire");
        this.unLockedBlockName.add("door");
        this.unLockedBlockName.add("button");
        this.unLockedBlockName.add("pressure");
    }


    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDropItem(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDropItem(PlayerSwapHandItemsEvent e){
        Player p = e.getPlayer();
        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDropItem(FoodLevelChangeEvent e){
        e.setFoodLevel(20);
        e.setCancelled(true);
    }

    @EventHandler
    public void entity(HangingBreakByEntityEvent e){
        if(e.getEntity() instanceof Player p
                && (p.isOp() && p.getGameMode() == GameMode.CREATIVE)){
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void blockPlaceEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!p.isOp() || p.getGameMode() != GameMode.CREATIVE){
            Block block = e.getClickedBlock();
            if(block == null)return;
            String name = block.getType().toString().toLowerCase();
            for(String unLocked : this.unLockedBlockName){
                if(name.contains(unLocked)){
                    return;
                }
            }
            if(block.getType().isInteractable() || (!p.isSneaking() && p.getItemInUse() == null)){
                e.setCancelled(true);
            }
        }
    }
}
