package fr.red_spash.murder.event;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.game.scoreboard.ScoreboardLines;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.spawn.SpawnManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class ServerListener implements Listener {

    private final GameManager gameManager;
    private final PlayerManager playerManager;
    private final DeathManager deathManager;
    private final SpawnManager spawnManager;

    public ServerListener(GameManager gameManager, DeathManager deathManager, SpawnManager spawnManager) {
        this.gameManager = gameManager;
        this.playerManager = gameManager.getPlayerManager();
        this.deathManager = deathManager;
        this.spawnManager = spawnManager;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e){
        GameState gameState = this.gameManager.getGameState();
        Player p = e.getPlayer();
        String name = this.colorName(p);

        p.setPlayerListHeader("§f    §f    §f    §f    §f    §f    §f    §f    §f    §f    \n"+
                ChatColor.of(new Color(255,0,0))+"§lMurder\n"+
                "§f"
        );
        p.setPlayerListFooter("""
                §f
                §dDéveloppé par @Red_Spash
                §f
                """
        );

        boolean setSpectator = false;

        if(gameState == GameState.PRE_START){
            e.setJoinMessage(name+" §ase glisse dans la partie au dernier moment!");
        } else if (gameState == GameState.IN_GAME || gameState == GameState.END_GAME) {
            p.setPlayerListName(ScoreboardLines.REPLACED_NAME_ANONYMOUS);
            e.setJoinMessage("");
            p.teleport(this.gameManager.getActualMap().getSpawnLocation());
            setSpectator = true;
        }else{
            this.spawnManager.giveSpawnItems(p);
            e.setJoinMessage(name+" §arejoint le murder!");
        }
        if(playerManager.getData(p) == null){
            playerManager.insertPlayer(p);
        }
        if(setSpectator){
            this.deathManager.setSpectator(p);
        }

        playerManager.getData(p).setSpectator(setSpectator);

        PlayerData playerData = this.playerManager.getData(p);
        playerData.setScoreBoard();
    }

    private String colorName(Player p) {
        String name = "§6"+p.getName();
        if(p.isOp()){
            name = "§c"+p.getName();
        }
        return name;
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent e){
        GameState gameState = this.gameManager.getGameState();
        Player p = e.getPlayer();
        String name = this.colorName(p);
        PlayerData playerData = this.playerManager.getData(p);

        if (gameState == GameState.IN_GAME && playerData.getVisualRole() != null) {
            e.setQuitMessage("");
            if(!playerData.isSpectator()){
                this.deathManager.killPlayer(p,null,"§cUn "+playerData.getVisualRole().getName().toLowerCase()+" est mort d'une déconnexion!");
            }
        }else{
            e.setQuitMessage(name+" §cvient de quitter le murder!");
        }
    }
}
