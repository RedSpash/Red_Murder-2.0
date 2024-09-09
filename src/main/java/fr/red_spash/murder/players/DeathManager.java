package fr.red_spash.murder.players;

import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.roles.*;
import fr.red_spash.murder.game.roles.concrete_roles.*;
import fr.red_spash.murder.game.tasks.cooldown.CooldownTask;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.awt.Color;

public class DeathManager {

    private final JavaPlugin main;
    private final PlayerManager playerManager;
    private final BowOnGroundListener bowOnGroundListener;
    private final GameManager gameManager;

    public DeathManager(JavaPlugin main, GameManager gameManager) {
        this.main = main;
        this.playerManager = gameManager.getPlayerManager();
        this.bowOnGroundListener = gameManager.getBowOnGroundListener();
        this.gameManager = gameManager;
    }

    public void killPlayer(Player p, Player damager, String message){
        this.killPlayer(p,damager,message,true);
    }

    public void killPlayer(Player p, Player damager, String message, boolean checkEnd) {
        PlayerData playerData = this.playerManager.getData(p);
        PlayerData playerDataDamager = null;
        if(damager != null){
            playerDataDamager = this.playerManager.getData(damager);
        }

        if(playerDataDamager != null){
            if(playerData.getVisualRole() instanceof Ancient ancient &&
                    (playerDataDamager.getVisualRole() instanceof Murder) && (!ancient.isRespawned())){
                boolean success = this.killAncient(p, ancient);
                if(success) return;
            } else if (playerData.getVisualRole() instanceof Trublion trublion) {
                if(trublion.isPowerAvailable()){
                    trublion.teleportPlayers(this.gameManager);
                }
            }
            this.playDeathEffect(playerData,"§aVous êtes mort par un "+playerDataDamager.getVisualRole().getName().toLowerCase());
        }else{
            this.playDeathEffect(playerData,"§aVous vous êtes suicidé!");
        }

        this.setSpectator(p);

        Role role = playerData.getVisualRole();

        if(role instanceof Detective || (role instanceof Innocent innocent && innocent.isInfiniteBow())){
            this.bowOnGroundListener.addBow(p.getLocation());
        }

        for(Player pl : Bukkit.getOnlinePlayers()){
            if(!pl.getUniqueId().equals(p.getUniqueId())){
                pl.playSound(pl.getLocation().add(new Vector(Utils.generateRandomNumber(-3,3),Utils.generateRandomNumber(-3,3),Utils.generateRandomNumber(-3,3))), Sound.ENTITY_PLAYER_HURT, 3,1);
            }
        }
        if(message != null){
            Bukkit.broadcastMessage("§e"+message);
        }
        if(checkEnd){
            this.gameManager.checkEnd();
        }
    }

    private boolean killAncient(Player p, Ancient ancient) {
        ancient.setRespawned(true);
        double distance = 0.0;
        Location location = null;
        for(Location loca : this.gameManager.getActualMap().getSpawnsLocation()){
            double distanceBetween = loca.distance(p.getLocation());
            if(distanceBetween> distance){
                distance = distanceBetween;
                location = loca;
            }
        }
        if(location != null){
            p.getWorld().strikeLightning(p.getLocation());
            p.teleport(location);
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,20*3,1, false, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,20*3,1, false, false, false));
            Bukkit.broadcastMessage("§eL'"+ancient.getName().toLowerCase()+" vient de réapparaitre!");
            p.sendTitle("§cVous venez de réapparaître!", "§cVous n'avez plus de 2ème chance!",10,20*3,20);
        }else{
            p.sendMessage("§cAucun point de réapparition trouvé! Vous êtes donc mort. Contactez @red_spash par la suite.");
        }
        return location != null;
    }

    public void playDeathEffect(PlayerData playerData){
        this.playDeathEffect(playerData, "§aUn meurtrier vous a tué!");
    }

    public void playDeathEffect(PlayerData playerData, String cause){
        Player p = Bukkit.getPlayer(playerData.getUUID());
        if(p != null && !playerData.isSpectator()){
            p.getLocation().getWorld().spawnParticle(Particle.ITEM_CRACK, p.getLocation().add(0,1,0),50,0.1,0.35,0.1,0.05, new ItemStack(Material.REDSTONE_BLOCK));
            p.setVelocity(p.getLocation().getDirection().multiply(-1.15).add(new Vector(0,0.3,0)));
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20*6, 4, false, false, false));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT,1,1);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP,1,1);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT,1,1);
            p.sendTitle(ChatColor.of(new Color(255,0,0)) +"§lVous êtes mort!",cause,20,20*3,10);

            for(CooldownTask cooldownTask : playerData.getCooldownTasks()){
                cooldownTask.stopTask(false);
            }
        }
    }

    public void setSpectator(Player p) {
        PlayerData playerData = this.playerManager.getData(p);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setAllowFlight(true);
        p.setFlying(true);
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.getUniqueId() != p.getUniqueId()){
                pl.hidePlayer(main, p);
            }
        }
        playerData.setSpectator(true);
    }

    public void killPlayer(Player p, Player damager) {
        this.killPlayer(p,damager,null);
    }

}
