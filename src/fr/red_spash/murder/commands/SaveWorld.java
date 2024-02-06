package fr.red_spash.murder.commands;

import fr.red_spash.murder.utils.Utils;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class SaveWorld implements CommandExecutor {
    private final EditWorld editWorld;
    private final ArrayList<UUID> waitingReSend;
    private final JavaPlugin main;

    public SaveWorld(JavaPlugin main, EditWorld editWorld) {
        this.editWorld = editWorld;
        this.main = main;
        this.waitingReSend = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){

            if(strings.length != 1){
                p.sendMessage("§c/saveworld <nom du monde>");
                return true;
            }

            if(!this.editWorld.getEditingWorld().contains(p.getWorld())){
                p.sendMessage("§cImpossible de sauvegarder un monde n'étant pas en mode modification !");
                return true;
            }

            World world = p.getWorld();
            File file = world.getWorldFolder();
            String name = strings[0];

            if(!this.waitingReSend.contains(p.getUniqueId())){
                File configurationFile = new File(file.getPath(),"config.yml");
                if(configurationFile.exists()){
                    FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configurationFile);

                    boolean spawn = fileConfiguration.isSet("spawnlocation");

                    if(!spawn){
                        p.sendMessage("§cVous devez définir un point de spawn majeur avec /spawns setmainspawn");
                        return true;
                    }
                    p.sendMessage("§aVotre monde est prêt à être sauvegardé! Éxecutez la commande à nouveau pour le sauvegarder!");
                    this.waitingReSend.add(p.getUniqueId());
                }else{
                    p.sendMessage("§cOn dirait bien que tu n'as pas configuré le point de spawn et les spawns\nFait §c§l/spawns.");
                }
                return true;
            }

            this.waitingReSend.remove(p.getUniqueId());
            File path = new File(this.main.getDataFolder(), "maps/"+name);

            try {
                p.sendMessage("§aDéchargement du monde...");
                Utils.teleportPlayersAndRemoveWorld(world,true);
                p.sendMessage("§aMonde déchargé !");
                if(path.exists()) {
                    p.sendMessage("§cLe monde existe déjà! Suppresion en cours du monde...");
                    Utils.deleteWorldFiles(path);
                    p.sendMessage("§aSuppresion du monde réussie!");
                }
                p.sendMessage("§aSauvegarde en cours du monde ...");
                Utils.copyDirectory(file.getPath(),path.toString());
                p.sendMessage("§aMonde sauvegardé correctement !");
                p.sendMessage("§aSuppresion des fichiers...");
                Utils.deleteWorldFiles(file);
                p.sendMessage("§aFichiers supprimé corectement !");
                p.sendMessage("§a§lMonde sauvegardé sous le nom de '"+name+"'");
                this.editWorld.removeEditingWorld(world);
            }catch (Exception e){
                e.printStackTrace();
                p.sendMessage("§cImpossible de sauvegarder correctement le monde ! §7("+e.getMessage()+" | "+e.getLocalizedMessage()+")");
            }
            return true;
        }
        return false;
    }
}
