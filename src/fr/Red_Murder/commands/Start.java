package fr.Red_Murder.commands;

import fr.Red_Murder.Main;
import fr.Red_Murder.event.Murder;
import fr.Red_Murder.tasks.StartMurder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Start implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.getName().equalsIgnoreCase("Red_Spash")){
            Murder.state = Murder.State.Starting;
            new StartMurder().runTaskTimer(Main.getInstance(), 0L, 20L);


        }
        return true;
    }
}
