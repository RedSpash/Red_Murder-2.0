package fr.Red_Murder.commands;

import fr.Red_Murder.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;

public class AddSpawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            Player p = (Player) commandSender;
            int SpawnNumber = Main.Spawn.size();
            Location loca = new Location(p.getLocation().getWorld(),p.getLocation().getBlockX()+0.5,p.getLocation().getBlockY(),p.getLocation().getBlockZ()+0.5,p.getLocation().getYaw(),0);
            Main.getInstance().Spawn_Config.set("spawns."+SpawnNumber+".world",loca.getWorld().getName());
            Main.getInstance().Spawn_Config.set("spawns."+SpawnNumber+".X",loca.getBlockX());
            Main.getInstance().Spawn_Config.set("spawns."+SpawnNumber+".Y",loca.getBlockY());
            Main.getInstance().Spawn_Config.set("spawns."+SpawnNumber+".Z",loca.getBlockZ());
            Main.getInstance().Spawn_Config.set("spawns."+SpawnNumber+".yaw",loca.getYaw());
            Main.Spawn.add(loca);
            p.teleport(loca);

            try {
                Main.getInstance().Spawn_Config.save(Main.getInstance().Spawn_File);
            } catch (IOException e) {
                p.sendMessage("§cErreur: impossible de set le spawn !");
                e.printStackTrace();
                return true;
            }
            p.sendMessage("§aUn nouveau spawn vient d'être positioné en :\n"+(loca.getBlockX()+0.5)+" "+loca.getBlockY()+" "+(loca.getBlockZ()+0.5));
            return true;

        }
        return true;
    }
}
