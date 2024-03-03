package fr.red_spash.murder.game.commands;

import fr.red_spash.murder.game.roles.concrete_roles.Murder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CanThrowSwordCommand extends Command {
    private final Murder murder;

    public CanThrowSwordCommand(Player p, Murder murder) {
        super(p);
        this.murder = murder;
    }

    @Override
    public void execute() {
        this.murder.setThrowSword(true);

        super.p.playSound(super.p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,2);
        super.p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lVous pouvez maintenant lancer votre épée!"));
    }
}
