package fr.red_spash.murder.game.commands;

import fr.red_spash.murder.game.roles.concrete_roles.Murder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public class GiveSwordCommand extends Command {

    private final Murder murder;

    public GiveSwordCommand(Player p, Murder murder) {
        super(p);
        this.murder = murder;
    }

    @Override
    public void execute() {
        Item item = murder.getSwordOnGround();
        if(item != null){
            item.remove();
            murder.setSwordOnGround(null);
        }

        p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON,1,2);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lVous avez récupéré votre épée!"));
        p.getInventory().setItem(Murder.MURDER_SWORD_SLOT,Murder.MURDER_SWORD);
    }
}
