package fr.red_spash.murder.game.roles.tasks;

import fr.red_spash.murder.game.roles.concrete_roles.Paranoiac;
import fr.red_spash.murder.utils.Utils;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class ParanoiacTask implements Runnable {

  private final Player target;
  private final JavaPlugin main;
  private final BukkitTask bukkitTask;
  private final ArrayList<Location> ghostBlock = new ArrayList<>();
  private int timer = 0;
  private Random random = new Random();


  public ParanoiacTask(Player target, JavaPlugin main) {
    this.target = target;
    this.main = main;

    this.bukkitTask = Bukkit.getScheduler().runTaskTimer(this.main, this, 1, 1);
    this.target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 7, false, false, false));
  }

  @Override
  public void run() {
    if (this.timer % 20 == 0
        || this.timer % 20 == 2) {
      target.playSound(this.getRandomLocationAround(), Sound.ENTITY_WARDEN_HEARTBEAT, 1, 1);
    }

    if (this.timer % 10 * 10 == 0) {
      target.playSound(this.getRandomLocationAround(), Sound.AMBIENT_CAVE, 1, 0);
    }
    int offsetX = Utils.generateRandomNumber(5, 7);
    int offsetZ = Utils.generateRandomNumber(5, 7);
    if (Utils.generateRandomNumber(0, 1) == 0) {
      offsetX = offsetX * -1;
    }
    if (Utils.generateRandomNumber(0, 1) == 0) {
      offsetZ = offsetZ * -1;
    }
    if (Utils.generateRandomNumber(0, 3) == 0) {
      offsetZ = 0;
    }

    if (Utils.generateRandomNumber(0, 3) == 0) {
      offsetX = 0;
    }
    Location location = this.target.getLocation().add(offsetX, Utils.generateRandomNumber(1, 7), offsetZ);
    this.ghostBlock.add(location);
    this.target.sendBlockChange(location, Material.BEDROCK.createBlockData());

    if (Utils.generateRandomNumber(0, 10) == 0) {
      this.target.playSound(this.getRandomLocationAround(), Sound.BLOCK_GRASS_STEP, Utils.generateRandomNumber(1, 20) / 10f, 1);
    }

    this.target.spawnParticle(Particle.FLASH, this.getRandomLocationAround(), 10, 0.5, 0.5, 0.5, 1);
    this.target.spawnParticle(Particle.WHITE_ASH, target.getLocation(), 100, 5, 5, 5, 1);
    this.target.getWorld().spawnParticle(Particle.SMOKE_LARGE, target.getLocation(), 50, 5, 5, 5, 0.1);
    target.setFreezeTicks(7);
    if (Utils.generateRandomNumber(0, 5) == 0) {
      target.playSound(this.getRandomLocationAround(), Sound.ENTITY_PLAYER_HURT, Utils.generateRandomNumber(1, 20) / 10f, 1);
    }

    if (this.timer % 20 * 10 == 0) {
      target.playSound(this.getRandomLocationAround(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Utils.generateRandomNumber(1, 20) / 10f, 0);
    }
    if (Utils.generateRandomNumber(0, 10) == 0) {
      target.playSound(this.getRandomLocationAround(), Sound.ENTITY_WARDEN_AMBIENT, 1, 0);
    }
    if (Utils.generateRandomNumber(0, 2) == 0) {
      target.playSound(this.getRandomLocationAround(), Sound.ENTITY_WARDEN_HEARTBEAT, 1, 0);
    }
    if (this.timer == Paranoiac.PARANOIAC_EFFECT_TIME * 20) {
      target.getLocation().setYaw(target.getLocation().getYaw() + 45);
      target.getLocation().setPitch(target.getLocation().getPitch() - 30);
      this.timer = 0;
      this.stop();
    }

    this.timer = this.timer + 1;
  }

  private void stop() {
    this.target.removePotionEffect(PotionEffectType.DARKNESS);
    for (Location location : this.ghostBlock) {
      this.target.sendBlockChange(location, location.getWorld().getBlockAt(location).getBlockData());
    }
    this.bukkitTask.cancel();
  }

  private Location getRandomLocationAround() {
    return target.getLocation().add(
        Utils.generateRandomNumber(-10, 10),
        Utils.generateRandomNumber(-10, 10),
        Utils.generateRandomNumber(-10, 10)
    );
  }
}
