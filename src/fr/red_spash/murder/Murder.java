package fr.red_spash.murder;

import fr.red_spash.murder.commands.*;
import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.event.ChatListener;
import fr.red_spash.murder.event.ServerListener;
import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.game.events.*;
import fr.red_spash.murder.game.roles.RoleConfiguration;
import fr.red_spash.murder.game.roles.listener.*;
import fr.red_spash.murder.game.roles.tasks.SpyTask;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.spawn.MapsManagerListener;
import fr.red_spash.murder.spawn.RoleManagerListener;
import fr.red_spash.murder.spawn.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Murder extends JavaPlugin {
    private EditWorld editWorld;
    private GameManager gameManager;
    private SpawnManager spawnManager;
    private RoleConfiguration roleConfiguration;
    private MapManager mapManager;
    private PlayerManager playerManager;
    private BowOnGroundListener bowOnGroundListener;
    private DeathManager deathManager;
    private ArrayList<GameActionListener> gameActionListeners;
    private RoleManagerListener roleManagerListener;
    private SpyListener spyListener;

    @Override
    public void onEnable(){
        this.createMainsManagers();
        this.createRolesListeners();
        this.registerEventListener();
        this.registerCommands();

        Bukkit.getServer().getScheduler().runTaskTimer(this, new SpyTask(this.playerManager, this.spyListener), 0, 1);
        Bukkit.getConsoleSender().sendMessage("§c§lRed_Murder prêt !");

    }

    private void createMainsManagers() {
        this.roleConfiguration = new RoleConfiguration();
        this.spawnManager = new SpawnManager();

        this.mapManager = new MapManager(this, this.spawnManager);
        this.playerManager = new PlayerManager(this, this.spawnManager);
        this.bowOnGroundListener = new BowOnGroundListener(playerManager, this);
        this.initializeOnlinePlayers(playerManager);

        this.gameManager = new GameManager(this, mapManager, playerManager, bowOnGroundListener, roleConfiguration, this.spawnManager);
        this.deathManager = new DeathManager(this, this.gameManager);
    }

    private void createRolesListeners() {
        this.gameActionListeners = new ArrayList<>();
        this.spyListener = new SpyListener(playerManager, this);
        gameActionListeners.add(spyListener);
        gameActionListeners.add(new ElectricianListener(this.playerManager));
        gameActionListeners.add(new VagabondListener(this));
    }

    private void registerCommands() {
        this.editWorld = new EditWorld(this, this.mapManager, this.spawnManager);
        getCommand("editWorld").setExecutor(this.editWorld);
        getCommand("saveWorld").setExecutor(new SaveWorld(this, this.editWorld, this.spawnManager));
        getCommand("spawns").setExecutor(new Spawns(this, this.editWorld));
        getCommand("start").setExecutor(new StartGame(this.gameManager));
        getCommand("teleportTo").setExecutor(new TeleportTo(this.mapManager));
    }

    private void registerEventListener() {
        this.roleManagerListener = new RoleManagerListener(roleConfiguration);

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new ServerListener(this.gameManager, deathManager, this.spawnManager),this);
        pm.registerEvents(new ChatListener(),this);
        pm.registerEvents(new MapsManagerListener(this.mapManager, this.gameManager),this);
        pm.registerEvents(roleManagerListener,this);
        pm.registerEvents(new DetectiveListener(playerManager, deathManager, this),this);
        pm.registerEvents(new RolesListener(this.gameManager,gameActionListeners),this);
        pm.registerEvents(new MurderListener(this.gameManager, deathManager, this),this);
        pm.registerEvents(new GameListener(),this);
        pm.registerEvents(bowOnGroundListener,this);
    }

    @Override
    public void onDisable(){
        this.editWorld.deleteAllWorlds();
        if(this.gameManager.getGameState() != GameState.WAITING){
            this.gameManager.resetGame();
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            this.spawnManager.teleportSpawn(player);
        }
    }

    private void initializeOnlinePlayers(PlayerManager playerManager) {
        for(Player p : Bukkit.getOnlinePlayers()){
            playerManager.insertPlayer(p);
        }
    }
}
