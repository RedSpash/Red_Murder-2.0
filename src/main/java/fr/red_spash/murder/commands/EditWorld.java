package fr.red_spash.murder.commands;

import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.spawn.SpawnManager;
import fr.red_spash.murder.utils.Utils;
import fr.red_spash.murder.world.EmptyChunkGenerator;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
    private final SpawnManager spawnManager;

    public EditWorld(JavaPlugin main, MapManager mapManager, SpawnManager spawnManager) {
        this.mapManager = mapManager;
        this.main = main;
        this.spawnManager = spawnManager;
        this.editingWorld = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p))return false;
        if(!(p.isOp()))return false;
        if(strings.length == 0){
            commandSender.sendMessage("§c/editworld <nom du monde>");
            return true;
        }
        String name = strings[0];
        if(!name.equals("void")){
            ArrayList<String> completer = new ArrayList<>();
            MapManager manager = this.mapManager;
            for(GameMap gameMap : manager.getMaps()){
                completer.add(gameMap.getFile().getName());
            }

            for(File file : manager.getInvalidMaps()){
                completer.add(file.getName());
            }
            StringBuilder stringBuilder1 = new StringBuilder();
            for(String ss : strings){
                stringBuilder1.append(ss).append(" ");
            }
            StringBuilder stringBuilder = new StringBuilder(stringBuilder1.substring(0, stringBuilder1.length() - 1));
            if(!completer.contains(stringBuilder.toString())){
                commandSender.sendMessage("§cCarte introuvable !");
                return true;
            }
            name = stringBuilder.toString();
        }


        for(World world : editingWorld){
            if(world.getName().equalsIgnoreCase(name)){
                p.sendMessage("§cImpossible de charger un monde avec le nom '"+name+"' car un monde a déjà ce nom !");
                p.teleport(world.getSpawnLocation());
                return true;
            }
        }

        if(!name.equals("void")){
            File mapsFolder = new File(this.main.getDataFolder(), "maps/"+name);
            Path path2 = Paths.get(name);

            Utils.copyDirectory(mapsFolder.getPath(), path2.toString());
        }

        WorldCreator creator = new WorldCreator(name);
        creator.generator(new EmptyChunkGenerator());
        World world = creator.createWorld();

        if(world == null){
            world = Bukkit.getWorld(name);
        }

        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);

        Location spawnLocation = this.mapManager.getSpawnLocation(world, this.main.getDataFolder()+"/maps/name/");

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
            Utils.teleportPlayersAndRemoveWorld(world,false, this.spawnManager);
            Utils.deleteWorldFiles(world.getWorldFolder());
        }
    }
}
