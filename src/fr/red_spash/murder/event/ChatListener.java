package fr.red_spash.murder.event;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    private final HashMap<UUID, Long> chatCooldown = new HashMap<>();
    private final PlayerManager playerManager;
    private final GameManager gameManager;

    public ChatListener(PlayerManager playerManager, GameManager gameManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
    }

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
        PlayerData playerData = this.playerManager.getData(p.getUniqueId());
        String message = getPrefix(p)+p.getName()+" §7>>>§7 "+e.getMessage();
        e.setCancelled(true);
        if(playerData.isSpectator()){
            message = "§7[SPECTATEUR] "+message;
            for(Player pl : Bukkit.getOnlinePlayers()){
                PlayerData data = this.playerManager.getData(pl);
                if(data.isSpectator()){
                    pl.sendMessage(message);
                }
            }
        }else{
            if(this.gameManager.getGameState() == GameState.IN_GAME){
                for(Player pl : Bukkit.getOnlinePlayers()){
                    double distance = Utils.round(pl.getLocation().distance(p.getLocation()),2);
                    if(distance <= 20){
                        pl.sendMessage("§7["+ distance+"m]"+message);
                    }
                }
            }else{
                Bukkit.broadcastMessage(message);
            }
        }
    }

    public static String getPrefix(Player p){
        if(p.getName().equals("Red_Spash")){
            return "§c[Développeur] ";
        } else if (p.getName().equals("Abidex_")) {
            return "§c[Admin] ";
        } else{
            return "§6";
        }
    }
}
