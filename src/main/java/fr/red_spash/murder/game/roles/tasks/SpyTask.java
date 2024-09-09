package fr.red_spash.murder.game.roles.tasks;

import fr.red_spash.murder.game.roles.concrete_roles.Spy;
import fr.red_spash.murder.game.roles.listener.SpyListener;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpyTask implements Runnable {

    private final PlayerManager playerManager;
    private final SpyListener spyListener;

    public SpyTask(PlayerManager playerManager, SpyListener spyListener){
        this.playerManager = playerManager;
        this.spyListener = spyListener;
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
                        this.spyListener.disableInvisibility(p, spy);
                        p.getInventory().setItem(Spy.SPY_SLOT, null);
                    }else{
                        StringBuilder actionBarMessage = new StringBuilder();
                        double percentage = spy.getPower() * 100 / Spy.SPY_INVISIBILITY_TIME;

                        for (int i = 1; i <= 50; i++) {
                            String color = "§c";

                            if (i * 2 <= percentage) {
                                color = "§a";
                            }

                            actionBarMessage.insert(0, color + "|");
                        }
                        String seconds;
                        if(spy.getPower() >= 10){
                            seconds = Utils.addZero(Utils.round(spy.getPower(),2),5);
                        }else{
                            seconds = Utils.addZero(Utils.round(spy.getPower(),2),4);
                        }
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lTemps restant: "+actionBarMessage+" §7("+seconds+" secondes)"));
                    }
                }

            }
        }
    }
}
