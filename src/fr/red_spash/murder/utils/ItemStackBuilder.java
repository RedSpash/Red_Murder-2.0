package fr.red_spash.murder.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemStackBuilder {

    private final ItemStack itemStack;

    public ItemStackBuilder(Material m) {
        this(m, 1);
    }

    public ItemStackBuilder(ItemStack is) {
        this.itemStack = is;
    }

    public ItemStackBuilder(Material m, int amount) {
        this.itemStack = new ItemStack(m, amount);
    }

    public ItemStackBuilder setDurability(short dur) {
        ItemMeta im = this.getItemMeta();
        if(im instanceof Damageable damageable){
            damageable.setDamage(dur);
            this.itemStack.setItemMeta(damageable);
        }
        return this;
    }


    public ItemStackBuilder setLore(String... lore) {
        ItemMeta im = this.getItemMeta();
        im.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder setHeadTexture(String textureUUID){
        if(this.getItemMeta() instanceof SkullMeta skullMeta){
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", textureUUID));

            setGameProfile(skullMeta, profile);

            this.itemStack.setItemMeta(skullMeta);
        }else{
            Bukkit.broadcastMessage("§cError, not instance of SkullMeta");
        }
        return this;
    }

    private static void setGameProfile(SkullMeta meta, GameProfile profile) {
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public ItemStackBuilder setLore(List<String> lore) {
        ItemMeta im = this.getItemMeta();
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder addLoreLine(String line) {
        ItemMeta im = this.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder setName(String name) {
        ItemMeta im = this.getItemMeta();
        im.setDisplayName(name);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder hideAttributes() {
        ItemMeta im = this.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
        this.itemStack.setItemMeta(im);
        return this;
    }

    private ItemMeta getItemMeta() {
        if(itemStack.hasItemMeta()){
            return itemStack.getItemMeta();
        }
        this.itemStack.setItemMeta(Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
        return this.itemStack.getItemMeta();
    }

    public ItemStackBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = this.getItemMeta();
        im.addEnchant(ench, level, true);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStack toItemStack() {
        return itemStack;
    }


    public ItemStackBuilder setUnbreakable(boolean b) {
        ItemMeta im = this.getItemMeta();
        im.setUnbreakable(b);
        this.itemStack.setItemMeta(im);
        return this;
    }
}
