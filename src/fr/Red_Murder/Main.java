package fr.Red_Murder;

import fr.Red_Murder.commands.AddSpawn;
import fr.Red_Murder.commands.Start;
import fr.Red_Murder.event.Murder;
import fr.Red_Murder.tasks.StartMurder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends JavaPlugin {
    private static Main instance;
    public static ArrayList<Location> Spawn = new ArrayList<Location>();
    public static ArrayList<Material> LockedBlock = new ArrayList<Material>();

    public File Spawn_File;
    public FileConfiguration Spawn_Config;


    public void onEnable(){
        instance = this;

        //Register Events
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new fr.Red_Murder.event.EventListener(),this);
        pm.registerEvents(new Murder(),this);

        //Register Commands
        getCommand("start").setExecutor(new Start());
        getCommand("addspawn").setExecutor(new AddSpawn());

        //Configs des spawns vers ArrayList
        Init_Spawns();
        Init_LockedBlock();

        new ScoreboardManager().runTaskTimer(Main.getInstance(), 0L, 10L);

        Bukkit.getConsoleSender().sendMessage("§c§lRed_Murder prêt !");

    }

    private void Init_LockedBlock() {
        LockedBlock.add(Material.CHEST);
        LockedBlock.add(Material.ANVIL);
        LockedBlock.add(Material.ENCHANTMENT_TABLE);
        LockedBlock.add(Material.WORKBENCH);
        LockedBlock.add(Material.BEACON);
        LockedBlock.add(Material.BED);
        LockedBlock.add(Material.BED_BLOCK);
        LockedBlock.add(Material.DAYLIGHT_DETECTOR);
        LockedBlock.add(Material.DAYLIGHT_DETECTOR_INVERTED);
        LockedBlock.add(Material.STONE_BUTTON);
        LockedBlock.add(Material.WOOD_BUTTON);
        LockedBlock.add(Material.LEVER);
        LockedBlock.add(Material.FURNACE);
        LockedBlock.add(Material.TRAPPED_CHEST);
        LockedBlock.add(Material.WOOD_PLATE);
    }

    public static Main getInstance() {
        return instance;
    }


    private void Init_Spawns() {
        Spawn_File = new File(getDataFolder(),"Spawns.yml");

        checkifexist(Spawn_File);
        Spawn_Config = YamlConfiguration.loadConfiguration(Spawn_File);

        if(Spawn_Config.getConfigurationSection("spawns") == null){return;}
        Spawn_Config.getConfigurationSection("spawns").getKeys(false).forEach(spawn -> {
            float x = Spawn_Config.getInt("spawns."+spawn+".X");
            float y = Spawn_Config.getInt("spawns."+spawn+".Y");
            float z = Spawn_Config.getInt("spawns."+spawn+".Z");
            String world = Spawn_Config.getString("spawns."+spawn+".world");
            float yaw = Spawn_Config.getInt("spawns."+spawn+".yaw");
            Spawn.add(new Location(Bukkit.getWorld(String.valueOf(world)),x,y,z,yaw,0));
        });

    }

    public void checkifexist(File file){
        if(!file.exists()){
            try {
                file.createNewFile();
                Bukkit.getConsoleSender().sendMessage("§a§lFile "+file.getName()+".yml created !");
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("§4&lError: "+file.getName()+".yml");
                e.printStackTrace();
            }
        }

    }

    public static int random_number(Integer min, Integer max){
        max = max +1;
        return (int) (Math.random()*(max-min)) + min;
    }
}
