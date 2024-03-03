package fr.red_spash.murder.skin_utils;

import java.util.Collection;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
public class SkinUtils {

    private final JavaPlugin main;
    private Player player;
    private Collection<PotionEffect> effects;
    private Location location;
    private int slot;

    public SkinUtils(Player player, JavaPlugin javaPlugin) {
        this.player = player;
        this.main = javaPlugin;
    }



    public void changeSkin(String data, String signature) {
        EntityPlayer ePlayer = ((CraftPlayer) player).getHandle();
        GameProfile profile = ePlayer.getBukkitEntity().getProfile();
        PropertyMap pMap = profile.getProperties();
        pMap.removeAll("textures");
        pMap.put("textures", new Property("textures", data, signature));
        updateSkin();
    }



    public void updateSkin() {
        effects = player.getActivePotionEffects();
        location = player.getLocation();
        slot = player.getInventory().getHeldItemSlot();

        CraftWorld world = (CraftWorld) location.getWorld();
        CraftPlayer craftPlayer = ((CraftPlayer) player);

        // REMOVE
        craftPlayer.getHandle().c.a(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.a, craftPlayer.getHandle()));
        // ADD
        craftPlayer.getHandle().c.a(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.a.b, craftPlayer.getHandle()));

        // Respawn player in the same world
        craftPlayer.getHandle().c.a(new PacketPlayOutRespawn(world.getHandle().aa(), world.getHandle().ac(), world.getEnvironment().getId()+0L, this.getGamemode(), this.getGamemode(), false, false, (byte) 0,craftPlayer.getHandle().gm(), 0));
        player.teleport(location);
        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.hidePlayer(this.main, this.player);
            pl.showPlayer(this.main,this.player);
        }

        Bukkit.getScheduler().runTaskLater(this.main, () -> {
            player.getInventory().setHeldItemSlot(slot);
            player.addPotionEffects(effects);
            player.setExp(player.getExp());
            player.setHealth(player.getHealth()-0.0001);
            player.openInventory(player.getEnderChest());
            player.closeInventory();
        }, 2);
    }



    public EnumGamemode getGamemode() {
        return switch (this.player.getGameMode()) {
            case SURVIVAL -> EnumGamemode.a;
            case CREATIVE -> EnumGamemode.b;
            case SPECTATOR -> EnumGamemode.d;
            default -> EnumGamemode.c;
        };
    }




}
