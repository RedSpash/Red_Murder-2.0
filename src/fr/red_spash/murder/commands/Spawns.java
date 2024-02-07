package fr.red_spash.murder.commands;

import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.tasks.SpawnShowTask;
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
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Spawns implements CommandExecutor, TabCompleter {

    private final JavaPlugin main;
    private final EditWorld editWorld;
    private final MapManager mapManager;
    private final HashMap<UUID, SpawnShowTask> spawnsShows;

    public Spawns(JavaPlugin main, MapManager mapManager, EditWorld editWorld) {
        this.main = main;
        this.editWorld = editWorld;
        this.mapManager = mapManager;
        spawnsShows = new HashMap<UUID, SpawnShowTask>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player p))return true;
        if(strings.length == 0){
            commandSender.sendMessage("§c/spawns <add|delete|list|setmainspawn|shows>");
            return true;
        }

        if(!editWorld.getEditingWorld().contains(p.getWorld())){
            p.sendMessage("§cVous devez être dans un monde en monde /editworld pour modifier le spawns.");
            return true;
        }

        FileConfiguration fileConfiguration = this.getFileConfigurationOfWorld(p.getWorld());

        switch (strings[0].toLowerCase()){
            case "add"->{
                int lastId = 0;

                if(fileConfiguration.isSet("spawns")){
                    ArrayList<String> elements = new ArrayList<>(fileConfiguration.getConfigurationSection("spawns").getKeys(false));
                    if(!elements.isEmpty()){
                        lastId = Integer.parseInt(elements.get(elements.size()-1))+1;
                    }
                }

                String keyPath = "spawns.";
                fileConfiguration.set(keyPath +lastId+".x",p.getLocation().getX());
                fileConfiguration.set(keyPath +lastId+".y",p.getLocation().getY());
                fileConfiguration.set(keyPath +lastId+".z",p.getLocation().getZ());
                fileConfiguration.set(keyPath +lastId+".yaw",p.getLocation().getYaw());
                fileConfiguration.set(keyPath +lastId+".pitch",p.getLocation().getPitch());

                this.saveFileConfiguration(p.getWorld(),fileConfiguration);
                p.sendMessage("§aLe spawn n°"+lastId+" vient d'être ajouté avec succès!");
                this.updateRunnables(p.getWorld(), fileConfiguration);
            }
            case "delete","remove"->{

                HashMap<Location,String> spawnsLocation = new HashMap<>();
                fileConfiguration.getConfigurationSection("spawns").getKeys(false).forEach(spawnId ->{
                    String path = "spawns."+spawnId;
                    spawnsLocation.put(new Location(
                            p.getWorld(),
                            fileConfiguration.getDouble(path+".x",0.0),
                            fileConfiguration.getDouble(path+".y",101.5),
                            fileConfiguration.getDouble(path+".z",0.0),
                            fileConfiguration.getInt(path+".yaw",0),
                            fileConfiguration.getInt(path+".pitch",0)
                        ),
                            spawnId
                    );
                });

                Location nearbyLocation = null;

                for(Location location : spawnsLocation.keySet()){
                    if(nearbyLocation != null){
                        if(location.distance(p.getLocation()) < nearbyLocation.distance(p.getLocation())){
                            nearbyLocation = location;
                        }
                    }else{
                        nearbyLocation = location;
                    }
                }

                if(nearbyLocation == null || nearbyLocation.distance(p.getLocation()) >= 1){
                    p.sendMessage("§cAucun spawn trouvé dans un rayon de 1 block!");
                }else{
                    String spawnId = spawnsLocation.get(nearbyLocation);
                    String path = "spawns."+spawnId;
                    fileConfiguration.set(path,null);
                    // fileConfiguration.set(path+".y",null);
                    // fileConfiguration.set(path+".z",null);
                    // fileConfiguration.set(path+".yaw",null);
                    // fileConfiguration.set(path+".pitch",null);

                    this.saveFileConfiguration(p.getWorld(), fileConfiguration);
                    p.sendMessage("§aLe spawn n°"+spawnId+" vient d'être retiré !");
                }

                this.updateRunnables(p.getWorld(),fileConfiguration);
            }
            case "list"->{
                StringBuilder message = new StringBuilder("§a§lListe des spawns de votre monde:\n");

                ArrayList<String> spawnIds = new ArrayList<>(fileConfiguration.getConfigurationSection("spawns").getKeys(false));

                for(String spawnId : spawnIds){
                    String path = "spawns."+spawnId;
                    Location location = new Location(
                                    p.getWorld(),
                                    fileConfiguration.getDouble(path+".x",0.0),
                                    fileConfiguration.getDouble(path+".y",101.5),
                                    fileConfiguration.getDouble(path+".z",0.0),
                                    fileConfiguration.getInt(path+".yaw",0),
                                    fileConfiguration.getInt(path+".pitch",0)
                            );
                    message.append("§f- §fn°")
                            .append(spawnId)
                            .append(" en §7")
                            .append(Utils.round(location.getX(),1))
                            .append(" ")
                            .append(Utils.round(location.getY(),1))
                            .append(" ")
                            .append(Utils.round(location.getZ(),1))
                            .append(" ")
                            .append(Utils.round(location.getYaw(),1))
                            .append(" ")
                            .append(Utils.round(location.getPitch(),1))
                            .append(" §fà §7")
                            .append(Utils.round(p.getLocation().distance(location),1))
                            .append(" block\n");
                }
                p.sendMessage(message.toString());
            }
            case "setmainspawn","mainspawn"->{
                String keyPath = "spawnlocation";
                fileConfiguration.set(keyPath +".x",p.getLocation().getX());
                fileConfiguration.set(keyPath +".y",p.getLocation().getY());
                fileConfiguration.set(keyPath +".z",p.getLocation().getZ());
                fileConfiguration.set(keyPath +".yaw",p.getLocation().getYaw());
                fileConfiguration.set(keyPath +".pitch",p.getLocation().getPitch());

                this.saveFileConfiguration(p.getWorld(),fileConfiguration);
                p.sendMessage("§aLe spawn majeur vient d'être positionné à vote position !");
            }case "shows"->{
                if(this.spawnsShows.containsKey(p.getUniqueId())){
                    this.spawnsShows.get(p.getUniqueId()).getBukkitTask().cancel();
                    this.spawnsShows.remove(p.getUniqueId());
                    p.sendMessage("§cVous ne voyez plus les spawns!");
                }else{
                    SpawnShowTask runnable = new SpawnShowTask(p, fileConfiguration);
                    BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(this.main,runnable, 0, 20);
                    this.spawnsShows.put(p.getUniqueId(), runnable);
                    runnable.setBukkitTask(bukkitTask);
                    p.sendMessage("§aVous voyez désormais les spawns!");
                }
            }
        }



        return true;
    }

    private void updateRunnables(World world, FileConfiguration fileConfiguration) {
        for(SpawnShowTask spawnShowTask : this.spawnsShows.values()){
            spawnShowTask.updateData(world, fileConfiguration);
        }
    }

    private void saveFileConfiguration(World world, FileConfiguration fileConfiguration) {
        File file = new File(world.getWorldFolder(),"config.yml");
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileConfiguration getFileConfigurationOfWorld(World world){
        File file = new File(world.getWorldFolder(),"config.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("§cImpossible de créer le fichier de configuration !");
                throw new RuntimeException(e);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        ArrayList<String> completer = new ArrayList<>();

        if(strings.length == 1){
            completer.addAll(List.of("add","delete","list","setmainspawn","shows"));
        }

        return completer;
    }

}
