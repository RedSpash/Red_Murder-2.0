package fr.red_spash.murder.players;

import fr.red_spash.murder.game.tasks.CooldownTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

    private final HashMap<UUID, PlayerData> playerDataHashMap;
    private final JavaPlugin main;

    public PlayerManager(JavaPlugin main) {
        this.main = main;
        playerDataHashMap = new HashMap<>();
    }

    public PlayerData insertPlayer(Player player){
        return this.insertPlayer(player, new PlayerData(player.getUniqueId()));
    }

    public PlayerData insertPlayer(Player player, PlayerData playerData){
        this.playerDataHashMap.put(player.getUniqueId(), playerData);
        return playerData;
    }

    public PlayerData getData(Player player){
        return this.getData(player.getUniqueId());
    }

    public PlayerData getData(UUID uuid){
        return playerDataHashMap.getOrDefault(uuid, null);
    }

    public void resetData() {
        for(PlayerData playerData : this.playerDataHashMap.values()){
            playerData.setSpectator(false);
            playerData.setRole(null);
        }
        this.resetCooldowns();
    }

    public List<PlayerData> getAllPlayerData() {
        return new ArrayList<>(this.playerDataHashMap.values());
    }

    public void showAllPlayers() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                if (!p.getUniqueId().equals(pl.getUniqueId())) {
                    p.showPlayer(this.main, pl);
                }
            }
        }
    }

    public void resetPlayers() {
        this.showAllPlayers();
        for(Player p : Bukkit.getOnlinePlayers()){
            p.setFlying(false);
            p.setAllowFlight(false);
            p.setPlayerListName(null);
            p.getInventory().clear();
            for(PotionEffect potionEffect : p.getActivePotionEffects()){
                p.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public void resetCooldowns() {
        for(PlayerData playerData : this.getAllPlayerData()){
            ArrayList<CooldownTask> tasks = new ArrayList<>(playerData.getCooldownTasks());
            playerData.getCooldownTasks().clear();

            for(CooldownTask cooldownTask : tasks){
                cooldownTask.stopTask();
            }
        }
    }
}
