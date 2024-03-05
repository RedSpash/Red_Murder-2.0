package fr.red_spash.murder.maps;

import fr.red_spash.murder.spawn.SpawnManager;
import fr.red_spash.murder.utils.Utils;
import fr.red_spash.murder.world.EmptyChunkGenerator;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameMap{
    private Location spawnLocation;
    private final String name;
    private final File file;
    private final FileConfiguration fileConfiguration;
    private final ArrayList<Location> spawnsLocation;
    private World world;
    private final SpawnManager spawnManager;

    public GameMap(String name, File file, FileConfiguration fileConfiguration, SpawnManager spawnManager) {
        this.name = name;
        this.file = file;
        this.fileConfiguration = fileConfiguration;
        this.spawnManager = spawnManager;
        this.spawnsLocation = new ArrayList<>();
    }

    private void loadMapData() {
        this.spawnsLocation.clear();

        this.spawnLocation = this.getConfigurationLocation("spawnlocation");

        if(this.fileConfiguration.isSet("spawns")){
            this.fileConfiguration.getConfigurationSection("spawns").getKeys(false).forEach(spawnId ->
                this.spawnsLocation.add(this.getConfigurationLocation("spawns."+spawnId))
            );
        }
    }

    private Location getConfigurationLocation(String path) {
        return new Location(
                this.world,
                this.fileConfiguration.getDouble(path+".x",0.0),
                this.fileConfiguration.getDouble(path+".y",101.5),
                this.fileConfiguration.getDouble(path+".z",0.0),
                this.fileConfiguration.getInt(path+".yaw",0),
                this.fileConfiguration.getInt(path+".pitch",0)
        );

    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public void deleteWorld(){
        this.deleteWorld(this.world);
    }

    public void deleteWorld(World world){
        if(world != null){
            Utils.teleportPlayersAndRemoveWorld(world,false, this.spawnManager);
            Utils.deleteWorldFiles(world.getWorldFolder());
            this.world = null;
        }else{
            Bukkit.broadcastMessage("§cMonde introuvable !");
        }

    }

    public World loadWorld(){
        Path path = Paths.get(this.file.getPath());
        String pathName = this.name+"-"+System.currentTimeMillis();
        Path path2 = Paths.get(pathName);

        Utils.copyDirectory(path.toString(), path2.toString());

        WorldCreator creator = new WorldCreator(pathName);
        creator.generator(new EmptyChunkGenerator());
        this.world = creator.createWorld();
        if(world == null){
            world = Bukkit.getWorld(pathName);
        }else{
            this.world.setGameRule(GameRule.DO_MOB_SPAWNING,false);
            this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
            this.world.setGameRule(GameRule.DO_FIRE_TICK,false);
        }
        try{
            this.loadMapData();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return this.world;
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    public Location getSpawnLocation() {
        return spawnLocation.clone();
    }

    public World getWorld() {
        return this.world;
    }

    public List<Location> getSpawnsLocation() {
        return spawnsLocation;
    }
}
