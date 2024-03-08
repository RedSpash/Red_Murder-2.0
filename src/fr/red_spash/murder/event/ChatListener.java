package fr.red_spash.murder.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    private final HashMap<UUID, Long> chatCooldown = new HashMap<>();

    @EventHandler
    public void asyncPlayerChatEvent(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if (p.isOp()){
            e.setMessage(e.getMessage().replace("&","§"));
        }else{
            if(this.chatCooldown.containsKey(p.getUniqueId()) &&
                    (this.chatCooldown.get(p.getUniqueId()) > System.currentTimeMillis())){
                e.setCancelled(true);
                p.sendMessage("§cVous devez attendre avant d'envoyer un nouveau message!");
                return;
            }
            this.chatCooldown.put(p.getUniqueId(),System.currentTimeMillis()+1000);
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
