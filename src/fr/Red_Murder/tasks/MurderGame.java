package fr.Red_Murder.tasks;

import fr.Red_Murder.Main;
import fr.Red_Murder.ScoreboardManager;
import fr.Red_Murder.event.Murder;
import fr.Red_Murder.roles.RoleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.github.paperspigot.Title;

import java.text.DecimalFormat;

import static fr.Red_Murder.ScoreboardManager.updateScoreboard;

public class MurderGame extends BukkitRunnable {
    public static int time = 8*60;
    private static int TimeSchizo = 0;

    @Override
    public void run() {
        if(time == 8*60){
            TimeSchizo = Main.random_number(1,45);
        }
        if(Murder.state != Murder.State.Playing){
            time = 8*60;
            cancel();
            return;
        }
        if(time == 420-TimeSchizo && Murder.Schizo != null){
            if(Main.random_number(0,1) == 0){
                Bukkit.broadcastMessage("§cDans un élan de folie, le schizophrène devient murder !");
                Player p = Bukkit.getPlayerExact(Murder.Schizo);
                p.sendTitle(new Title("§c§lVous êtes murder","§cVotre but tuer tout le monde !",0,2*20,20));
                if(Murder.Roles.containsKey(p.getName())){
                    RoleManager roleManager = Murder.Roles.get(p.getName());
                    roleManager.set_sub_roletype(Murder.Role.Murder);
                    roleManager.give_main_item();
                }
                for(Player pl :Bukkit.getOnlinePlayers()){
                    pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL,1,1);
                }
            }else{
                Bukkit.broadcastMessage("§aLe schizophrène devient détective !");
                Player p = Bukkit.getPlayerExact(Murder.Schizo);
                p.sendTitle(new Title("§a§lVous êtes Détective","§aVotre but est de sauver les innocents !",0,2*20,20));
                if(Murder.Roles.containsKey(p.getName())){
                    RoleManager roleManager = Murder.Roles.get(p.getName());
                    roleManager.set_sub_roletype(Murder.Role.Detective);
                    roleManager.give_main_item();
                }
                for(Player pl :Bukkit.getOnlinePlayers()){
                    pl.playSound(pl.getLocation(), Sound.ORB_PICKUP,2,1);
                }
            }
            Murder.check_end_game();
        }

        if(time % 5 == 0){
            int item = 0;
            for(Entity entity : Bukkit.getWorld("world").getEntities()){
                if(entity instanceof Item){
                    item++;
                }
            }
            if(item <= 30){
                for(int i =0; i<= 5;i++){
                    Murder.Spawn_Gold();
                }
            }
        }

        time --;




    }


    public static String get_time_remaining() {
        if(Murder.state != Murder.State.Waiting){
            return (int)(time/60)+"m"+time%60+"s";
        }else{
            return "-";
        }

    }
}
