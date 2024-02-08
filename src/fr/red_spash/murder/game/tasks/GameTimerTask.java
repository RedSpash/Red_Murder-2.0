package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class GameTimerTask implements Runnable{

    private final GameManager gameManager;
    public static final int MAX_TIME = 60*5;
    private final PlayerManager playerManager;
    private final BukkitTask bukkitTask;
    private int time = -1;

    public GameTimerTask(GameManager gameManager, Plugin main) {
        this.gameManager = gameManager;
        this.playerManager = gameManager.getPlayerManager();
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(main, this, 20,20);
    }

    @Override
    public void run() {
        this.time = this.time + 1;

        if(this.time == 5){
            for(Player p : Bukkit.getOnlinePlayers()){
                PlayerData playerData = this.playerManager.getData(p);
                Role role = playerData.getRole();

                p.sendTitle(role.getRoleColor()+"ROLE: §l"+role.getName().toUpperCase(),"§e"+role.shortDescription(),10,20*5,10);
                p.sendMessage("\n"+role.getRoleColor()+"§l"+role.getName().toUpperCase()+"\n§7"+role.getDescription()+"\n§f");
                p.playSound(p.getLocation(), role.getSound(), 5,1);

                role.giveItems(p);
            }
        }else if(this.time >= MAX_TIME){
            this.gameManager.stopGame();
            this.bukkitTask.cancel();
            return;
        }
    }

    public int getMaxTime() {
        return MAX_TIME;
    }

    public int getRemainingSeconds(){
        return this.MAX_TIME - time;
    }

    public int getTime() {
        return time;
    }

    public void stop() {
        this.bukkitTask.cancel();
    }
}
