package fr.red_spash.murder;

import fr.red_spash.murder.commands.*;
import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.event.ChatListener;
import fr.red_spash.murder.event.ServerListener;
import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.events.DetectiveListener;
import fr.red_spash.murder.game.events.GameListener;
import fr.red_spash.murder.game.events.MurderListener;
import fr.red_spash.murder.game.scoreboard.ScoreboardTask;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Murder extends JavaPlugin {

    private EditWorld editWorld;
    private GameManager gameManager;

    @Override
    public void onEnable(){
        PlayerManager playerManager = new PlayerManager(this);
        BowOnGroundListener bowOnGroundListener = new BowOnGroundListener(playerManager, this);

        DeathManager deathManager = new DeathManager(this, playerManager, bowOnGroundListener);
        this.initializeOnlinePlayers(playerManager);

        MapManager mapManager = new MapManager(this);
        this.gameManager = new GameManager(this, mapManager, playerManager, bowOnGroundListener, deathManager);

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new ServerListener(this.gameManager),this);
        pm.registerEvents(new ChatListener(),this);
        pm.registerEvents(new DetectiveListener(this.gameManager, this),this);
        pm.registerEvents(new MurderListener(this.gameManager, this),this);
        pm.registerEvents(new GameListener(),this);
        pm.registerEvents(bowOnGroundListener,this);

        this.editWorld = new EditWorld(this, mapManager);
        getCommand("editWorld").setExecutor(this.editWorld);
        getCommand("saveWorld").setExecutor(new SaveWorld(this, this.editWorld));
        getCommand("spawns").setExecutor(new Spawns(this, this.editWorld));
        getCommand("start").setExecutor(new StartGame(this.gameManager));
        getCommand("teleportTo").setExecutor(new TeleportTo(mapManager));

        Bukkit.getServer().getScheduler().runTaskTimer(this, new ScoreboardTask(this.gameManager), 0, 20);

        Bukkit.getConsoleSender().sendMessage("§c§lRed_Murder prêt !");

    }

    @Override
    public void onDisable(){
        this.editWorld.deleteAllWorlds();
        this.gameManager.stopGame();
    }

    private void initializeOnlinePlayers(PlayerManager playerManager) {
        for(Player p : Bukkit.getOnlinePlayers()){
            playerManager.insertPlayer(p);
        }
    }
}
