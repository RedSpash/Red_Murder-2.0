package fr.red_spash.murder.game;

import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.game.roles.Detective;
import fr.red_spash.murder.game.roles.Innocent;
import fr.red_spash.murder.game.roles.Murder;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.tasks.GameTimerTask;
import fr.red_spash.murder.game.tasks.StartTimer;
import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;

public class GameManager {

    private final MapManager mapManager;
    private final Plugin main;
    private final PlayerManager playerManager;
    private final BowOnGroundListener bowOnGroundListener;
    private final DeathManager deathManager;
    private GameMap actualMap;
    private GameTimerTask gameTimer;
    private GameState gameState;

    public GameManager(Plugin main, MapManager mapManager, PlayerManager playerManager, BowOnGroundListener bowOnGroundListener, DeathManager deathManager) {
        this.mapManager = mapManager;
        this.main = main;
        this.playerManager = playerManager;
        this.bowOnGroundListener = bowOnGroundListener;
        this.actualMap = null;
        this.deathManager = deathManager;
        this.gameState = GameState.WAITING;
    }

    public void startPreGame(GameMap gameMap){
        World world = gameMap.loadWorld();
        if(world == null){
            Bukkit.broadcastMessage("§c§lImpossible de démarrer le murder avec le carte "+gameMap.getName());
            return;
        }
        this.actualMap = gameMap;
        for(Player p : Bukkit.getOnlinePlayers()){
            p.teleport(gameMap.getSpawnLocation());
            p.sendTitle("§a"+gameMap.getName(),"§eLe murder va commencer...",10,20*3,20);
            p.playSound(p.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN,1,2);
            p.setGameMode(GameMode.ADVENTURE);
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
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Murder());
        roles.add(new Detective());

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
        }

        this.gameTimer = new GameTimerTask(this, this.main);
        this.gameState = GameState.IN_GAME;
    }

    public void checkEnd(){
        int murderRemainings = 0;
        int alive = 0;
        for(PlayerData playerData: this.playerManager.getAllPlayerData()){
            Role role = playerData.getRole();
            if(role != null && !playerData.isSpectator()){
                if(role.isMurder()){
                    murderRemainings = murderRemainings + 1;
                }
                alive = alive + 1;
            }
        }

        if(murderRemainings == alive){
            Bukkit.broadcastMessage("murders");
            this.stopGame();
        } else if (murderRemainings == 0) {
            Bukkit.broadcastMessage("innocents");
            this.stopGame();
        }
    }

    public void stopGame() {
        this.gameState = GameState.END_GAME;
        this.actualMap.deleteWorld();
        this.gameTimer.stop();
        this.playerManager.resetData();
        this.playerManager.resetPlayers();
        this.bowOnGroundListener.clearBowsLocations();
        this.gameState = GameState.WAITING;
    }

    public DeathManager getDeathManager() {
        return deathManager;
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
                Role role = playerData.getRole();
                if(role != null && !role.isMurder()){
                    amount = amount + 1;
                }
            }
        }
        return amount;
    }
}
