package fr.red_spash.murder.game.roles;

import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Murder implements Role{

    public static final ItemStack MURDER_SWORD = new ItemStackBuilder(Material.DIAMOND_SWORD)
            .setName("§cÉpée du Meurtrier")
            .setLore("§7Tuez tout ce qui bouge!")
            .setUnbreakable(true)
            .toItemStack();

    @Override
    public String getRoleColor() {
        return "§c";
    }

    @Override
    public String getName() {
        return "Meurtrier";
    }

    @Override
    public String shortDescription() {
        return "Éliminez secrètement les autres joueurs.";
    }

    @Override
    public String getDescription() {
        return " En incarnant le rôle unique de schizophrène, vous vous trouvez dans une position intrigante où vous pouvez être soit innocent, soit meurtrier, avec une chance égale pour les deux. Cette dualité ajoute une complexité stratégique au jeu, car les autres participants doivent naviguer avec prudence, ne sachant pas clairement de quel côté vous vous trouvez.";
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setHeldItemSlot(3);
        p.getInventory().setItem(4,MURDER_SWORD);
    }

    @Override
    public Sound getSound() {
        return Sound.AMBIENT_CAVE;
    }
}
