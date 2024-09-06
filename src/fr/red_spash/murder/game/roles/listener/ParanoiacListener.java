package fr.red_spash.murder.game.roles.listener;

import fr.red_spash.murder.game.events.GameActionListener;
import fr.red_spash.murder.players.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ParanoiacListener extends GameActionListener {

    private Player getTargetPlayer(Player player) {
        Vector cursorDirection = player.getEyeLocation().getDirection();

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player.equals(target)) continue; // Skip the player who is checking

            // Get the direction from player to target
            Vector playerToTarget = target.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector());

            // Check if the target is within the maximum distance and in the same direction as the cursor
            if (playerToTarget.length() <= 10 && playerToTarget.normalize().dot(cursorDirection) > 0.99) {
                // Player is within range and in the direction of the cursor
                // Do whatever you want with the target player here
                player.sendMessage("Player " + target.getName() + " is within your cursor!");
                return player;
            }
        }
        return null;

    }
}
