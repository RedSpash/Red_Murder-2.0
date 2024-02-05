package fr.Red_Murder.event;

import fr.Red_Murder.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class EventListener implements Listener {


    @EventHandler
    public void OnChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        String prefix = "";
        if (p.isOp()){
            e.setMessage(e.getMessage().replace("&","§"));
        }
        e.setFormat(getPrefix(p)+p.getName()+" §7>>>§f "+e.getMessage());

    }

    public static String getPrefix(Player p){
        if(p.isOp()){
            return "§c[OP] ";
        }else{
            return "§6";
        }
    }



}
