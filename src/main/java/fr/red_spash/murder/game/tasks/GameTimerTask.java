package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.roles.*;
import fr.red_spash.murder.game.roles.concrete_roles.Detective;
import fr.red_spash.murder.game.roles.concrete_roles.Innocent;
import fr.red_spash.murder.game.roles.concrete_roles.Murder;
import fr.red_spash.murder.game.roles.concrete_roles.Schizophrenic;
import fr.red_spash.murder.game.scoreboard.ScoreboardLines;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class GameTimerTask implements Runnable{

    private static final int SCHIZOPHRENIC_TIMER = 60;
    public static final int MAX_TIME = 60*7;
    private static final int COMPASS_TIME = MAX_TIME - 60;

    private final GameManager gameManager;
    private final PlayerManager playerManager;
    private final BukkitTask bukkitTask;
    private final ScoreboardLines scoreboardLines;
    private int time = 0;

    public GameTimerTask(GameManager gameManager, Plugin main, ScoreboardLines scoreboardLines) {
        this.gameManager = gameManager;
        this.playerManager = gameManager.getPlayerManager();
        this.scoreboardLines = scoreboardLines;

        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(main, this, 20,20);
    }

    @Override
    public void run() {
        if(this.time == 5) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = this.playerManager.getData(p);
                Role role = playerData.getVisualRole();

                role.setDiscovered(true);
                p.sendTitle(role.getMinecraftRoleColor() + "ROLE: §l" + role.getName().toUpperCase(), "§e" + role.shortDescription(), 10, 20 * 5, 10);
                p.sendMessage("\n" + role.getMinecraftRoleColor() + "§l" + role.getName().toUpperCase() + "\n§7" + role.getDescription() + "\n§f");
                p.playSound(p.getLocation(), role.getSound(), 5, 1);
                role.giveItems(p);
                this.scoreboardLines.updateShow(p);
            }
        } else if (this.time == SCHIZOPHRENIC_TIMER) {
            for(PlayerData playerData : this.playerManager.getAllPlayerData()){
                if(playerData.getTrueRole() != null &&
                        (playerData.getTrueRole() instanceof Schizophrenic schizophrenic) && !playerData.isSpectator()){

                    Player p = Bukkit.getPlayer(playerData.getUUID());
                    if(p!= null && p.isOnline()){
                        Role role = new Murder();
                        if(Utils.generateRandomNumber(0,1) == 0){
                            role = new Detective();
                        }

                        schizophrenic.setSubRole(role);
                        role.giveItems(p);
                        p.sendTitle(role.getMinecraftRoleColor()+role.getName(),role.shortDescription(),10,20*3,10);
                        Bukkit.broadcastMessage(role.getMinecraftRoleColor()+"Le "+schizophrenic.getName()+" vient de passer "+schizophrenic.getSubRole().getName()+"!");
                        for(Player pl: Bukkit.getOnlinePlayers()){
                            if(role instanceof Murder){
                                pl.playSound(pl.getLocation(), Sound.AMBIENT_CAVE,2,0);
                                pl.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,3*20,2,false,false,false));
                            }
                        }
                    }
                }
            }
            this.gameManager.checkEnd();
        }else if(this.time >= MAX_TIME) {
            this.gameManager.stopGame(new Innocent());
            this.bukkitTask.cancel();
            return;
        } else if (this.time == COMPASS_TIME) {
            for(Player player : Bukkit.getOnlinePlayers()){
                PlayerData playerData = this.playerManager.getData(player.getUniqueId());
                if(!playerData.isSpectator()){
                    if(playerData.getVisualRole() instanceof Murder){

                    }
                }
                player.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK,1,1);
            }
        }else if(this.time == MAX_TIME-25){
            for(Player player : Bukkit.getOnlinePlayers()){
                PlayerData playerData = this.playerManager.getData(player.getUniqueId());
                if(!playerData.isSpectator()){
                   player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,20*30,1,false,false,false)) ;
                }
                player.playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,1,1);
            }
            Bukkit.broadcastMessage("§aAffichage des joueurs en surbrillance !");
        }
        this.time = this.time + 1;
    }

    public int getMaxTime() {
        return MAX_TIME;
    }

    public int getRemainingSeconds(){
        return MAX_TIME - time;
    }

    public int getTime() {
        return time;
    }

    public void stop() {
        this.bukkitTask.cancel();
    }
}
