package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.GameManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartTimer implements Runnable{
    private final GameManager gameManager;
    private BukkitTask bukkitTask;
    private final List<String> countDown = new ArrayList<>(Arrays.asList("❶","❷","❸","❹","❺","❻","❼","❽","❾","❿"));
    private int seconds = 17;;

    public StartTimer(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        if(seconds == 0) {
            this.gameManager.startGame();
            this.bukkitTask.cancel();
            for(Player p : Bukkit.getOnlinePlayers()){
                p.sendTitle("§a§lLancement de la partie!", "§aBonne chance",0,20,3);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 2,1.5F);
            }
            return;
        }
        if(seconds <=5){
            int toAdd = seconds * 51;
            Color color = new Color(255-toAdd,toAdd,0);
            for(Player p : Bukkit.getOnlinePlayers()){
                p.sendTitle(ChatColor.of(color) +this.countDown.get(seconds-1), "§aDébut de la partie...",0,20,3);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,1);
            }
        }
        if (seconds == 10 || seconds == 7 || seconds <= 5 || seconds % 10 == 0) {
            Bukkit.broadcastMessage("§aDémarrage de la partie dans "+seconds+" secondes...");
            for(Player p : Bukkit.getOnlinePlayers()){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT,1,1);
            }
        }
        seconds = seconds - 1;
    }

    public void setBukkitTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }
}
