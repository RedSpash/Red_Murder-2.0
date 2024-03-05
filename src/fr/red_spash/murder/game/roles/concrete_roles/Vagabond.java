package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;

public class Vagabond extends Innocent{
    public static final int VAGABOND_SLOT = 22;
    public static final int TELEPORTATION_MAX_USE = 20;//2;

    public static final ItemStack VAGABOND_TELEPORT_BEACON = new ItemStackBuilder(Material.BEACON)
            .setName("§cPlacer votre balise de téléportation")
            .setLore("§7Vous permez de placer","§7la balise de téléportation")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();
    public static final ItemStack VAGABOND_TELEPORT_ITEM = new ItemStackBuilder(Material.NETHER_STAR, TELEPORTATION_MAX_USE)
            .setName("§cSe téléporter à votre balise")
            .setLore("§7Vous permez de vous","§7téléporter à votre balise.")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();
    public static final int MOTIONLESS_TIME = 7;
    public static final int VAGABOND_TIME_BETWEEN_TELEPORTATION = 25;
    private int remainingUse = TELEPORTATION_MAX_USE;
    private Location beaconLocation = null;
    private long lastTeleportation = 0L;

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGIyMGM4NDhhMmQ1NTViNmQ4YWYxMDQ3ZGNhOGFmNDU0YjA2OWU2YjI2Mzc4MWEwYTIxYmJjYmFjMjkyMmNjIn19fQ==";
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§b";
    }

    @Override
    public Color getRoleColor() {
        return new Color(0, 255, 174);
    }

    @Override
    public String getName() {
        return "Vagabond";
    }

    @Override
    public String shortDescription() {
        return "Téléportez vous à un point précis.";
    }

    @Override
    public String getDescription() {
        return "En tant que Vagabond, vous pouvez placer une balise pour vous y téléportez plus tard dans la partie. La téléportation peut être réalisée "+TELEPORTATION_MAX_USE+" par partie! Lorsque vous utilisez votre pouvoir vous serez immobilisé pendant "+MOTIONLESS_TIME+" secondes!\n"+Murder.OPEN_YOUR_INVENTORY_FOR_ITEMS;
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_ILLUSIONER_MIRROR_MOVE;
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setItem(VAGABOND_SLOT,VAGABOND_TELEPORT_BEACON.clone());
    }

    public long getLastTeleportation() {
        return lastTeleportation;
    }

    public int getRemainingUse() {
        return remainingUse;
    }

    public Location getBeaconLocation() {
        return beaconLocation;
    }

    public void setBeaconLocation(Location beaconLocation) {
        this.beaconLocation = beaconLocation;
    }

    public void setLastTeleportation(long lastTeleportation) {
        this.lastTeleportation = lastTeleportation;
    }

    public void removeUtilisation(int i) {
        this.remainingUse = this.remainingUse - i;
    }
}
