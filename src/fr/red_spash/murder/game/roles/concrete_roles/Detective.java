package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class Detective extends Role {

    public static final ItemStack DETECTIVE_BOW = new ItemStackBuilder(Material.BOW)
            .setName("§6Arc du détective")
            .setLore("§7Protégez les innocents","§7et démasquez le meutrier!")
            .setUnbreakable(true)
            .toItemStack();
    public static final int SLOT_ARROW = 9;
    public static final int SLOT_BOW = 4;

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU3YTExMzIwOWVmYWQ0ZTgwMWI0YWNiNmRkYzU2NWFmZDBjZDg0ZTMzZGMyNjk0YzFjZjBjNmM5MmJjMDE4YiJ9fX0=";
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§6";
    }

    @Override
    public Color getRoleColor() {
        return new Color(255, 170, 0);
    }

    @Override
    public String getName() {
        return "Detective";
    }

    @Override
    public String shortDescription() {
        return "Analysez les scènes de crime et identifiez le meurtrier.";
    }

    @Override
    public String getDescription() {
        return "En tant que détective, vous avez la tâche cruciale de collecter des indices, d'analyser les scènes de crime et d'interroger les autres joueurs pour identifier le meurtrier.";
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setHeldItemSlot(3);
        p.getInventory().setItem(SLOT_BOW,DETECTIVE_BOW);
        p.getInventory().setItem(SLOT_ARROW,new ItemStack(Material.ARROW));
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_PLAYER_LEVELUP;
    }

}
