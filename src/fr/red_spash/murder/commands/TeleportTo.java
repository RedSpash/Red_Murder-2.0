package fr.red_spash.murder.commands;

import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.utils.Utils;
import fr.red_spash.murder.world.EmptyChunkGenerator;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TeleportTo implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p))return true;
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

        Location spawnLocation = world.getSpawnLocation();
        File configurationFile = new File(world.getWorldFolder(),"config.yml");
        if(configurationFile.exists()){
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configurationFile);

            String path = "spawnlocation";
            boolean spawn = fileConfiguration.isSet(path);
            if(spawn){
                spawnLocation = new Location(
                        world,
                        fileConfiguration.getDouble(path+".x",0.0),
                        fileConfiguration.getDouble(path+".y",101.5),
                        fileConfiguration.getDouble(path+".z",0.0),
                        fileConfiguration.getInt(path+".yaw",0),
                        fileConfiguration.getInt(path+".pitch",0)
                );
            }

        }

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
