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

    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI0ZDNjNjk2NDllMjU3MTZjYTA5MzE3Y2I0ZGJhYzE5NTU1MjI5ZjY2YmYxMWJmZWIwYzc1YjdmZTg3ODUzIn19fQ==";
    }
}
