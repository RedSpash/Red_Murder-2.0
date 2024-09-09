package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.event.BowOnGroundListener;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;

public class BowParticleAndRotation implements Runnable {

    private final BowOnGroundListener bowOnGroundListener;

    public BowParticleAndRotation(BowOnGroundListener bowOnGroundListener) {
        this.bowOnGroundListener = bowOnGroundListener;
    }

    @Override
    public void run() {
        for(Location location : this.bowOnGroundListener.getBowsLocation().keySet()){
            ArmorStand armorStand = this.bowOnGroundListener.getBowsLocation().get(location);

            Location direction = armorStand.getLocation();
            direction.setYaw(direction.getYaw()+2.5f);
            armorStand.teleport(direction);

            armorStand.getWorld().spawnParticle(Particle.END_ROD,armorStand.getLocation().add(0,2,0), 1,0.25,1,0.25, 0.02);
        }
    }
}
