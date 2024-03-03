package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.game.roles.ItemTrigger;
import fr.red_spash.murder.utils.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;

public class Electrician extends Innocent implements ItemTrigger {
    private static final int TIMER_ELECTRICIAN = 17;
    private static final int ELECTRICIAN_SLOT = 22;
    private static final ItemStack ELECTRICIAN_TRIGGER_ITEM = new ItemStackBuilder(Material.TRIPWIRE_HOOK)
            .setName("§cCoupe le courant")
            .setLore("§7Vous permez de couper","§7le courant pendant "+TIMER_ELECTRICIAN+" secondes!")
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,1)
            .hideAttributes()
            .toItemStack();

    private boolean powerUsed = false;

    @Override
    public String getMinecraftRoleColor() {
        return "§5";
    }

    @Override
    public Color getRoleColor() {
        return new Color(157, 0, 157);
    }

    @Override
    public String getName() {
        return "Électricien";
    }

    @Override
    public String shortDescription() {
        return "Plongez le monde dans l'obscurité pour semer le chaos";
    }

    @Override
    public String getDescription() {
        return "Vous possédez le pouvoir d'obscurcir le monde pendant quelques secondes. Astucieux et habile, vous vous faufilez dans les recoins sombres, semant le chaos ou accomplissant ses desseins sinistres dans l'ombre\n§l§nVous n'êtes pas totalement affecté par votre pouvoir!";
    }

    @Override
    public Sound getSound() {
        return Sound.BLOCK_BEACON_POWER_SELECT;
    }

    @Override
    public void giveItems(Player p) {
        p.getInventory().setItem(ELECTRICIAN_SLOT,ELECTRICIAN_TRIGGER_ITEM.clone());
    }

    @Override
    public void triggerAction(Player p, ItemStack itemStack) {
        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;
        if(!itemMeta.getDisplayName().equals(ELECTRICIAN_TRIGGER_ITEM.getItemMeta().getDisplayName()))return;
        if(!this.powerUsed){
            this.powerUsed = true;
            for(Player pl : Bukkit.getOnlinePlayers()){
                pl.playSound(pl.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,2,0);
                if(pl.getUniqueId() == p.getUniqueId()){
                    pl.sendTitle("§5§lVous êtes semi-aveuglé!", ChatColor.of(this.getRoleColor())+"La lumière clignote pour vous!",0,20*5,0);
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,20*TIMER_ELECTRICIAN,4,false,false,false));
                }else{
                    pl.sendTitle("§5§lVous êtes aveuglé!", ChatColor.of(this.getRoleColor())+"l'"+this.getName()+" vient d'éteindre la lumière!",0,20*5,0);
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*TIMER_ELECTRICIAN,4,false,false,false));
                }

            }
            itemStack.setType(Material.BEDROCK);
            for(Enchantment enchantment : itemStack.getEnchantments().keySet()){
                itemStack.removeEnchantment(enchantment);
            }
            p.closeInventory();
        }else{
            p.sendMessage("§cVous avez déjà utilisé votre pouvoir !");
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1,1);
            p.closeInventory();
        }
    }
}
