package fr.red_spash.murder.game.roles;

import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Role {

    public abstract String getRoleColor();
    public abstract String getName();
    public abstract String shortDescription();
    public abstract String getDescription();
    public abstract Sound getSound();
    public void giveItems(Player p){

    }

    public void giveBow(Player p){
        ItemStack itemStack = p.getInventory().getItem(Detective.SLOT_BOW);
        p.getInventory().setItem(Detective.SLOT_BOW, Detective.DETECTIVE_BOW);
        if(itemStack == null){
            p.getInventory().setItem(Detective.SLOT_ARROW, new ItemStack(Material.ARROW));
        }else{
            p.getInventory().getItem(Detective.SLOT_ARROW).setAmount(
                    p.getInventory().getItem(Detective.SLOT_ARROW).getAmount()+1
            );
        }
    }
    public boolean isMurder(){
        return false;
    }
}
