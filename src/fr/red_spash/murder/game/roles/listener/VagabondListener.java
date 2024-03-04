package fr.red_spash.murder.game.roles.listener;

import fr.red_spash.murder.game.commands.MessageCommand;
import fr.red_spash.murder.game.events.GameActionListener;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Vagabond;
import fr.red_spash.murder.game.tasks.CooldownTask;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.red_spash.murder.game.roles.concrete_roles.Vagabond.*;

public class VagabondListener extends GameActionListener {

    private final JavaPlugin main;

    public VagabondListener(JavaPlugin main) {
        this.main = main;
    }

    @Override
    public void inventoryClickEvent(InventoryClickEvent e, Player p, PlayerData playerData, ItemStack itemStack){
        Role role = playerData.getVisualRole();
        if(!(role instanceof Vagabond vagabond))return;

        if(!itemStack.hasItemMeta())return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(!itemMeta.hasDisplayName())return;
        String displayName = itemMeta.getDisplayName();

        if(displayName.equals(VAGABOND_TELEPORT_BEACON.getItemMeta().getDisplayName())){
            if(vagabond.getBeaconLocation() == null){
                if(p.getLocation().add(0,-1,0).getBlock().getType() == Material.AIR){
                    p.sendMessage("§cVous devez être sur le sol !");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,1);
                    return;
                }
                Location location = p.getLocation();
                location.setY(location.getBlockY());
                vagabond.setBeaconLocation(location);
                ArmorStand armorStand = (ArmorStand) p.getWorld().spawnEntity(location.clone().add(0,-0.5,0), EntityType.ARMOR_STAND);
                for(EquipmentSlot equipmentSlot : EquipmentSlot.values()){
                    armorStand.addEquipmentLock(equipmentSlot, ArmorStand.LockType.REMOVING_OR_CHANGING);
                }
                armorStand.getEquipment().setHelmet(new ItemStack(Material.BEACON));
                armorStand.teleport(armorStand.getLocation().add(0,-0.75,0));
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setMarker(true);
                p.sendMessage("§aBalise positionnée! Utilisez l'item de téléportation pour vous téléporter à la balise.");
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE,1,1);
                p.getInventory().setItem(VAGABOND_SLOT,VAGABOND_TELEPORT_ITEM.clone());
            }else{
                p.sendMessage("§cVotre balise est déjà posée, impossible de la changer!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,1);
            }
            p.closeInventory();
        } else if (displayName.equals(VAGABOND_TELEPORT_ITEM.getItemMeta().getDisplayName())) {
            if(vagabond.getBeaconLocation() != null){
                if(vagabond.getRemainingUse() > 0){
                    if(vagabond.getLastTeleportation() + 1000 * VAGABOND_TIME_BETWEEN_TELEPORTATION < System.currentTimeMillis()){
                        vagabond.removeUtilisation(1);
                        itemStack.setAmount(vagabond.getRemainingUse());
                        p.teleport(vagabond.getBeaconLocation());
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,MOTIONLESS_TIME*20+20,2));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS,MOTIONLESS_TIME*20,2));
                        vagabond.setLastTeleportation(System.currentTimeMillis());
                        playerData.addCooldown(new CooldownTask(role.getMinecraftRoleColor()+"Prochaine téléportation",playerData,VAGABOND_TIME_BETWEEN_TELEPORTATION,new MessageCommand(p,"§a§lVous pouvez désormais utilisez votre téléportation",Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN),this.main));
                    }else{
                        p.sendMessage("§cVous devez attendre "+ Utils.round((double) ((vagabond.getLastTeleportation() + 1000 * VAGABOND_TIME_BETWEEN_TELEPORTATION) - System.currentTimeMillis()) /1000,1)+" secondes!");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,1);
                        p.closeInventory();
                    }
                }else{
                    p.sendMessage("§cVous n'avez plus d'utilisation restante!");
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,1);
                }
            }else{
                p.sendMessage("§cVous devez d'abord positionner la balise !");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS,1,1);
            }
            p.closeInventory();
        }
    }

}
