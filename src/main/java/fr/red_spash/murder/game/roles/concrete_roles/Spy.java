package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class Spy extends Innocent{
    public static final int SPY_SLOT = 1;
    public static final ItemStack SPY_TRIGGER_ITEM_ENABLE = new ItemStackBuilder(Material.REDSTONE)
            .setName("§cSe rendre invisible")
            .setLore("§7Vous permet d'être invisible")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();
    public static final ItemStack SPY_TRIGGER_ITEM_DISABLE = new ItemStackBuilder(Material.GLOWSTONE_DUST)
            .setName("§cSe rendre visible")
            .setLore("§7Vous permet d'être visible")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();
    public static final double COOLDOWN_BETWEEN_SWITCH = 1.25;
    public static final int SPY_INVISIBILITY_TIME = 15;
    private boolean usingPower = false;
    private double power = SPY_INVISIBILITY_TIME;
    private long cooldownPower = 0L;

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUwZDM2MWQ4MzI5MmMwNGZlYWE4MTFmMWU1NzgzODQyYjc4ZmFhOWM5ZmY4ZGE2Y2MwNjMwMThkNjJkYjdkZiJ9fX0=";
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§d";
    }

    @Override
    public Color getRoleColor() {
        return new Color(255, 0, 255);
    }

    @Override
    public String getName() {
        return "Espion";
    }

    @Override
    public String shortDescription() {
        return "Manipulez les ombres à votre avantage";
    }

    @Override
    public String getDescription() {
        return "Vous incarnez l'archétype de la furtivité, capable de se rendre invisible pendant "+SPY_INVISIBILITY_TIME+" secondes à volonté. Vous maniez les ombres avec habileté, vous permettant de vous déplacer incognito et de planifier vos mouvements avec précision. Cette capacité vous confère un avantage tactique indéniable, vous permettant d'observer vos ennemis, de vous infiltrer dans leurs rangs, ou de vous éclipser rapidement lorsque la situation l'exige.";
    }

    @Override
    public Sound getSound() {
        return Sound.BLOCK_BEACON_POWER_SELECT;
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setItem(SPY_SLOT,SPY_TRIGGER_ITEM_ENABLE.clone());
    }


    public boolean isUsingIsPower() {
        return this.usingPower;
    }

    public void removePower(double v) {
        this.power = this.power - v;
    }

    public double getPower() {
        return this.power;
    }

    public long getCooldownPower() {
        return cooldownPower;
    }

    public void setCooldownPower(long cooldownPower) {
        this.cooldownPower = cooldownPower;
    }

    public void setUsingPower(boolean usingPower) {
        this.usingPower = usingPower;
    }
}
