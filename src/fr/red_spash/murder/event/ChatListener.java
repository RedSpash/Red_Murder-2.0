package fr.red_spash.murder.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {


    @EventHandler
    public void asyncPlayerChatEvent(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if (p.isOp()){
            e.setMessage(e.getMessage().replace("&","§"));
        }
        e.setFormat(getPrefix(p)+p.getName()+" §7>>>§f "+e.getMessage());
    }

    public static String getPrefix(Player p){
        if(p.getName().equals("Red_Spash")){
            return "§c[Développeur] ";
        }else{
            return "§6";
        }
    }



}
