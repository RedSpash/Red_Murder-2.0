package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class EndGameTask implements Runnable{


    private final Role winnerRole;
    private final GameManager gameManager;
    private final BukkitTask runnable;
    private final GameMap actualMap;
    private int timer = 30;

    private final List<Color> colors = List.of(
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.AQUA,
            Color.BLUE,
            Color.PURPLE
            );

    public EndGameTask(GameManager gameManager, Role winnerRole, JavaPlugin javaPlugin) {
        this.gameManager = gameManager;
        this.winnerRole = winnerRole;
        this.actualMap = gameManager.getActualMap();

        this.sendTitles();

        this.runnable = Bukkit.getScheduler().runTaskTimer(javaPlugin, this, 0,20);
    }

    private void sendTitles() {
        String title = "§a§lÉgalité!";
        String subTitle = this.getSubTitle();

        for(Player p : Bukkit.getOnlinePlayers()){
            if(this.winnerRole != null){
                title = ChatColor.of(new java.awt.Color(255, 0, 0)) +"§lVous avez perdu!";
                PlayerData playerData = this.gameManager.getPlayerManager().getData(p.getUniqueId());
                if(playerData.getVisualRole() != null){
                    if(playerData.getVisualRole().isMurder() == this.winnerRole.isMurder()){
                        title = ChatColor.of(new java.awt.Color(0,255,0)) +"§lVous avez gagné!";
                    }
                }else{
                    title = "§c§lFin de la partie!";
                }
            }
            p.sendTitle(title,subTitle,10,20*5,20);

            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH,0.3F,1);
        }
    }

    private String getSubTitle() {
        String subTitle = "§aPersonne n'a gagné!";

        String murderPlurial = "s";
        String murderVerb = "nt";
        int murderAmount = 0;

        for(PlayerData playerData : this.gameManager.getPlayerManager().getAllPlayerData()){
            if(playerData.getVisualRole() != null && (playerData.getVisualRole().isMurder())){
                murderAmount = murderAmount + 1;
            }
        }

        if(murderAmount <= 1){
            murderVerb = "";
            murderPlurial = "";
        }

        if(this.winnerRole != null){
            if(this.winnerRole.isMurder()){
                subTitle = this.winnerRole.getMinecraftRoleColor()+"§lLe"+murderPlurial+" "+this.winnerRole.getName()+" gagne"+murderVerb+" la partie!";
            }else{
                subTitle = this.winnerRole.getMinecraftRoleColor()+"§lLes "+this.winnerRole.getName()+" gagnent la partie!";
            }
        }
        return subTitle;
    }

    @Override
    public void run() {

        if(timer <= 0){
            this.gameManager.resetGame();
            this.stop();
            return;
        }

        if(timer % 10 == 0 || timer <= 5){
            Bukkit.broadcastMessage("§cRetour au lobby dans "+timer+" secondes !");
        }

        if(this.actualMap != null && timer >= 7){
            for(Player p : Bukkit.getOnlinePlayers()){
                for(int i =0; i<=Utils.generateRandomNumber(0,13);i++){
                    Location location = p.getLocation().add(
                            Utils.generateRandomNumber(-20,20),
                            Utils.generateRandomNumber(-10,10),
                            Utils.generateRandomNumber(-20,20)
                    );

                    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                    firework.setSilent(1 <= Utils.generateRandomNumber(0,2));
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.setPower(Utils.generateRandomNumber(0,2));
                    FireworkEffect fireworkEffect = FireworkEffect.builder()
                            .with(FireworkEffect.Type.values()[Utils.generateRandomNumber(0,FireworkEffect.Type.values().length-1)])
                            .flicker(0 == Utils.generateRandomNumber(0,1))
                            .withColor(colors.get(Utils.generateRandomNumber(0,colors.size()-1)))
                            .withFade(colors.get(Utils.generateRandomNumber(0,colors.size()-1)))
                            .build();
                    fireworkMeta.addEffect(fireworkEffect);
                    firework.setFireworkMeta(fireworkMeta);
                }
            }
        }

        timer = timer - 1;
    }

    public void stop(){
        this.runnable.cancel();
    }
}
