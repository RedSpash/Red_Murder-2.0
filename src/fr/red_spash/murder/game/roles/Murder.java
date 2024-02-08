package fr.red_spash.murder.game.roles;

import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
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
        return " En tant que meurtrier, votre mission consiste à éliminer secrètement les autres joueurs sans attirer l'attention, nécessitant une stratégie subtile pour choisir le bon moment et la bonne méthode tout en évitant d'être découvert.";
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
