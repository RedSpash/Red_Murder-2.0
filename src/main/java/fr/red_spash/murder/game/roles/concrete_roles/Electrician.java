package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.Murder;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class Electrician extends Innocent {
    public static final int TIMER_ELECTRICIAN = 17;
    private static final int ELECTRICIAN_SLOT = 22;
    public static final ItemStack ELECTRICIAN_TRIGGER_ITEM = new ItemStackBuilder(Material.TRIPWIRE_HOOK)
            .setName("§cCoupe le courant")
            .setLore("§7Vous permet de couper","§7le courant pendant "+TIMER_ELECTRICIAN+" secondes!")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();

    private boolean powerUsed = false;

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQyMGNkZjJhZjU3Y2M3NTc3NzNhY2UzZjE5MTVjYjcyYjU0MzJhMmZkYTMzNzNiMTY3OGY5OGJlYTdhYzcifX19";
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§5";
    }

    @Override
    public Color getRoleColor() {
        return new Color(157, 0, 157);
    }

    @Override
    public String getName() {
        return "Électricien";
    }

    @Override
    public String shortDescription() {
        return "Plongez le monde dans l'obscurité pour semer le chaos";
    }

    @Override
    public String getDescription() {
        return "Vous pouvez plonger le monde dans l'obscurité pour semer le chaos pendant "+TIMER_ELECTRICIAN+" secondes! Ce pouvoir donne l'effet aveuglement à tous les joueurs sauf à vous. Cependant vous possédez l'effet obscurité pendant le temps de votre pouvoir.\n"+Murder.OPEN_YOUR_INVENTORY_FOR_ITEMS;
    }

    @Override
    public Sound getSound() {
        return Sound.BLOCK_BEACON_POWER_SELECT;
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setItem(ELECTRICIAN_SLOT,ELECTRICIAN_TRIGGER_ITEM.clone());
    }

    public void setPowerUsed(boolean powerUsed) {
        this.powerUsed = powerUsed;
    }

    public boolean isPowerUsed() {
        return powerUsed;
    }
}
