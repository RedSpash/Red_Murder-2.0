package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.Murder;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class Psychic extends Innocent {
    public static final int SLOT = 22;
    public static final int POWER_ANALYSE_TIME = 25;
    private static final int AMOUNT_OF_USE = 2;
    public static final int POWER_RADIUS = 12;
    public static final ItemStack TRIGGER_ITEM = new ItemStackBuilder(Material.AMETHYST_SHARD)
            .setName("§5§lAffiche le rôle d'un joueur")
            .setLore("§7Vous permet de découvrir","§7le rôle d'un joueur dans","§7un rayon de "+POWER_RADIUS+" blocks autour de vous.","§f","§fTemps d'analyse: "+POWER_ANALYSE_TIME+" secondes")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();

    private int remainingUse = AMOUNT_OF_USE;
    private boolean powerInUse = false;

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc4M2FhMmJmZjI5Yjk3MTNiZjZjY2UyZjk3MzMxMWUzMWJiMmNhOTVjNTQxNDRiOTEyOGZhYTkwOWQifX19";
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§5";
    }

    @Override
    public Color getRoleColor() {
        return new Color(108, 0, 92);
    }

    @Override
    public String getName() {
        return "Médium";
    }

    @Override
    public String shortDescription() {
        return "Écoutez les puissances mystérieuses du monde";
    }

    @Override
    public String getDescription() {
        return "Vous pouvez voir le rôle d'une personne situé dans un rayon de " + POWER_RADIUS + " blocks "+AMOUNT_OF_USE+" fois par partie. La personne est prise aléatoirement parmi les joueurs situé dans le rayon.\n"+ Murder.OPEN_YOUR_INVENTORY_FOR_ITEMS;
    }

    @Override
    public Sound getSound() {
        return Sound.BLOCK_BREWING_STAND_BREW;
    }

    @Override
    public void giveItems(Player p) {
        ItemStack itemStack = TRIGGER_ITEM.clone();
        itemStack.setAmount(AMOUNT_OF_USE);
        p.getInventory().setItem(SLOT, itemStack);
    }

    public int getRemainingUse() {
        return remainingUse;
    }

    public void removeOneUse() {
        this.remainingUse = this.remainingUse - 1;
    }

    public void setPowerInUse(boolean b) {
        this.powerInUse = b;
    }

    public boolean isPowerInUse() {
        return this.powerInUse;
    }
}
