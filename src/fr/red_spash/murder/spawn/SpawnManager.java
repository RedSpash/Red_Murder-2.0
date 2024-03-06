package fr.red_spash.murder.spawn;

import fr.red_spash.murder.utils.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class SpawnManager {
    public static final ItemStack VIEW_ROLES = new ItemStackBuilder(Material.PLAYER_HEAD)
            .setName("§a§lVoir les rôles")
            .setHeadTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRlMWRhODgyZTQzNDgyOWI5NmVjOGVmMjQyYTM4NGE1M2Q4OTAxOGZhNjVmZWU1YjM3ZGViMDRlY2NiZjEwZSJ9fX0=")
            .toItemStack();

    public static final ItemStack VIEW_MAPS = new ItemStackBuilder(Material.MAP)
            .setName("§a§lVoir les cartes")
            .toItemStack();
    private final Location spawnLocation;

    public SpawnManager() {
        spawnLocation = new Location(Bukkit.getWorld("world"),-77.5, 105, -59.5, 180, 0);;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public void giveSpawnItems(Player p){
        p.getInventory().clear();
        p.getInventory().setItem(4, VIEW_ROLES);
        p.getInventory().setItem(8,VIEW_MAPS);
    }

    public void teleportSpawn(Player p) {
        p.teleport(this.spawnLocation);
        giveSpawnItems(p);
    }

    public void playTitle(Player p) {
        p.sendTitle(ChatColor.of(Color.RED)+"§lMURDER - BETA TEST","§c§lDéveloppé par @Red_Spash",10,20*3,20);
    }
}
