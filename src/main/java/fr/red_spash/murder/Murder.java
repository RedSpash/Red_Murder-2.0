package fr.red_spash.murder;

import fr.red_spash.murder.commands.EditWorld;
import fr.red_spash.murder.commands.MuteVocal;
import fr.red_spash.murder.commands.SaveWorld;
import fr.red_spash.murder.commands.Spawns;
import fr.red_spash.murder.commands.StartGame;
import fr.red_spash.murder.commands.TeleportTo;
import fr.red_spash.murder.event.BowOnGroundListener;
import fr.red_spash.murder.event.ChatListener;
import fr.red_spash.murder.event.ServerListener;
import fr.red_spash.murder.game.GameManager;
import fr.red_spash.murder.game.GameState;
import fr.red_spash.murder.game.events.GameActionListener;
import fr.red_spash.murder.game.events.GameListener;
import fr.red_spash.murder.game.events.RolesListener;
import fr.red_spash.murder.game.roles.Role;
import fr.red_spash.murder.game.roles.RoleConfiguration;
import fr.red_spash.murder.game.roles.concrete_roles.Ancient;
import fr.red_spash.murder.game.roles.concrete_roles.Detective;
import fr.red_spash.murder.game.roles.concrete_roles.Electrician;
import fr.red_spash.murder.game.roles.concrete_roles.Innocent;
import fr.red_spash.murder.game.roles.concrete_roles.Lucky;
import fr.red_spash.murder.game.roles.concrete_roles.Psychic;
import fr.red_spash.murder.game.roles.concrete_roles.Schizophrenic;
import fr.red_spash.murder.game.roles.concrete_roles.Spy;
import fr.red_spash.murder.game.roles.concrete_roles.Trublion;
import fr.red_spash.murder.game.roles.concrete_roles.Vagabond;
import fr.red_spash.murder.game.roles.listener.DetectiveListener;
import fr.red_spash.murder.game.roles.listener.ElectricianListener;
import fr.red_spash.murder.game.roles.listener.MurderListener;
import fr.red_spash.murder.game.roles.listener.PsychicListener;
import fr.red_spash.murder.game.roles.listener.SpyListener;
import fr.red_spash.murder.game.roles.listener.TrublionListener;
import fr.red_spash.murder.game.roles.listener.VagabondListener;
import fr.red_spash.murder.game.roles.tasks.SpyTask;
import fr.red_spash.murder.maps.MapManager;
import fr.red_spash.murder.players.DeathManager;
import fr.red_spash.murder.players.PlayerManager;
import fr.red_spash.murder.spawn.MapsManagerListener;
import fr.red_spash.murder.spawn.RoleManagerListener;
import fr.red_spash.murder.spawn.SpawnManager;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Murder extends JavaPlugin {

  public static final String OPEN_YOUR_INVENTORY_FOR_ITEMS = "§aOuvrez votre inventaire pour accédez à vos items.";
  private EditWorld editWorld;
  private GameManager gameManager;
  private SpawnManager spawnManager;
  private RoleConfiguration roleConfiguration;
  private MapManager mapManager;
  private PlayerManager playerManager;
  private BowOnGroundListener bowOnGroundListener;
  private DeathManager deathManager;
  private ArrayList<GameActionListener> gameActionListeners;
  private RoleManagerListener roleManagerListener;
  private SpyListener spyListener;
  private List<Role> allRoles;

  private void createMainsManagers() {
    this.roleConfiguration = new RoleConfiguration();
    this.spawnManager = new SpawnManager();

    this.mapManager = new MapManager(this, this.spawnManager);
    this.playerManager = new PlayerManager(this, this.spawnManager);
    this.bowOnGroundListener = new BowOnGroundListener(playerManager, this);
    this.initializeOnlinePlayers(playerManager);

    this.gameManager = new GameManager(this, mapManager, playerManager, bowOnGroundListener, roleConfiguration, this.spawnManager);
    this.deathManager = new DeathManager(this, this.gameManager);

    Bukkit.getScheduler().runTaskLater(this, () -> {
      for (Player player : Bukkit.getOnlinePlayers()) {
        spawnManager.playTitle(player);
      }
    }, 2);
  }

  private void createRolesListeners() {
    this.gameActionListeners = new ArrayList<>();
    this.spyListener = new SpyListener(this.playerManager, this);
    gameActionListeners.add(this.spyListener);
    gameActionListeners.add(new ElectricianListener(this.playerManager));
    //gameActionListeners.add(new ParanoiacListener());
    gameActionListeners.add(new TrublionListener(this.gameManager));
    gameActionListeners.add(new VagabondListener(this));
    gameActionListeners.add(new PsychicListener(this.playerManager, this));
  }

  private void registerCommands() {
    this.editWorld = new EditWorld(this, this.mapManager, this.spawnManager);
    getCommand("editWorld").setExecutor(this.editWorld);
    getCommand("saveWorld").setExecutor(new SaveWorld(this, this.editWorld, this.spawnManager));
    getCommand("spawns").setExecutor(new Spawns(this, this.editWorld));
    getCommand("muteVocal").setExecutor(new MuteVocal(this, this.playerManager));
    getCommand("start").setExecutor(new StartGame(this.gameManager));
    getCommand("teleportTo").setExecutor(new TeleportTo(this.mapManager));
  }

  private void registerEventListener() {
    this.roleManagerListener = new RoleManagerListener(this.roleConfiguration, this.allRoles);

    PluginManager pm = Bukkit.getServer().getPluginManager();
    pm.registerEvents(new ServerListener(this.gameManager, deathManager, this.spawnManager), this);
    pm.registerEvents(new ChatListener(this.playerManager, this.gameManager), this);
    pm.registerEvents(new MapsManagerListener(this.mapManager, this.gameManager), this);
    pm.registerEvents(roleManagerListener, this);
    pm.registerEvents(new DetectiveListener(playerManager, deathManager, this), this);
    pm.registerEvents(new RolesListener(this.gameManager, gameActionListeners), this);
    pm.registerEvents(new MurderListener(this.gameManager, deathManager, this), this);
    pm.registerEvents(new GameListener(), this);
    pm.registerEvents(bowOnGroundListener, this);
  }

  @Override
  public void onDisable() {
    this.editWorld.deleteAllWorlds();
    if (this.gameManager.getGameState() != GameState.WAITING) {
      this.gameManager.resetGame();
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.spawnManager.teleportSpawn(player);
    }
  }

  @Override
  public void onEnable() {
    this.allRoles = List.of(
        new fr.red_spash.murder.game.roles.concrete_roles.Murder(),
        new Detective(),
        new Schizophrenic(),
        new Ancient(),
        new Electrician(),
        new Lucky(),
        new Spy(),
        new Vagabond(),
        //new Paranoiac(),
        new Psychic(),
        new Trublion(),
        new Innocent()
    );
    this.createMainsManagers();
    this.createRolesListeners();
    this.registerEventListener();
    this.registerCommands();

    Bukkit.getServer().getScheduler().runTaskTimer(this, new SpyTask(this.playerManager, this.spyListener), 0, 1);
    Bukkit.getConsoleSender().sendMessage("§c§lRed_Murder prêt !");
  }

  private void initializeOnlinePlayers(PlayerManager playerManager) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      playerManager.insertPlayer(p);
    }
  }
}
