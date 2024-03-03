package fr.red_spash.murder;

import fr.red_spash.murder.commands.*;
import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.event.ChatListener;
import fr.red_spash.murder.event.ServerListener;
import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.game.events.DetectiveListener;
import fr.red_spash.murder.game.events.GameListener;
import fr.red_spash.murder.game.events.MurderListener;
import fr.red_spash.murder.game.events.RolesListener;
import fr.red_spash.murder.game.scoreboard.ScoreboardLines;
import fr.red_spash.murder.game.scoreboard.ScoreboardTask;
import fr.red_spash.murder.game.tasks.SpyTask;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Murder extends JavaPlugin {
    public static final Location SPAWN = new Location(Bukkit.getWorld("world"),-77.5, 105, -59.5, 180, 0);
    private EditWorld editWorld;
    private GameManager gameManager;

    @Override
    public void onEnable(){
        MapManager mapManager = new MapManager(this);
        PlayerManager playerManager = new PlayerManager(this);
        BowOnGroundListener bowOnGroundListener = new BowOnGroundListener(playerManager, this);
        this.initializeOnlinePlayers(playerManager);

        this.gameManager = new GameManager(this, mapManager, playerManager, bowOnGroundListener);
        DeathManager deathManager = new DeathManager(this, this.gameManager);

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new ServerListener(this.gameManager, deathManager),this);
        pm.registerEvents(new ChatListener(),this);
        pm.registerEvents(new DetectiveListener(playerManager, deathManager, this),this);
        pm.registerEvents(new RolesListener(this.gameManager),this);
        pm.registerEvents(new MurderListener(this.gameManager, deathManager, this),this);
        pm.registerEvents(new GameListener(),this);
        pm.registerEvents(bowOnGroundListener,this);

        this.editWorld = new EditWorld(this, mapManager);
        getCommand("editWorld").setExecutor(this.editWorld);
        getCommand("saveWorld").setExecutor(new SaveWorld(this, this.editWorld));
        getCommand("spawns").setExecutor(new Spawns(this, this.editWorld));
        getCommand("start").setExecutor(new StartGame(this.gameManager));
        getCommand("teleportTo").setExecutor(new TeleportTo(mapManager));

        Bukkit.getServer().getScheduler().runTaskTimer(this, new SpyTask(playerManager), 0, 1);

        Bukkit.getConsoleSender().sendMessage("§c§lRed_Murder prêt !");

    }

    @Override
    public void onDisable(){
        this.editWorld.deleteAllWorlds();
        if(this.gameManager.getGameState() != GameState.WAITING){
            this.gameManager.resetGame();
        }
    }

    private void initializeOnlinePlayers(PlayerManager playerManager) {
        for(Player p : Bukkit.getOnlinePlayers()){
            playerManager.insertPlayer(p);
        }
    }
}
