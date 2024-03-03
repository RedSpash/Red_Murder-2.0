package fr.red_spash.murder.commands;

import fr.red_spash.murder.maps.MapManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeleportTo implements CommandExecutor, TabCompleter {

    private final MapManager mapManager;

    public TeleportTo(MapManager mapManager) {
        this.mapManager = mapManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p))return false;
        if(!(p.isOp()))return false;
        if(strings.length == 0){
            commandSender.sendMessage("§c/teleportTo <nom du monde>");
            return true;
        }
        String name = strings[0];

        World world = Bukkit.getWorld(name);

        if(world == null){
            p.sendMessage("§cAucun monde avec le nom "+name+"!");
            return true;
        }

        Location spawnLocation = this.mapManager.getSpawnLocation(world, world.getWorldFolder().toString());

        p.teleport(spawnLocation);
        p.setGameMode(GameMode.CREATIVE);
        p.setFlying(true);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        ArrayList<String> completer = new ArrayList<>();
        for(World world : Bukkit.getWorlds()){
            completer.add(world.getName());
        }

        return completer;
    }
}
