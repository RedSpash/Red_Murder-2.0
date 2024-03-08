package fr.red_spash.murder.game.roles.listener;

import fr.red_spash.murder.game.commands.ActionBarMessageCommand;
import fr.red_spash.murder.game.events.GameActionListener;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Spy;
import fr.red_spash.murder.game.tasks.cooldown.CooldownTask;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.red_spash.murder.game.roles.concrete_roles.Spy.*;

public class SpyListener extends GameActionListener {

    private final PlayerManager playerManager;
    private final JavaPlugin javaPlugin;

    public SpyListener(PlayerManager playerManager, JavaPlugin javaPlugin) {
        this.playerManager = playerManager;
        this.javaPlugin = javaPlugin;
    }

    @Override
    public void playerInteractEvent(PlayerInteractEvent e, Player p, PlayerData playerData, ItemStack itemStack) {
        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;

        Role role = playerData.getVisualRole();
        if(!(role instanceof Spy spy))return;

        if (itemMeta.getDisplayName().equals(SPY_TRIGGER_ITEM_DISABLE.getItemMeta().getDisplayName())
                || itemMeta.getDisplayName().equals(SPY_TRIGGER_ITEM_ENABLE.getItemMeta().getDisplayName())) {
            if(!spy.isUsingIsPower() && spy.getCooldownPower() > System.currentTimeMillis()){
                p.sendMessage("§cVeuillez attendre entre chaque interaction!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,0);
                return;
            }
            if(spy.getPower() <= 0){
                p.sendMessage("§cVous n'avez plus d'invisibilité!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,0);
                return;
            }
            if(spy.isUsingIsPower()){
                this.disableInvisibility(p, spy);
                playerData.addCooldown(new CooldownTask("§cRechargement...",playerData, COOLDOWN_BETWEEN_SWITCH,new ActionBarMessageCommand(p,"§a§lRechargement terminé!"), this.javaPlugin, true));
            }else{
                this.enableInvisibility(p, spy);
            }
            spy.setCooldownPower(System.currentTimeMillis()+(int) (1500* COOLDOWN_BETWEEN_SWITCH));
        }
    }

    public void enableInvisibility(Player p, Spy spy) {
        spy.setUsingPower(true);
        for(Player pl : Bukkit.getOnlinePlayers()){
            pl.hidePlayer(this.javaPlugin, p);
        }
        p.sendMessage("§aVous êtes désormais invisible !");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,1,1);
        p.getWorld().spawnParticle(Particle.CRIT_MAGIC,p.getLocation().add(0,0.5,0) ,70,0.25,0.5,0.25,0.2);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,10000*20,10,false,false,false));
        p.getInventory().setItem(SPY_SLOT, SPY_TRIGGER_ITEM_DISABLE);
    }

    public void disableInvisibility(Player p, Spy spy) {
        spy.setUsingPower(false);
        for(Player pl : Bukkit.getOnlinePlayers()){
            pl.showPlayer(this.javaPlugin, p);
        }
        p.sendMessage("§cVous êtes désormais visible !");
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,1,1);
        p.getWorld().spawnParticle(Particle.CRIT_MAGIC,p.getLocation().add(0,0.5,0)  ,70,0.25,0.5,0.25,0.2);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.getInventory().setItem(SPY_SLOT, SPY_TRIGGER_ITEM_ENABLE);
    }

}
