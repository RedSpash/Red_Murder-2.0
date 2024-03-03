package fr.red_spash.murder.utils;

import fr.red_spash.murder.Murder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    private Utils(){

    }

    public static int generateRandomNumber(Integer min, Integer max){
        max = max +1;
        return (int) (Math.random()*(max-min)) + min;
    }

    public static void teleportPlayersAndRemoveWorld(World world, boolean save) {
        for(Player p : world.getPlayers()){
            p.teleport(Murder.SPAWN);
            p.sendMessage("§cLe monde vient d'être détruit! Vous êtes désormais au spawn.");
        }
        Bukkit.unloadWorld(world,save);
    }

    public static void deleteWorldFiles(File worldFolder) {
        if (worldFolder.exists() && worldFolder.isDirectory()) {
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFiles(file);
                    }
                    try {
                        Files.delete(file.toPath());
                    } catch (IOException e) {
                        Bukkit.getLogger().warning("Impossible de supprimer le fichier: "+file.getName()+"!");
                    }
                }
            }
            try {
                Files.delete(worldFolder.toPath());
            } catch (IOException e) {
                Bukkit.getLogger().warning("Impossible de supprimer le monde: "+worldFolder.getName()+"!");
            }
        }
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) {
        try {
            Files.walk(Paths.get(sourceDirectoryLocation))
                    .forEach(source -> {
                        Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                                .substring(sourceDirectoryLocation.length()));
                        try {
                            if(!destination.toString().contains("uid.dat")){
                                Files.copy(source, destination);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
