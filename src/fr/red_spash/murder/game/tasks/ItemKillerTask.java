package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class ItemKillerTask implements Runnable {
    private final BukkitTask bukkitTask;
    private final Item item;
    private final GameManager gameManager;
    private final Player playerLauncher;
    private final DeathManager deathManager;

    public ItemKillerTask(Item item, JavaPlugin main, GameManager gameManager, Player playerLauncher, DeathManager deathManager) {
        this.item = item;
        this.gameManager = gameManager;
        this.deathManager = deathManager;
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(main, this,1,1);
        this.playerLauncher = playerLauncher;
    }

    @Override
    public void run() {
        if(this.item == null || this.item.isDead() || this.item.isOnGround()){
            if(this.item != null){
                this.item.getWorld().playSound(this.item.getLocation(), Sound.ENTITY_ITEM_BREAK,1,1);
            }
            this.bukkitTask.cancel();
            return;
        }

        for(Entity entity : this.item.getWorld().getNearbyEntities(this.item.getLocation(),0.2,0.2,0.2)){
            if(entity instanceof Player p){
                PlayerData playerData = this.gameManager.getPlayerManager().getData(p);
                Role role = playerData.getVisualRole();

                if(role.isMurder() || playerData.isSpectator())continue;

                this.deathManager.killPlayer(p,this.playerLauncher);
            }
        }

    }
}
