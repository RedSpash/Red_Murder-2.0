package fr.red_spash.murder;

import fr.red_spash.murder.commands.*;
import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.event.ChatListener;
import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.events.DetectiveListener;
import fr.red_spash.murder.game.events.MurderListener;
import fr.red_spash.murder.maps.MapManager;
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
        BowOnGroundListener bowOnGroundListener = new BowOnGroundListener();
        PlayerManager playerManager = new PlayerManager(this, bowOnGroundListener);
        this.initializeOnlinePlayers(playerManager);

        MapManager mapManager = new MapManager(this);
        this.gameManager = new GameManager(this, mapManager, playerManager, bowOnGroundListener);
        bowOnGroundListener.setGameManager(this.gameManager, this);

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new ChatListener(),this);
        pm.registerEvents(new DetectiveListener(playerManager, this, gameManager),this);
        pm.registerEvents(new MurderListener(playerManager, this, gameManager),this);
        pm.registerEvents(bowOnGroundListener,this);

        this.editWorld = new EditWorld(this, mapManager);
        getCommand("editWorld").setExecutor(this.editWorld);
        getCommand("saveWorld").setExecutor(new SaveWorld(this, this.editWorld));
        getCommand("spawns").setExecutor(new Spawns(this, mapManager, this.editWorld));
        getCommand("start").setExecutor(new StartGame(this.gameManager));
        getCommand("teleportTo").setExecutor(new TeleportTo());

        Bukkit.getConsoleSender().sendMessage("§c§lRed_Murder prêt !");

    }

    @Override
    public void onDisable(){
        editWorld.deleteAllWorlds();
        this.gameManager.stopGame();
    }

    private void initializeOnlinePlayers(PlayerManager playerManager) {
        for(Player p : Bukkit.getOnlinePlayers()){
            playerManager.insertPlayer(p);
        }
    }
}
