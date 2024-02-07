package fr.red_spash.murder.game.roles;

import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Murder extends Role{

    public static final int MURDER_SWORD_SLOT = 4;
    private boolean dash = true;
    private Item swordOnGround;
    public static final ItemStack MURDER_SWORD = new ItemStackBuilder(Material.DIAMOND_SWORD)
            .setName("§cÉpée du Meurtrier")
            .setLore("§7Tuez tout ce qui bouge!")
            .setUnbreakable(true)
            .toItemStack();

    public boolean canDash() {
        return dash;
    }

    public void setDash(boolean dash) {
        this.dash = dash;
    }

    public void setSwordOnGround(Item swordOnGround) {
        this.swordOnGround = swordOnGround;
    }

    public Item getSwordOnGround() {
        return swordOnGround;
    }

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
        p.getInventory().setHeldItemSlot(MURDER_SWORD_SLOT-1);
        p.getInventory().setItem(MURDER_SWORD_SLOT,MURDER_SWORD);
    }

    @Override
    public Sound getSound() {
        return Sound.AMBIENT_CAVE;
    }

    @Override
    public boolean isMurder() {
        return true;
    }
}
