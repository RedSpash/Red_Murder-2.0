package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.roles.concrete_roles.Spy;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

public class SpyTask implements Runnable {

    private final PlayerManager playerManager;

    public SpyTask(PlayerManager playerManager){
        this.playerManager = playerManager;
    }

    @Override
    public void run() {
        for(PlayerData playerData : this.playerManager.getAllPlayerData()){
            if(playerData.getVisualRole() instanceof Spy spy
                    && !playerData.isSpectator()
                    && spy.isUsingIsPower()){
                spy.removePower(0.05);
                Player p = Bukkit.getPlayer(playerData.getUUID());
                if(p != null){
                    if(spy.getPower() <= 0){
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lVous n'avez plus d'invisibilité !"));
                        p.sendMessage("§c§lVous n'avez plus d'invisibilité !");
                        spy.disableInvisibility(p);
                        p.getInventory().setItem(Spy.SPY_SLOT, null);
                    }else{
                        String remaining = Utils.round(spy.getPower(),2)+"";
                        if(remaining.split("\\.")[1].length() == 1){
                            remaining = remaining + "0";
                        }
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lIl vous reste "+ remaining+" secondes!"));
                    }
                }

            }
        }
    }
}
