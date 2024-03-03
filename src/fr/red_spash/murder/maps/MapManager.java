package fr.red_spash.murder.maps;

import fr.red_spash.murder.Murder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class MapManager {

    private final ArrayList<File> invalidMaps;
    private final JavaPlugin murder;
    private final ArrayList<GameMap> maps;

    public MapManager(Murder murder) {
        this.murder = murder;
        this.maps = new ArrayList<>();
        this.invalidMaps = new ArrayList<>();
        this.loadMaps();
    }

    private void loadMaps() {
        File mapsFolder = new File(this.murder.getDataFolder(), "maps");

        if (mapsFolder.exists() && mapsFolder.isDirectory()) {
            File[] mapFolders = mapsFolder.listFiles(File::isDirectory);

            if (mapFolders != null) {
                for (File mapFolder : mapFolders) {
                    ArrayList<File> directories = new ArrayList<>(Collections.singletonList(mapFolder));

                    for (File directory : directories) {
                        String[] name = directory.toString().split("\\\\");

                        if (name.length == 1) {
                            name = directory.toString().split("/");
                        }

                        File[] files = directory.listFiles((dir, fileName) -> fileName.equalsIgnoreCase("config.yml"));

                        if (files == null || files.length != 1) {
                            this.invalidateMap(directory);
                        } else {
                            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(files[0]);

                            if(!fileConfiguration.isSet("spawnlocation")){
                                this.invalidateMap(directory);
                                return;
                            }

                            GameMap gameMap = new GameMap(name[name.length - 1], directory, fileConfiguration);
                            this.maps.add(gameMap);
                            getLogger().info("[MAP LOADER]: map " + gameMap.getName() + " loaded !");
                        }
                    }
                }
            }
        }
    }

    public Location getSpawnLocation(World world, String fileLocation) {
        Location spawnLocation = world.getSpawnLocation();
        if(spawnLocation.getY() >= 200){
            spawnLocation.setY(100);
        }

        File configurationFile = new File(fileLocation,"config.yml");
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

        return spawnLocation;
    }

    private void invalidateMap(File directory) {
        getLogger().warning("La map suivante est invalide : " + directory.toString());
        this.invalidMaps.add(directory);
    }

    public List<GameMap> getMaps() {
        return (List<GameMap>) maps.clone();
    }

    public List<File> getInvalidMaps() {
        return (List<File>) this.invalidMaps.clone();
    }
}
