package fr.red_spash.murder.game.events;

import fr.red_spash.murder.players.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class GameActionListener {

    public void playerInteractEvent(PlayerInteractEvent e, Player p, PlayerData playerData, ItemStack itemStack){

    }
    public void inventoryClickEvent(InventoryClickEvent e, Player p, PlayerData playerData, ItemStack itemStack){

    }

}
