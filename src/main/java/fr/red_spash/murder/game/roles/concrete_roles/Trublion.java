package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.Murder;
import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.utils.ItemStackBuilder;
import fr.red_spash.murder.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.ArrayList;

public class Trublion extends Innocent {

    public static final int SLOT = 22;
    public static final ItemStack TRIGGER_POWER = new ItemStackBuilder(Material.COMPASS)
            .setName("§aUtilisez votre pouvoir")
            .setLore("§7Téléporte tout le monde","§7aléatoirement sur la carte!")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();

    private boolean powerAvailable = true;

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVlMGY5MTViNTY0NmI0MGU2ZDIyY2E2YWYxZjRlMzM2Njk3MmEzYWVlNDRhMWEzNmZiNTE3ODQ5YzI2M2ZjOSJ9fX0=";
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§3";
    }

    @Override
    public Color getRoleColor() {
        return new Color(0, 156, 101);
    }

    @Override
    public String getName() {
        return "Troublion";
    }

    @Override
    public String shortDescription() {
        return "Téléportez tout le monde aléatoirement à votre mort ou sur demande";
    }

    @Override
    public String getDescription() {
        return "Vous êtes Troublion, vous pouvez téléporter tout le monde aléatoirement sur la carte à votre demande ou automatiquement à votre mort !\n"+Murder.OPEN_YOUR_INVENTORY_FOR_ITEMS;
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_ILLUSIONER_MIRROR_MOVE;
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setItem(SLOT,TRIGGER_POWER);
    }

    public void setPowerAvailable(boolean powerAvailable) {
        this.powerAvailable = powerAvailable;
    }

    public boolean isPowerAvailable() {
        return powerAvailable;
    }

    public void teleportPlayers(GameManager gameManager) {
        this.setPowerAvailable(false);
        ArrayList<Location> spawns = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(spawns.isEmpty()){
               spawns.addAll(new ArrayList<>(gameManager.getActualMap().getSpawnsLocation()));
            }
            PlayerData playerData = gameManager.getPlayerManager().getData(player.getUniqueId());
            if(!playerData.isSpectator()){
                player.teleport(spawns.remove(Utils.generateRandomNumber(0,spawns.size()-1)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,35,2,false,false,false));
            }
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT,2,0);
        }
    }
}
