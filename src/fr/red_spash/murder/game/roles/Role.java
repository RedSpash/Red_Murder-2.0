package fr.red_spash.murder.game.roles;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public interface Role {
    String getRoleColor();
    String getName();
    String shortDescription();
    String getDescription();
    void giveItems(Player p);
    Sound getSound();
}
