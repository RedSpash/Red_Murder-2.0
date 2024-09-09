package fr.red_spash.murder.game.scoreboard;

import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreboardTask implements Runnable {

  private final PlayerManager playerManager;
  private final ScoreboardLines scoreBoardLines;

  public ScoreboardTask(PlayerManager playerManager, ScoreboardLines scoreBoardLines) {
    this.playerManager = playerManager;
    this.scoreBoardLines = scoreBoardLines;
  }

  @Override
  public void run() {
    for (Player p : Bukkit.getOnlinePlayers()) {
      PlayerData playerData = this.playerManager.getData(p.getUniqueId());
      RedScoreBoard board = playerData.getScoreBoard();

      if (board == null) {
        board = new RedScoreBoard();
        playerData.setScoreBoard(board);
      }

      this.scoreBoardLines.setScoreBoard(playerData, p);
      this.scoreBoardLines.updateTeam(board);
    }
  }
}
