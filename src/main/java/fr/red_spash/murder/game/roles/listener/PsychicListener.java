package fr.red_spash.murder.game.roles.listener;

import fr.red_spash.murder.game.commands.PsychicEndScanCommand;
import fr.red_spash.murder.game.events.GameActionListener;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Psychic;
import fr.red_spash.murder.game.tasks.cooldown.CooldownTask;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class PsychicListener extends GameActionListener {

    private final PlayerManager playerManager;
    private final JavaPlugin javaPlugin;

    public PsychicListener(PlayerManager playerManager, JavaPlugin javaPlugin) {
        this.playerManager = playerManager;
        this.javaPlugin = javaPlugin;
    }

    @Override
    public void inventoryClickEvent(InventoryClickEvent e, Player p, PlayerData playerData, ItemStack itemStack) {
        Role role = playerData.getVisualRole();
        if(!(role instanceof Psychic psychic))return;

        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;
        if(!itemMeta.getDisplayName().equals(Psychic.TRIGGER_ITEM.getItemMeta().getDisplayName()))return;

        if(psychic.isPowerInUse()){
            p.sendMessage("§cVotre pouvoir est déjà en cours d'utilisation !");
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK,1,1);
            return;
        }
        if(psychic.getRemainingUse() <= 0){
            p.sendMessage("§cVous n'avez plus de pouvoir !");
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK,1,1);
            return;
        }
        itemStack.setAmount(itemStack.getAmount()-1);
        psychic.setPowerInUse(true);
        p.sendMessage("§aScan en cours des joueurs...");
        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_AMBIENT,100,0);
        playerData.addCooldown(new CooldownTask("§a§lScan en cours...",playerData, Psychic.POWER_ANALYSE_TIME,new PsychicEndScanCommand(p,psychic,this.playerManager),this.javaPlugin));
    }
}
