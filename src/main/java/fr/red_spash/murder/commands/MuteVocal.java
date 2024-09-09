package fr.red_spash.murder.commands;

import fr.red_spash.murder.Murder;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.players.PlayerManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MuteVocal implements CommandExecutor, TabCompleter {

  private final PlayerManager playerManager;
  private final HashMap<UUID, Boolean> muteMap = new HashMap<>();
  private boolean muteAll = false;
  public MuteVocal(Murder murder, PlayerManager playerManager) {
    this.playerManager = playerManager;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
      if (!(commandSender instanceof Player p)) {
          return false;
      }
      if (!p.isOp()) {
          return false;
      }
      if (strings.length == 0) {
          return false;
      }
    if (strings[0].equalsIgnoreCase("all")) {
      this.muteAll = !muteAll;
      for (PlayerData playerData : this.playerManager.getAllPlayerData()) {
        Player pl = Bukkit.getPlayer(playerData.getUUID());
        if (pl != null) {
          if (muteAll) {
            if (!pl.isOp()) {
              playerData.getVoiceManager().forceMute();
            }
          } else {
            playerData.getVoiceManager().forceUnMute();
          }
        }
      }
      if (muteAll) {
        Bukkit.broadcastMessage("§cTout le monde est désormais muet !");
      } else {
        Bukkit.broadcastMessage("§cTout le monde a retrouvé sa voix !");
      }
    } else {
      Player target = Bukkit.getPlayer(strings[0]);
      if (target == null) {
        p.sendMessage("§cJoueur introuvable!");
        return true;
      }

      if (!muteMap.containsKey(target.getUniqueId())) {
        muteMap.put(target.getUniqueId(), true);
      }
      boolean mute = !muteMap.getOrDefault(target.getUniqueId(), false);
      muteMap.put(target.getUniqueId(), mute);

      PlayerData playerData = this.playerManager.getData(target.getUniqueId());
      if (mute) {
        playerData.getVoiceManager().forceMute();
        target.sendMessage("§c" + p.getName() + " vient de vous rendre muet !");
      } else {
        playerData.getVoiceManager().forceUnMute();
        target.sendMessage("§a" + p.getName() + " vient de vous redonner la voix !");
      }
      p.sendMessage("§a" + target.getName() + "muet: " + mute);
    }
    return false;
  }

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    ArrayList<String> completer = new ArrayList<>();
    for (Player p : Bukkit.getOnlinePlayers()) {
      completer.add(p.getName());
    }

    completer.add("all");
    return completer;
  }
}
