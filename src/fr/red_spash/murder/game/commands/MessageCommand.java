package fr.red_spash.murder.game.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MessageCommand extends Command {
    private final Sound sound;
    private final String message;

    public MessageCommand(Player p, String message, Sound sound) {
        super(p);
        this.sound = sound;
        this.message = message;
    }

    public MessageCommand(Player p, String message) {
        this(p,message,null);
    }



    @Override
    public void execute() {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        p.sendMessage(message);
        if(this.sound != null){
            p.playSound(p.getLocation(), this.sound,1,1);
        }
    }
}
