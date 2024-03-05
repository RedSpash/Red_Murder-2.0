package fr.red_spash.murder.game.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ActionBarMessageCommand extends Command {
    private final String message;

    public ActionBarMessageCommand(Player p, String message) {
        super(p);
        this.message = message;
    }

    @Override
    public void execute() {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
