package fr.red_spash.murder.game.tasks;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Detective;
import fr.red_spash.murder.game.roles.concrete_roles.Murder;
import fr.red_spash.murder.maps.GameMap;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class EndGameTask implements Runnable{


    private final Role winnerRole;
    private final GameManager gameManager;
    private final BukkitTask runnable;
    private final GameMap actualMap;
    private int timer = 15;

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
                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,timer*20,2,false,false,false));
                    }
                }else{
                    title = "§c§lFin de la partie!";
                }
            }
            p.sendTitle(title,subTitle,10,20*5,20);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH,0.3F,1);
        }

        ArrayList<PlayerData> murders = new ArrayList<>();
        ArrayList<PlayerData> detectives = new ArrayList<>();
        ArrayList<PlayerData> other = new ArrayList<>();
        StringBuilder allDetails = new StringBuilder();

        for(PlayerData playerData : this.gameManager.getPlayerManager().getAllPlayerData()){
            if(playerData.getTrueRole() != null){
                if(playerData.getVisualRole() instanceof Murder){
                    if(playerData.getTrueRole() instanceof Murder){
                        murders.add(0,playerData);
                    }else{
                        murders.add(playerData);
                    }
                } else if (playerData.getVisualRole() instanceof Detective) {
                    if(playerData.getTrueRole() instanceof Detective){
                        detectives.add(0,playerData);
                    }else{
                        detectives.add(playerData);
                    }
                }else{
                    other.add(playerData);

                    allDetails.append(this.getRoleInformation(playerData));
                }
            }
        }

        StringBuilder murder = this.getMessageDetails("§e§lMurder" + Utils.plural(murders.size()) + ":",murders);
        StringBuilder detective = this.getMessageDetails("§e§lDétective" + Utils.plural(detectives.size()) + ":",detectives);
        System.out.println(murder);
        System.out.println(detective);
        for(PlayerData playerData : detectives){
            allDetails.insert(0,this.getRoleInformation(playerData));
        }

        for(int i = murders.size()-1; i>=0; i--){
            allDetails.insert(0,this.getRoleInformation(murders.get(i)));
        }

        for(Player p : Bukkit.getOnlinePlayers()){
            TextComponent textComponent = new TextComponent(
                    "§7§l---------------------------------------------" +
                    "\n§f"+
                    "\n"+murder+
                    "\n"+detective+
                    "\n§7Passez votre souris ici pour plus d'information !"+
                    "\n§7§l---------------------------------------------"
            );
            textComponent.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§a§lDétails des rôles:\n"+allDetails.substring(0,allDetails.length()-1)).create()
            ));
            p.spigot().sendMessage(textComponent);
        }
    }

    private String getRoleInformation(PlayerData playerData) {
        Player player = Bukkit.getPlayer(playerData.getUUID());
        String name = "Joueur inconnu";
        if(player != null){
            name = player.getName();
        }
        Role role = playerData.getTrueRole();
        Role visualRole = playerData.getVisualRole();
        String details = "";
        if(!role.getClass().equals(visualRole.getClass())){
            details = " §7("+visualRole.getMinecraftRoleColor()+visualRole.getName()+"§7)";
        }
        return role.getMinecraftRoleColor()+name+" §7§l- "+role.getMinecraftRoleColor()+"§l"+role.getName()+details+"\n";
    }

    private StringBuilder getMessageDetails(String start, ArrayList<PlayerData> playerInfos) {
        StringBuilder stringBuilder = new StringBuilder(start);
        stringBuilder.append("\n");
        for(PlayerData playerData : playerInfos){
            Player player = Bukkit.getPlayer(playerData.getUUID());
            String name = "Joueur inconnu";
            if(player != null){
                name = player.getName();
            }
            Role visualRole = playerData.getVisualRole();
            Role trueRole = playerData.getTrueRole();

            stringBuilder
                    .append("§f  §f  §7§l- ")
                    .append(trueRole.getMinecraftRoleColor())
                    .append(name);

            if(!visualRole.getClass().equals(trueRole.getClass())){
                stringBuilder
                        .append(" §7(")
                        .append(trueRole.getMinecraftRoleColor())
                        .append(trueRole.getName())
                        .append("§7)");
            }
            stringBuilder.append("\n");

        }
        return stringBuilder;
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
                for(int i =0; i<=Utils.generateRandomNumber(0,5);i++){
                    Location location = p.getLocation().add(
                            Utils.generateRandomNumber(-10,10),
                            Utils.generateRandomNumber(-2,5),
                            Utils.generateRandomNumber(-10,10)
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
