package fr.red_spash.murder.players;

import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Schizophrenic;
import fr.red_spash.murder.game.scoreboard.RedScoreBoard;
import fr.red_spash.murder.game.tasks.cooldown.CooldownTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;

public class PlayerData {

    private RedScoreBoard scoreBoard;
    private final ArrayList<CooldownTask> cooldownTasks;
    private final UUID playerUUID;
    private Role role;
    private boolean isSpectator;

    public PlayerData(UUID uuid){
        this.playerUUID = uuid;
        this.cooldownTasks = new ArrayList<>();
        this.setScoreBoard(new RedScoreBoard());
    }

    public void addCooldown(CooldownTask cooldownTask){
        cooldownTask.setVisible(cooldownTasks.isEmpty());

        cooldownTasks.add(cooldownTask);
        this.updateCooldown();
    }

    public void updateCooldown(){
        ArrayList<CooldownTask> cooldownTasksCopy = new ArrayList<>();
        for(CooldownTask cooldownTask : this.cooldownTasks){
            if(!cooldownTask.isRunning()){
                cooldownTasksCopy.add(cooldownTask);
            }
        }

        Collections.sort(cooldownTasksCopy);

        this.cooldownTasks.clear();
        for(int i = 0; i < cooldownTasksCopy.size(); i++){
            cooldownTasksCopy.get(i).setVisible(i == 0);
            this.cooldownTasks.add(cooldownTasksCopy.get(i));
        }


    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public Role getVisualRole() {
        if(role instanceof Schizophrenic schizophrenic && (schizophrenic.getSubRole() != null)){
            return schizophrenic.getSubRole();
        }
        return role;
    }

    public Role getTrueRole(){
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public UUID getUUID() {
        return playerUUID;
    }

    public List<CooldownTask> getCooldownTasks() {
        return new ArrayList<>(this.cooldownTasks);
    }

    public CooldownTask removeCooldownTask(CooldownTask cooldownTask) {
        return this.cooldownTasks.remove(this.cooldownTasks.indexOf(cooldownTask));
    }

    public RedScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard() {
        this.setScoreBoard(this.scoreBoard);
    }

    public void setScoreBoard(RedScoreBoard scoreboard) {
        Player p = Bukkit.getPlayer(this.getUUID());
        if(p != null){
            this.scoreBoard = scoreboard;
            p.setScoreboard(this.scoreBoard.getBoard());
        }
    }
}
