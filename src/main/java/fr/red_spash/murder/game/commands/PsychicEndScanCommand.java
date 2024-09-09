package fr.red_spash.murder.game.commands;

import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Psychic;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PsychicEndScanCommand extends Command{
    private final Psychic psychic;
    private final PlayerManager playerManager;

    public PsychicEndScanCommand(Player p, Psychic psychic, PlayerManager playerManager) {
        super(p);
        this.psychic = psychic;
        this.playerManager = playerManager;
    }

    @Override
    public void execute() {
        List<Entity> nearbyEntities = p.getNearbyEntities(Psychic.POWER_RADIUS,Psychic.POWER_RADIUS,Psychic.POWER_RADIUS);
        ArrayList<PlayerData> players = new ArrayList<>();
        for(Entity entity : nearbyEntities){
            if(entity instanceof Player pl){
                PlayerData targetData = this.playerManager.getData(pl);
                if(pl.getUniqueId() != p.getUniqueId()
                        && (!targetData.isSpectator())){
                    players.add(targetData);
                }
            }
        }
        this.psychic.removeOneUse();
        this.psychic.setPowerInUse(false);
        if(players.isEmpty()){
            p.sendMessage("§cAucun joueur n'étais dans la zone!");
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,5,0);
        }else{
            PlayerData target = players.get(Utils.generateRandomNumber(0,players.size()-1));
            Role role = target.getTrueRole();
            p.playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,5,0);
            p.sendMessage("§aVoici le rôle d'un joueur dans un rayon de "+Psychic.POWER_RADIUS+" blocks autour de vous: "+ ChatColor.of(role.getRoleColor()) +role.getName());
        }
    }
}
