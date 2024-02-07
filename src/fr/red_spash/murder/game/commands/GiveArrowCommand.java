package fr.red_spash.murder.game.commands;

import fr.red_spash.murder.game.roles.Detective;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveArrowCommand extends Command {
    public GiveArrowCommand(Player p) {
        super(p);
    }

    @Override
    public void execute() {
        ItemStack item = p.getInventory().getItem(Detective.SLOT_ARROW);
        if(item == null || item.getAmount() == 0){
            p.getInventory().setItem(Detective.SLOT_ARROW, new ItemStack(Material.ARROW));
        }else{
            item.setAmount(item.getAmount()+1);
        }
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,2);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§a§lRechargement de l'arc terminé!"));
    }
}
