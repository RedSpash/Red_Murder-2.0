package fr.red_spash.murder.commands;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.maps.GameMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGame implements CommandExecutor {

    private GameManager gameManager;

    public StartGame(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.isOp()){
            GameMap findedGameMap = null;
            for(GameMap gameMap : this.gameManager.getMapManager().getMaps()){
                if(gameMap.getName().equalsIgnoreCase("amarantemap")){
                    findedGameMap = gameMap;
                    break;
                }
            }
            if(findedGameMap != null){
                this.gameManager.startPreGame(findedGameMap);
            }
        }
        return false;
    }
}
