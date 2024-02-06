package fr.red_spash.murder.game.roles;

import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Detective implements Role{

    public static final ItemStack DETECTIVE_BOW = new ItemStackBuilder(Material.BOW)
            .setName("§6Arc du détective")
            .setLore("§7Protégez les innocents","§7et démasquez le meutrier!")
            .setUnbreakable(true)
            .toItemStack();
    public static final int SLOT_ARROW = 30;

    @Override
    public String getRoleColor() {
        return "§6";
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
        p.getInventory().setItem(4,DETECTIVE_BOW);
        p.getInventory().setItem(SLOT_ARROW,new ItemStack(Material.ARROW));
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_PLAYER_LEVELUP;
    }
}
