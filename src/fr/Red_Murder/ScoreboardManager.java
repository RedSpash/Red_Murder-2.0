package fr.Red_Murder;

import fr.Red_Murder.event.Murder;
import fr.Red_Murder.roles.RoleManager;
import fr.Red_Murder.tasks.MurderGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;

import static fr.Red_Murder.event.Murder.Roles;

public class ScoreboardManager extends BukkitRunnable {

    @Override
    public void run() {
        if(Bukkit.getOnlinePlayers().size() == 0){
            return;
        }else{
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getScoreboard() == null){
                    ScoreboardManager.setScoreboard(p);
                }
                updateScoreboard(p);
            }
        }
    }

    public static void setScoreboard(Player p){
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Team team = board.registerNewTeam("teamName");
        team.setNameTagVisibility(NameTagVisibility.NEVER);
        team.addEntry(p.getName());
        for(Player target : Bukkit.getOnlinePlayers()){
            team.addEntry(target.getName());
        }





        Objective obj = board.registerNewObjective("§5§lSEMI-RP", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score14 = obj.getScore("§b§l▪ PARTIE:");
        //                                   Temps
        //                                   Detective
        //                                   Innocent
        Score score10 = obj.getScore("§f§2 ");
        Score score9 = obj.getScore("§9§l▪ RÔLE:");
        //                                   Role
        //                                   Kills
        Score score1 = obj.getScore("§c§l§n§2§5§8§4§1");
        Score score0 = obj.getScore("§c§ldev by Red_Spash");


        score14.setScore(14);



        score10.setScore(10);
        score9.setScore(9);


        score0.setScore(0);
        score1.setScore(1);


        Team time = board.registerNewTeam("time");
        time.addEntry(ChatColor.BLACK + "" + ChatColor.RED);
        time.setPrefix(ChatColor.WHITE+"≫ Temps: ");
        time.setSuffix(MurderGame.get_time_remaining()+"");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.RED).setScore(13);

        Team detective = board.registerNewTeam("detective");
        detective.addEntry(ChatColor.BLACK + "" + ChatColor.BLUE);
        detective.setPrefix(ChatColor.WHITE+"≫ Detective: ");
        if(Murder.BowLocation == null){
            detective.setSuffix("§aEn vie");
        }else{
            detective.setSuffix("§cAu sol");
        }
        obj.getScore(ChatColor.BLACK + "" + ChatColor.BLUE).setScore(12);

        Team role = board.registerNewTeam("role");
        role.addEntry(ChatColor.BLACK + "" + ChatColor.LIGHT_PURPLE);
        role.setPrefix(ChatColor.WHITE+"≫ Rôle: ");
        if(Roles.containsKey(p.getName())){
            RoleManager r = Roles.get(p.getName());
            role.setSuffix(r.get_role_color()+r.get_role_name());
        }else{
            role.setSuffix("§7Spectateur");
        }

        obj.getScore(ChatColor.BLACK + "" + ChatColor.LIGHT_PURPLE).setScore(8);

        p.setScoreboard(board);

    }

    public static void updateScoreboard(Player p){
        Scoreboard board = p.getScoreboard();
        board.getTeam("time").setSuffix(MurderGame.get_time_remaining()+"");

        String role;
        if(Roles.containsKey(p.getName())){
            RoleManager r = Roles.get(p.getName());
            role = r.get_role_color()+r.get_role_name();
        }else{
            role = "§7Spectateur";
        }
        board.getTeam("role").setSuffix(role);

        String detective;
        if(Murder.BowLocation == null){
            detective = "§aEn vie";
        }else{
            detective = "§cAu sol";
        }
        board.getTeam("detective").setSuffix(detective);

        if(board.getTeam("teamName") == null){
            Team team = board.registerNewTeam("teamName");
            team.setNameTagVisibility(NameTagVisibility.NEVER);
            team.addEntry(p.getName());
            for(Player target : Bukkit.getOnlinePlayers()){
                team.addEntry(target.getName());
            }
        }

    }


}
