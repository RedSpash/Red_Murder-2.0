package fr.red_spash.murder.game.roles;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemTrigger {

    void triggerAction(Player p, ItemStack itemStack);
}
