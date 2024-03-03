package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;


public class GoldTask implements Runnable{

    private static final int MAX_GOLD =30; //12;
    private static final int DESPAWN_COOLDOWN = 1000*60;
    private final HashMap<Item, Long> items;
    private final GameManager gameManager;
    private final BukkitTask bukkitTask;
    private int maxHeight;
    private int minHeight;
    private int radius;

    public GoldTask(GameManager gameManager, Plugin main) {
        this.gameManager = gameManager;
        this.items = new HashMap<>();

        this.initializeSizes();

        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(main, this, 1,2);
    }

    private void initializeSizes() {
        double min = Double.MAX_VALUE;
        double max = 0;
        double radius = 0;

        for(Location location : this.gameManager.getActualMap().getSpawnsLocation()){
            double distance = location.distance(this.gameManager.getActualMap().getSpawnLocation());
            if(distance > radius){
                radius = distance;
            }

            if(location.getY() > max){
                max = location.getY();
            }

            if(location.getY() < min){
                min = location.getY();
            }
        }

        this.minHeight = (int) (min - 10);
        this.maxHeight = (int) (max + 10);
        this.radius = (int) (radius + 30);
    }

    @Override
    public void run() {
        if(this.gameManager.getGameState() == GameState.PRE_START){
            return;
        }else{
            if(this.gameManager.getGameState() != GameState.IN_GAME){
                this.stop();
            }
        }
        HashMap<Item, Long> clonedItems = (HashMap<Item, Long>) this.items.clone();
        for(Map.Entry<Item,Long> entry : clonedItems.entrySet()){
            Item item = entry.getKey();
            Long cooldown = entry.getValue();
            if(item.isDead() || cooldown <= System.currentTimeMillis()){
                item.remove();
                this.items.remove(item);
            }
        }
        if(this.items.size() < MAX_GOLD){
            boolean isValid = false;
            while (!isValid){
                Location location = this.gameManager.getActualMap()
                        .getSpawnLocation()
                        .add(Utils.generateRandomNumber(-radius,radius),
                                0,
                                Utils.generateRandomNumber(-radius,radius)
                        );

                location.setY(Utils.generateRandomNumber(minHeight,maxHeight));

                while (location.getBlock().getType() == Material.AIR){
                    location.add(0,-1,0);
                }
                location.add(0,1,0);

                if(location.getY() > minHeight && location.getY() < maxHeight){
                    isValid = true;

                    Item item = location.getWorld()
                            .dropItemNaturally(
                                    location.add(0.5,0,0.5),
                                    new ItemStack(Material.GOLD_INGOT)
                            );
                    this.items.put(item, System.currentTimeMillis()+DESPAWN_COOLDOWN);
                }
            }
        }
    }

    public void stop() {
        this.bukkitTask.cancel();
        for(Item item : this.items.keySet()){
            item.remove();
        }
        this.items.clear();
    }
}
