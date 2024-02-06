package fr.red_spash.murder.game.commands;

import org.bukkit.entity.Player;

public abstract class Command {

    protected final Player p;

    Command(Player p) {
        this.p = p;
    }
     public abstract void execute();
}
