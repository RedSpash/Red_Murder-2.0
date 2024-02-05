package fr.Red_Murder.tasks;

import fr.Red_Murder.Main;
import fr.Red_Murder.event.Murder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class EndMurder extends BukkitRunnable {
    double timer = 15;
    ArrayList<Player> survivor = new ArrayList<>();
    @Override
    public void run() {
        Murder.Spawn_Gold();
        Murder.state = Murder.State.End;
        if(timer == 15.0){
            init_winner();
        }
        if(timer >= 5.0){
            for(Player pl : survivor){
                int x = pl.getLocation().getBlockX() + Main.random_number(-10,10);
                int y = pl.getLocation().getBlockY();
                int z = pl.getLocation().getBlockZ() + Main.random_number(-10,10);
                Location spawn_firework = new Location(pl.getWorld(),x,y,z);
                Firework firework = (Firework) spawn_firework.getWorld().spawnEntity(spawn_firework, EntityType.FIREWORK);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                Random random = new Random();
                FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(getColor(random.nextInt(10) + 1)).withFade(getColor(random.nextInt(10) + 1)).with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]).trail(random.nextBoolean()).build();
                fireworkMeta.setPower(0);
                fireworkMeta.addEffect(effect);
                firework.setFireworkMeta(fireworkMeta);

            }
        }
        if (timer == 15.0) {
            Bukkit.broadcastMessage("§e§lArrêt de la partie dans 15 secondes !");
            tick();
        } else if (timer == 10.0) {
            Bukkit.broadcastMessage("§e§lArrêt de la partie dans 10 secondes §e§l!");
            tick();
        }else if(timer <= 0.0){
            for(Player pl : Bukkit.getOnlinePlayers()){
                pl.removePotionEffect(PotionEffectType.INVISIBILITY);
                pl.setFlying(false);
                pl.setAllowFlight(false);
                pl.getInventory().clear();
                for(Player p : Bukkit.getOnlinePlayers()){
                    pl.showPlayer(p);
                }
                pl.teleport(new Location(pl.getWorld(),0,80,0,-90,0));
                for(ArmorStand as : Murder.Dead_Body){
                    as.remove();
                }
                Murder.Dead_Body.clear();
                if(Murder.BowArmor != null){
                    Murder.BowArmor.remove();
                    Murder.BowArmor = null;
                    Murder.BowLocation = null;
                }
            }
            for(Entity entity : Bukkit.getWorld("world").getEntities()){
                if(entity instanceof Item){
                    entity.remove();
                }
            }
            cancel();
        }else if( timer <= 3.5 && (int) timer == timer +0.0){
            Bukkit.broadcastMessage("§e§lArrêt de la partie dans §c§l"+((int) timer)+" secondes §e§l!");
            tick();
        }

        timer = timer -0.5;

    }

    private void init_winner() {
        survivor.clear();
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(Murder.Roles.containsKey(pl.getName())){
                if(!Murder.Roles.get(pl.getName()).get_specator()){
                    survivor.add(pl);
                }
            }
        }
        Murder.Roles.clear();
    }


    private void tick() {
        for(Player pl :Bukkit.getOnlinePlayers()){
            pl.playSound(pl.getLocation(), Sound.CLICK,1,1);
        }
    }
    private static Color getColor(final int i) {
        switch (i) {
            case 1:
                return Color.AQUA;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.FUCHSIA;
            case 4:
                return Color.LIME;
            case 5:
                return Color.NAVY;
            case 6:
                return Color.ORANGE;
            case 7:
                return Color.PURPLE;
            case 8:
                return Color.RED;
            case 9:
                return Color.WHITE;
            case 10:
                return Color.YELLOW;
        }
        return null;
    }
}
