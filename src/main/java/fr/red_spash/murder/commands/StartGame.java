package fr.red_spash.murder.commands;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.maps.GameMap;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class StartGame implements CommandExecutor, TabCompleter {

  private GameManager gameManager;

  public StartGame(GameManager gameManager) {
    this.gameManager = gameManager;
  }


  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (commandSender.isOp() && strings.length == 1) {
      GameMap findedGameMap = null;
      for (GameMap gameMap : this.gameManager.getMapManager().getMaps()) {
        if (gameMap.getName().equalsIgnoreCase(strings[0])) {
          findedGameMap = gameMap;
          break;
        }
      }
      if (findedGameMap != null) {
        this.gameManager.startPreGame(findedGameMap);
      } else {
        commandSender.sendMessage("§cCarte introuvable!");
      }
    }
    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
    ArrayList<String> completer = new ArrayList<>();
    for (GameMap gameMap : this.gameManager.getMapManager().getMaps()) {
      completer.add(gameMap.getName());
    }

    return completer;
  }
}
