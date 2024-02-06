package fr.red_spash.murder.commands;

import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.utils.Utils;
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

public class EditWorld implements CommandExecutor, TabCompleter {
    private final ArrayList<World> editingWorld;
    private final MapManager mapManager;
    private final JavaPlugin main;

    public EditWorld(JavaPlugin main, MapManager mapManager) {
        this.mapManager = mapManager;
        this.main = main;
        this.editingWorld = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p))return true;
        if(strings.length == 0){
            commandSender.sendMessage("§c/editworld <nom du monde>");
            return true;
        }

        ArrayList<String> completer = new ArrayList<>();
        MapManager manager = this.mapManager;
        for(GameMap gameMap : manager.getMaps()){
            completer.add(gameMap.getFile().getName());
        }

        for(File file : manager.getInvalidMaps()){
            completer.add(file.getName());
        }
        StringBuilder name = new StringBuilder();
        for(String ss : strings){
            name.append(ss).append(" ");
        }
        name = new StringBuilder(name.substring(0, name.length() - 1));
        if(!completer.contains(name.toString())){
            commandSender.sendMessage("§cCarte introuvable !");
            return true;
        }

        for(World world : editingWorld){
            if(world.getName().equalsIgnoreCase(name.toString())){
                p.sendMessage("§cImpossible de charger un monde avec le nom '"+name+"' car un monde a déjà ce nom !");
                p.teleport(world.getSpawnLocation());
                return true;
            }
        }

        File mapsFolder = new File(this.main.getDataFolder(), "maps/"+name);
        String pathName = name.toString();
        Path path2 = Paths.get(pathName);

        Utils.copyDirectory(mapsFolder.getPath(), path2.toString());

        World world = Bukkit.createWorld(new WorldCreator(name.toString()));
        if(world == null){
            world = Bukkit.getWorld(name.toString());
        }

        Location spawnLocation = world.getSpawnLocation();
        File configurationFile = new File(this.main.getDataFolder(),"maps/"+name+"/config.yml");
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
        editingWorld.add(world);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        ArrayList<String> completer = new ArrayList<>();
        for(GameMap gameMap : this.mapManager.getMaps()){
            completer.add(gameMap.getFile().getName());
        }

        for(File file : this.mapManager.getInvalidMaps()){
            completer.add(file.getName());
        }

        return completer;
    }

    public List<World> getEditingWorld() {
        return this.editingWorld;
    }

    public void removeEditingWorld(World world) {
        this.editingWorld.remove(world);
    }

    public void deleteAllWorlds() {
        for(World world : this.editingWorld){
            Utils.deleteWorldFiles(world.getWorldFolder());
        }
    }
}
