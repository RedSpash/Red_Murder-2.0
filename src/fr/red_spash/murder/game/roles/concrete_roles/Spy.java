package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.game.roles.ItemTrigger;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

public class Spy extends Innocent implements ItemTrigger {
    public static final int SPY_SLOT = 1;
    private static final ItemStack SPY_TRIGGER_ITEM_ENABLE = new ItemStackBuilder(Material.REDSTONE)
            .setName("§cSe rendre invisible")
            .setLore("§7Vous permez d'être invisible")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();
    private static final ItemStack SPY_TRIGGER_ITEM_DISABLE = new ItemStackBuilder(Material.GLOWSTONE_DUST)
            .setName("§cSe rendre visible")
            .setLore("§7Vous permez d'être visible")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();
    private static final int SPY_INVISIBILITY_TIME = 15;
    private boolean usingPower = false;
    private double power = SPY_INVISIBILITY_TIME;
    private long cooldownPower = 0L;

    @Override
    public String getMinecraftRoleColor() {
        return "§d";
    }

    @Override
    public Color getRoleColor() {
        return new Color(255, 0, 255);
    }

    @Override
    public String getName() {
        return "Espion";
    }

    @Override
    public String shortDescription() {
        return "Manipulez les ombres à votre avantage";
    }

    @Override
    public String getDescription() {
        return "Vous incarnez l'archétype de la furtivité, capable de se rendre invisible pendant "+SPY_INVISIBILITY_TIME+" secondes à volonté. Vous maniez les ombres avec habileté, vous permettant de vous déplacer incognito et de planifier vos mouvements avec précision. Cette capacité vous confère un avantage tactique indéniable, vous permettant d'observer vos ennemis, de vous infiltrer dans leurs rangs, ou de vous éclipser rapidement lorsque la situation l'exige.";
    }

    @Override
    public Sound getSound() {
        return Sound.BLOCK_BEACON_POWER_SELECT;
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setItem(SPY_SLOT,SPY_TRIGGER_ITEM_ENABLE.clone());
    }

    @Override
    public void triggerAction(Player p, ItemStack itemStack) {
        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;
        if (itemMeta.getDisplayName().equals(SPY_TRIGGER_ITEM_DISABLE.getItemMeta().getDisplayName())
                || itemMeta.getDisplayName().equals(SPY_TRIGGER_ITEM_ENABLE.getItemMeta().getDisplayName())) {
            if(cooldownPower > System.currentTimeMillis()){
                p.sendMessage("§cVeuillez attendre entre chaque interaction!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,0);
                return;
            }
            if(this.power <= 0){
                p.sendMessage("§cVous n'avez plus d'invisibilité!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,0);
                return;
            }
            if(this.usingPower){
                this.disableInvisibility(p);
            }else{
                this.enableInvisibility(p);
            }
        }
        cooldownPower = System.currentTimeMillis()+1000;
    }

    public boolean isUsingIsPower() {
        return this.usingPower;
    }

    public void removePower(double v) {
        this.power = this.power - v;
    }

    public double getPower() {
        return this.power;
    }

    public void enableInvisibility(Player p) {
        this.usingPower = true;
        for(Player pl : Bukkit.getOnlinePlayers()){
            pl.hidePlayer(p);
        }
        p.sendMessage("§aVous êtes désormais invisible !");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,1,1);
        p.getWorld().spawnParticle(Particle.CRIT_MAGIC,p.getLocation().add(0,0.5,0) ,70,0.25,0.5,0.25,0.2);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,10000*20,10,false,false,false));
        p.getInventory().setItem(SPY_SLOT, SPY_TRIGGER_ITEM_DISABLE);
    }

    public void disableInvisibility(Player p) {
        this.usingPower = false;
        for(Player pl : Bukkit.getOnlinePlayers()){
            pl.showPlayer(p);
        }
        p.sendMessage("§cVous êtes désormais visible !");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,1,1);
        p.getWorld().spawnParticle(Particle.CRIT_MAGIC,p.getLocation().add(0,0.5,0)  ,70,0.25,0.5,0.25,0.2);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.getInventory().setItem(SPY_SLOT, SPY_TRIGGER_ITEM_ENABLE);
    }
}
