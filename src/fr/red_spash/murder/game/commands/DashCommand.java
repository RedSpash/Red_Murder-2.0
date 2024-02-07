package fr.red_spash.murder.game.commands;

import fr.red_spash.murder.game.roles.Murder;
import fr.red_spash.murder.players.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DashCommand extends Command {
    private final PlayerData playerData;
    private final Murder murder;

    public DashCommand(Player p, PlayerData playerData, Murder murder) {
        super(p);
        this.playerData = playerData;
        this.murder = murder;
    }

    @Override
    public void execute() {
        this.murder.setDash(true);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP,1,2);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lDash prêt!"));
    }
}
