package fr.red_spash.murder.game.tasks;


import fr.red_spash.murder.game.commands.Command;
import fr.red_spash.murder.players.PlayerData;
import fr.red_spash.murder.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CooldownTask implements Runnable, Comparable<CooldownTask> {

    private final double maxTime;
    private final PlayerData playerData;
    private final Player player;
    private final String prefix;
    private final boolean showTask;
    private double time;
    private final Command command;
    private final BukkitTask bukkitTask;
    private boolean visible;

    public CooldownTask(String prefix,PlayerData playerData, double time, Command command, JavaPlugin main){
        this(prefix,playerData,time,command,main,true);
    }
    public CooldownTask(String prefix,PlayerData playerData, double time, Command command, JavaPlugin main, boolean showTask){
        this.prefix = prefix;
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(main, this,1,1);
        this.playerData = playerData;
        this.maxTime = time;
        this.time = time;
        this.command = command;
        this.player = Bukkit.getPlayer(playerData.getUUID());
        this.visible = false;
        this.showTask = showTask;
    }

    public Command getCommand() {
        return command;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if(playerData.isSpectator()){
            if(this.bukkitTask != null){
                this.stopTask();
            }
            return;
        }
        if(this.time <= 0){
            if(this.bukkitTask != null){
                this.command.execute();
                this.stopTask();
            }
            return;
        }

        if(this.visible && this.showTask && this.player != null && this.player.isOnline()) {
                StringBuilder actionBarMessage = new StringBuilder();
                double percentage = this.time * 100 / this.maxTime;

                for (int i = 1; i <= 50; i++) {
                    String color = "§a";

                    if (i * 2 <= percentage) {
                        color = "§c";
                    }

                    actionBarMessage.insert(0, color + "|");
                }
                actionBarMessage.append(" §7§l- §7").append(Utils.round(this.time, 1)).append("sec");
                this.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(prefix+" §7§l- "+ actionBarMessage));
        }
        this.time = this.time - 0.05;
    }

    public void stopTask() {
        this.stopTask(true);
    }

    public void stopTask(boolean update) {
        this.bukkitTask.cancel();
        if(update){
            this.playerData.updateCooldown();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isRunning() {
        return this.bukkitTask.isCancelled();
    }

    @Override
    public int compareTo(CooldownTask task) {
        double taskTime = this.time;
        double otherTaskTime = task.time;

        if(!this.showTask){
            taskTime = Double.MAX_VALUE;
        }

        if(!task.showTask){
            otherTaskTime = Double.MAX_VALUE;
        }
        return Double.compare(taskTime, otherTaskTime);
    }

    public double time() {
        return this.time;
    }
}
