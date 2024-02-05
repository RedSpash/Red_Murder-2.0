package fr.Red_Murder.tasks;

import fr.Red_Murder.Main;
import fr.Red_Murder.event.Murder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

public class StartMurder extends BukkitRunnable {
    int timer = 15;

    @Override
    public void run() {
        if (Murder.state != Murder.State.Starting){
            cancel();
        }
        if (timer <= 0){
            Bukkit.broadcastMessage("§2§lLa partie démarre !");
            Murder.state = Murder.State.Playing;
            timer = 15;
            Murder.Start_Game();
            this.cancel();
            return;
        }
        switch (timer){
            case 30:
                Bukkit.broadcastMessage("§e§lLa partie va démarrer dans 30 secondes !");
                tick(false);
                break;
            case 15:
                Bukkit.broadcastMessage("§e§lLa partie va démarrer dans 15 secondes !");
                tick(false);
                break;
            case 10:
                Bukkit.broadcastMessage("§e§lLa partie va démarrer dans 10 secondes §e§l!");
                tick(false);
                break;
        }
        if( timer <= 5){
            Bukkit.broadcastMessage("§e§lLa partie va démarrer dans §c§l"+timer+" secondes §e§l!");
            tick(true);

        }
        timer = timer -1;



    }

    public void tick(Boolean title){
        for(Player pl :Bukkit.getOnlinePlayers()){
            pl.playSound(pl.getLocation(), Sound.CLICK,1,1);
            if (title){
                Title t = new Title("§c"+timer+" secondes","§eL'aventure commence ...",0,25,0);

                pl.sendTitle(t);
            }
        }
    }

}
