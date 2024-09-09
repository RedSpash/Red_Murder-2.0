package fr.red_spash.murder.game.roles.listener;

import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.events.GameActionListener;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.concrete_roles.Trublion;
import fr.red_spash.murder.players.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrublionListener extends GameActionListener {

  private final GameManager gameManager;

  public TrublionListener(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @Override
  public void inventoryClickEvent(InventoryClickEvent e, Player p, PlayerData playerData, ItemStack itemStack) {
    Role role = playerData.getVisualRole();
      if (!(role instanceof Trublion trublion)) {
          return;
      }

      if (!itemStack.hasItemMeta()) {
          return;
      }
    ItemMeta itemMeta = itemStack.getItemMeta();
      if (!itemMeta.hasDisplayName()) {
          return;
      }
      if (!itemMeta.getDisplayName().equals(Trublion.TRIGGER_POWER.getItemMeta().getDisplayName())) {
          return;
      }
    if (trublion.isPowerAvailable()) {
      itemStack.setAmount(0);
      trublion.teleportPlayers(this.gameManager);
      Bukkit.broadcastMessage("§eUn " + ChatColor.of(trublion.getRoleColor()) + trublion.getName() + " §evient d'utiliser son pouvoir !");
    } else {
      p.sendMessage("§cVous avez déjà utilisé votre pouvoir !");
      p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
      p.closeInventory();
    }
  }
}
