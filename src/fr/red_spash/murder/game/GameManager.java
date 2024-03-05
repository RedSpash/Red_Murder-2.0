package fr.red_spash.murder.game;

import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.game.roles.*;
import fr.red_spash.murder.game.roles.concrete_roles.*;
import fr.red_spash.murder.game.scoreboard.ScoreboardLines;
import fr.red_spash.murder.game.scoreboard.ScoreboardTask;
import fr.red_spash.murder.game.tasks.EndGameTask;
import fr.red_spash.murder.game.tasks.GameTimerTask;
import fr.red_spash.murder.game.tasks.GoldTask;
import fr.red_spash.murder.game.tasks.StartTimer;
import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.spawn.SpawnManager;
import fr.red_spash.murder.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager {

    private final MapManager mapManager;
    private final JavaPlugin main;
    private final PlayerManager playerManager;
    private final BowOnGroundListener bowOnGroundListener;
    private GameMap actualMap;
    private GameTimerTask gameTimer;
    private GameState gameState;
    private EndGameTask endGameTask;
    private final ScoreboardLines scoreboardLines;
    private GoldTask goldTask;
    private final RoleConfiguration roleConfiguration;
    private final SpawnManager spawnManager;

    public GameManager(JavaPlugin main, MapManager mapManager, PlayerManager playerManager, BowOnGroundListener bowOnGroundListener, RoleConfiguration roleConfiguration, SpawnManager spawnManager) {
        this.mapManager = mapManager;
        this.main = main;
        this.playerManager = playerManager;
        this.bowOnGroundListener = bowOnGroundListener;
        this.roleConfiguration = roleConfiguration;
        this.spawnManager = spawnManager;
        this.scoreboardLines = new ScoreboardLines(this);
        this.actualMap = null;
        this.gameState = GameState.WAITING;

        Bukkit.getServer().getScheduler().runTaskTimer(this.main, new ScoreboardTask(playerManager, this.scoreboardLines), 0, 20);
    }

    public void startPreGame(GameMap gameMap){
        World world = gameMap.loadWorld();
        if(world == null){
            Bukkit.broadcastMessage("§c§lImpossible de démarrer le murder avec le carte "+gameMap.getName());
            return;
        }
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        world.setTime(Utils.generateRandomNumber(0,24000));
        this.actualMap = gameMap;
        for(Player p : Bukkit.getOnlinePlayers()){
            p.teleport(gameMap.getSpawnLocation());
            p.sendTitle("§a"+gameMap.getName(),"§eLe murder va commencer...",10,20*5,20);
            p.playSound(p.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN,1,2);
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
        }
        StartTimer startTimer = new StartTimer(this);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(this.main, startTimer, 20L, 20L);
        startTimer.setBukkitTask(bukkitTask);

        this.gameState = GameState.PRE_START;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public void startGame() {
        List<Role> roles = new ArrayList<>(this.roleConfiguration.getRoles());

        while (roles.size() < Bukkit.getOnlinePlayers().size()){
            roles.add(new Innocent());
        }
        Collections.shuffle(roles);

        ArrayList<Location> spawns = new ArrayList<>(this.actualMap.getSpawnsLocation());

        this.playerManager.resetPlayers();

        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = playerManager.getData(p);
            playerData.setRole(roles.remove(0));
            p.teleport(spawns.remove(Utils.generateRandomNumber(0,spawns.size()-1)));
            if(spawns.isEmpty()){
                spawns = new ArrayList<>(this.actualMap.getSpawnsLocation());
            }
            p.setPlayerListName(ScoreboardLines.REPLACED_NAME_ANONYMOUS);
            for(Player pl : Bukkit.getOnlinePlayers()){
                p.showPlayer(pl);
            }
        }

        this.gameTimer = new GameTimerTask(this, this.main, this.scoreboardLines);
        this.gameState = GameState.IN_GAME;
        this.goldTask = new GoldTask(this, this.main);
    }

    public boolean checkEnd(){
        int murderRemainings = 0;
        int alive = 0;
        for(PlayerData playerData: this.playerManager.getAllPlayerData()){
            Role role = playerData.getVisualRole();
            if(role != null && !playerData.isSpectator()){
                if(role.isMurder()){
                    murderRemainings = murderRemainings + 1;
                }
                alive = alive + 1;
            }
        }

        if(murderRemainings == alive){
            this.stopGame(new Murder());
            return true;
        } else if (murderRemainings == 0) {
            this.stopGame(new Innocent());
            return true;
        }
        return false;
    }

    public void stopGame(){
        this.stopGame(null);
    }

    public void stopGame(Role winnerRole) {
        this.goldTask.stop();
        this.gameState = GameState.END_GAME;
        this.gameTimer.stop();
        this.playerManager.resetPlayers();
        this.bowOnGroundListener.clearBowsLocations();
        this.playerManager.resetCooldowns();
        this.endGameTask = new EndGameTask(this, winnerRole, this.main);
        this.playerManager.resetData();
    }

    public void resetGame(){
        if(this.endGameTask != null){
            this.endGameTask.stop();
        }
        this.actualMap.deleteWorld();
        this.bowOnGroundListener.clearBowsLocations();
        this.playerManager.resetPlayers();
        this.gameState = GameState.WAITING;
        for(Player p : Bukkit.getOnlinePlayers()){
            this.spawnManager.giveSpawnItems(p);
        }
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameTimerTask getGameTimer() {
        return gameTimer;
    }

    public BowOnGroundListener getBowOnGroundListener() {
        return bowOnGroundListener;
    }

    public int getRemainingInnocents() {
        int amount = 0;
        for(PlayerData playerData : this.playerManager.getAllPlayerData()){
            if(!playerData.isSpectator()){
                Role role = playerData.getVisualRole();
                if(role != null && !role.isMurder()){
                    amount = amount + 1;
                }
            }
        }
        return amount;
    }

    public GameMap getActualMap() {
        return actualMap;
    }
}
