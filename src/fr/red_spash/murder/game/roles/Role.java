package fr.red_spash.murder.game.roles;

import fr.red_spash.murder.game.roles.concrete_roles.Detective;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public abstract class Role {

    private boolean discovered = false;

    public abstract String getMinecraftRoleColor();
    public abstract Color getRoleColor();
    public abstract String getName();
    public abstract String shortDescription();
    public abstract String getDescription();
    public abstract Sound getSound();
    public void giveItems(Player p){

    }

    public void giveBow(Player p){
        ItemStack itemStack = p.getInventory().getItem(Detective.SLOT_ARROW);

        if(p.getInventory().getItem(Detective.SLOT_BOW) == null){
            p.getInventory().setItem(Detective.SLOT_BOW, new ItemStackBuilder(Material.BOW).setUnbreakable(true).setName("§aArc").hideAttributes().toItemStack());
        }
        if(itemStack == null){
            p.getInventory().setItem(Detective.SLOT_ARROW, new ItemStack(Material.ARROW));
        }else{
            itemStack.setAmount(
                    itemStack.getAmount()+1
            );
        }
    }
    public boolean isMurder(){
        return false;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public boolean isDiscovered() {
        return this.discovered;
    }
}
