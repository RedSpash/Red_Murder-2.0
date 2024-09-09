package fr.red_spash.murder.event;

import fr.red_spash.murder.Murder;
import fr.red_spash.murder.game.roles.concrete_roles.Detective;
import fr.red_spash.murder.game.roles.concrete_roles.Innocent;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.tasks.BowParticleAndRotation;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;

public class BowOnGroundListener implements Listener {

    private static final int BOW_COMPASS_SLOT = 2;
    private static final ItemStack BOW_COMPASS = new ItemStackBuilder(Material.COMPASS)
            .setName("§6Position de l'arc")
            .setLore("§7Indique la position","§7de l'arc !")
            .toItemStack();
    private final HashMap<Location, ArmorStand> bowsLocation = new HashMap<>();
    private final PlayerManager playerManager;

    public BowOnGroundListener(PlayerManager playerManager, Murder murder) {
        this.playerManager = playerManager;
        Bukkit.getScheduler().runTaskTimer(murder, new BowParticleAndRotation(this),1,1);
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        if(this.bowsLocation.isEmpty())return;

        Player p = e.getPlayer();
        for (Map.Entry<Location,ArmorStand> entry : bowsLocation.entrySet()) {
            Location location = entry.getKey();
            ArmorStand armorStand = entry.getValue();
            if(location.distance(p.getLocation()) <= 1.0){
                PlayerData playerData = this.playerManager.getData(p);
                Role role = playerData.getVisualRole();
                if(role instanceof Innocent innocent && !playerData.isSpectator()){
                    armorStand.remove();
                    this.bowsLocation.remove(location);
                    Bukkit.broadcastMessage("§aL'arc du détective vient d'être ramassé!");
                    innocent.setInfiniteBow(true);
                    innocent.giveBow(p);
                    p.sendMessage("§aVous recevez l'arc du détective!");
                    p.playSound(p.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE,1,2);
                    for(Player player : Bukkit.getOnlinePlayers()){
                        player.getInventory().setItem(BOW_COMPASS_SLOT,null);
                    }
                }
            }
        }
    }


    public void addBow(Location location) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setRightArmPose(new EulerAngle(1.0,1.5,0));
        armorStand.setGravity(false);
        for(EquipmentSlot equipmentSlot : EquipmentSlot.values()){
            if(equipmentSlot == EquipmentSlot.HAND){
                armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            }else{
                armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.ADDING);
            }
        }
        armorStand.setCollidable(false);
        armorStand.setMarker(true);
        armorStand.getEquipment().setItemInMainHand(Detective.DETECTIVE_BOW);
        armorStand.teleport(location);

        Bukkit.broadcastMessage("§a§lL'arc est au sol !");
        for(Player p : Bukkit.getOnlinePlayers()){
            p.playSound(p.getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS,1,1);
            PlayerData playerData = this.playerManager.getData(p.getUniqueId());
            if(!playerData.isSpectator()){
                p.getInventory().setItem(BOW_COMPASS_SLOT, BOW_COMPASS);
                p.setCompassTarget(location);
            }
        }

        this.bowsLocation.put(location, armorStand);
    }

    public Map<Location, ArmorStand> getBowsLocation() {
        return bowsLocation;
    }

    public void clearBowsLocations() {
        this.bowsLocation.clear();
    }
}
