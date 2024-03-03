package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.utils.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class Murder extends Role {

    public static final int MURDER_SWORD_SLOT = 4;
    private boolean dash = true;
    private boolean throwSword = true;
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

    public void setThrowSword(boolean throwSword) {
        this.throwSword = throwSword;
    }

    public boolean canThrowSword() {
        return throwSword;
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§c";
    }

    @Override
    public Color getRoleColor() {
        return new Color(255,0,0);
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
        return "En tant que meurtrier, votre mission consiste à éliminer secrètement les autres joueurs sans attirer l'attention, nécessitant une stratégie subtile pour choisir le bon moment et la bonne méthode tout en évitant d'être découvert.";
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
