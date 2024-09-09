package fr.red_spash.murder.tasks;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class SpawnShowTask implements Runnable {

    private final ArrayList<Location> spawnLocations;
    private final Player player;
    private BukkitTask bukkitTask;

    public SpawnShowTask(Player p, FileConfiguration fileConfiguration) {
        this.player = p;
        this.spawnLocations = new ArrayList<>();
        this.updateData(p.getWorld(), fileConfiguration);
    }

    public void setBukkitTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public void updateData(World world, FileConfiguration fileConfiguration) {
        if(!world.equals(player.getWorld())){
            return;
        }
        this.spawnLocations.clear();
        ArrayList<String> spawnIds = new ArrayList<>(fileConfiguration.getConfigurationSection("spawns").getKeys(false));
        for(String spawnId : spawnIds) {
            String path = "spawns." + spawnId;
            this.spawnLocations.add(new Location(
                    this.player.getWorld(),
                    fileConfiguration.getDouble(path + ".x", 0.0),
                    fileConfiguration.getDouble(path + ".y", 101.5),
                    fileConfiguration.getDouble(path + ".z", 0.0),
                    fileConfiguration.getInt(path + ".yaw", 0),
                    fileConfiguration.getInt(path + ".pitch", 0)
            ));
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if(this.player == null || !this.player.isOnline()){
            this.stop();
            return;
        }
        for(Location location : this.spawnLocations){
            if(location.getWorld() != player.getWorld()){
                this.stop();
                return;
            }
            this.player.spawnParticle(Particle.CLOUD, location,20,0.3,0.3,0.3, 0);
        }
    }

    private void stop() {
        if(this.bukkitTask != null){
            this.bukkitTask.cancel();
            this.player.sendMessage("§cVous ne voyez plus les spawns!");
        }
    }

    public BukkitTask getBukkitTask() {
        return this.bukkitTask;
    }
}
