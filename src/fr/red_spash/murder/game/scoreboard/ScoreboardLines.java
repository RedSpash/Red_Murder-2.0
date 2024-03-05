package fr.red_spash.murder.game.scoreboard;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.game.tasks.GameTimerTask;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.awt.*;

public class ScoreboardLines {
    private final GameManager gameManager;
    public static final String REPLACED_NAME_ANONYMOUS = "§7§kAAAAAAAAAAAAAAA";
    private static final String SYMBOL = "➥";
    private final PlayerManager playerManager;

    public ScoreboardLines(GameManager gameManager) {
        this.gameManager = gameManager;
        this.playerManager = gameManager.getPlayerManager();
    }

    public void updateShow(Player p){
        this.setScoreBoard(this.playerManager.getData(p), p);
    }

    public void setScoreBoard(PlayerData playerData, Player p) {
        RedScoreBoard board = playerData.getScoreBoard();
        board.setLine(15,"§f");
        GameState gameState = this.gameManager.getGameState();

        switch (gameState){
            case WAITING -> {
                board.removeLine(14,13,12,11,10,9,8,7,6,5,4);
                board.setLine(3,"§d"+SYMBOL+" En attente du");
                board.setLine(2,"§d"+SYMBOL+" lancement de la partie");
            }
            case PRE_START -> {
                board.setLine(9,"§a"+SYMBOL+" §fCarte: §a"+this.gameManager.getActualMap().getName());
                board.setLine(8,"§f§l§r");
                board.setLine(7,"§a"+SYMBOL+" §fRôle: §caucun");
                board.setLine(6,"§f");
                board.setLine(5,"§a"+SYMBOL+" §fInnocents: §a"+this.gameManager.getRemainingInnocents());
                board.setLine(4,"§a"+SYMBOL+" §fTemps: §a"+ GameTimerTask.MAX_TIME+"sec");
                board.setLine(3,"§f");
                board.setLine(2,"§a"+SYMBOL+" §fDétective: §aEn vie");
            }
            case IN_GAME -> {
                board.setLine(9,"§a"+SYMBOL+" §fCarte: §a"+this.gameManager.getActualMap().getName());
                board.setLine(8,"§f§l§r");
                if(playerData.getVisualRole() != null && playerData.getVisualRole().isDiscovered()){
                    board.setLine(7,"§a"+SYMBOL+" §fRôle: "+playerData.getVisualRole().getMinecraftRoleColor()+playerData.getVisualRole().getName());
                }else{
                    board.setLine(7,"§a"+SYMBOL+" §fRôle: §caucun");
                }
                board.setLine(6,"§f");
                board.setLine(5,"§a"+SYMBOL+" §fInnocents: §a"+this.gameManager.getRemainingInnocents());
                board.setLine(4,"§a"+SYMBOL+" §fTemps: §a"+this.getTimeRemaining());

                board.setLine(3,"§f");

                String arc = "§aEn vie";
                if(!this.gameManager.getBowOnGroundListener().getBowsLocation().isEmpty()) arc = ChatColor.of(new Color(255,0,0)) +"Au sol!";
                board.setLine(2,"§a"+SYMBOL+" §fDétective: "+arc);
            }
            case END_GAME -> {
            }
        }
        board.setLine(1,"§f");
        board.setLine(0,"§7Dev par @Red_Spash");
    }

    public void updateTeam(RedScoreBoard board) {
        GameState gameState = this.gameManager.getGameState();

        if(gameState == GameState.PRE_START || gameState == GameState.IN_GAME){
            for (Player pl : Bukkit.getOnlinePlayers()) {
                this.addHideTeam(board, pl);
            }
        }else{
            for (Player pl : Bukkit.getOnlinePlayers()) {
                this.defaultTeam(board, pl);
            }
        }

    }

    private void addHideTeam(RedScoreBoard board, Player pl) {
        Team defaultTeam = board.getTeam("hidedTeam");
        if(defaultTeam == null){
            defaultTeam = board.createTeam("hidedTeam");
            defaultTeam.setColor(org.bukkit.ChatColor.LIGHT_PURPLE);
            defaultTeam.setCanSeeFriendlyInvisibles(false);
            defaultTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            defaultTeam.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
            defaultTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            defaultTeam.setDisplayName(REPLACED_NAME_ANONYMOUS);
            defaultTeam.setPrefix("§k");
        }

        if(!defaultTeam.hasEntry(pl.getName())){
            pl.setPlayerListName(REPLACED_NAME_ANONYMOUS);
            defaultTeam.addEntry(pl.getName());
        }
    }

    private void defaultTeam(RedScoreBoard board, Player pl) {
        String teamName = "defaultTeam";
        if(pl.isOp()){
            teamName = "dev";
        }

        Team team = board.getTeam(teamName);

        if(team == null){
            team = board.createTeam(teamName);
            if(pl.isOp()){
                team.setColor(org.bukkit.ChatColor.RED);
                team.setPrefix("[Développeur] ");
            }else{
                team.setColor(org.bukkit.ChatColor.LIGHT_PURPLE);
            }
        }

        if(!team.hasEntry(pl.getName())){
            team.addEntry(pl.getName());
        }
    }

    private String getTimeRemaining() {
        GameTimerTask gameTimer = this.gameManager.getGameTimer();

        int minute;
        int second;

        if(gameTimer != null){
            int timeRemaining = gameTimer.getRemainingSeconds();
            minute = timeRemaining/60;
            second = timeRemaining%60;
            if(minute == GameTimerTask.MAX_TIME){
                second = 0;
            }
        }else{
            minute = GameTimerTask.MAX_TIME/60;
            second = GameTimerTask.MAX_TIME%60;
        }
        return minute+"m "+second+"s";
    }

}
