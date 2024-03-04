package fr.red_spash.murder.game.roles.listener;

import fr.red_spash.murder.game.events.GameActionListener;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Electrician;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.red_spash.murder.game.roles.concrete_roles.Electrician.ELECTRICIAN_TRIGGER_ITEM;
import static fr.red_spash.murder.game.roles.concrete_roles.Electrician.TIMER_ELECTRICIAN;

public class ElectricianListener extends GameActionListener {

    @Override
    public void inventoryClickEvent(InventoryClickEvent e, Player p, PlayerData playerData, ItemStack itemStack) {
        Role role = playerData.getVisualRole();
        if(!(role instanceof Electrician electrician))return;

        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;
        if(!itemMeta.getDisplayName().equals(ELECTRICIAN_TRIGGER_ITEM.getItemMeta().getDisplayName()))return;
        if(!electrician.isPowerUsed()){
            electrician.setPowerUsed(true);
            for(Player pl : Bukkit.getOnlinePlayers()){
                pl.playSound(pl.getLocation(),Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE,2,0);
                if(pl.getUniqueId() == p.getUniqueId()){
                    pl.sendTitle("§5§lVous êtes semi-aveuglé!", ChatColor.of(electrician.getRoleColor())+"La lumière clignote pour vous!",0,20*5,0);
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,20*TIMER_ELECTRICIAN,4,false,false,false));
                }else{
                    pl.sendTitle("§5§lVous êtes aveuglé!", ChatColor.of(electrician.getRoleColor())+"l'"+electrician.getName()+" vient d'éteindre la lumière!",0,20*5,0);
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
