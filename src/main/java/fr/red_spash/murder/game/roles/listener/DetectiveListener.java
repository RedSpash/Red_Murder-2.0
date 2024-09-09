package fr.red_spash.murder.game.roles.listener;

import fr.red_spash.murder.game.commands.GiveArrowCommand;
import fr.red_spash.murder.game.roles.concrete_roles.Detective;
import fr.red_spash.murder.game.roles.concrete_roles.Innocent;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.tasks.cooldown.CooldownTask;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DetectiveListener implements Listener {

    private final PlayerManager playerManager;
    private final JavaPlugin main;
    private final DeathManager deathManager;

    public DetectiveListener(PlayerManager playerManager, DeathManager deathManager, JavaPlugin main){
        this.playerManager = playerManager;
        this.main = main;
        this.deathManager = deathManager;
    }

    @EventHandler
    public void shootBowEvent(EntityShootBowEvent e){
        if(!(e.getEntity() instanceof Player player))return;
        PlayerData playerData = this.playerManager.getData(player);
        Role role = playerData.getVisualRole();

        if(!(role instanceof Detective) &&
                !(role instanceof Innocent innocent && innocent.isInfiniteBow()))return;
        if(player.getInventory().containsAtLeast(new ItemStack(Material.ARROW),2))return;

        playerData.addCooldown(new CooldownTask("§6§lARC",playerData,9,new GiveArrowCommand(player), this.main));
    }

    @EventHandler
    public void hitByArrow(ProjectileHitEvent e){
        if(!(e.getEntity() instanceof Arrow arrow))return;
        if(!(e.getHitEntity() instanceof Player playerHit))return;
        if(!(arrow.getShooter() instanceof Player shooter))return;

        PlayerData dataShooter = this.playerManager.getData(shooter);
        PlayerData dataHit = this.playerManager.getData(playerHit);

        Role shooterRole = dataShooter.getVisualRole();
        Role hitRole = dataHit.getVisualRole();

        if(shooterRole.isMurder() && !hitRole.isMurder()) {
            this.deathManager.killPlayer(playerHit, shooter, "Un "+hitRole.getName()+" est mort par un murder!");
            e.getEntity().remove();
        }else if(!shooterRole.isMurder() && hitRole.isMurder()){
            this.deathManager.killPlayer(playerHit, shooter, "§aUn murder vient d'être tué!");
            e.getEntity().remove();
        }else if(!hitRole.isMurder()) {
            if(playerHit.getUniqueId().equals(shooter.getUniqueId())){
                this.deathManager.killPlayer(shooter, shooter, "Un "+hitRole.getName().toLowerCase()+" vient de se tirer dessus! Comme c'est embarassant...");
            }else{
                String ePrefixed = "e ";
                if(List.of("a","e","i","o","u").contains(String.valueOf(shooterRole.getName().charAt(0)).toLowerCase())){
                    ePrefixed = "'";
                }
                this.deathManager.killPlayer(playerHit, shooter, "Un "+hitRole.getName().toLowerCase()+" est mort par un "+shooterRole.getName().toLowerCase()+"!", false);
                this.deathManager.killPlayer(shooter, null, "§cL"+ePrefixed+shooterRole.getName().toLowerCase()+" était incapable de supporter l'idée de se tromper, il a donc mis fin à ses jours!");
            }
            e.getEntity().remove();
        }
    }

}
