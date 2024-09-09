package fr.red_spash.murder.game.roles.concrete_roles;

import org.bukkit.Sound;

import java.awt.*;

public class Paranoiac extends Innocent{

    public static final int PARANOIAC_EFFECT_TIME = 25;
    public static final int MAX_USE = 2;
    private static final int PARANOIAC_DISTANCE_POWER = 5;
    private int remainingUse = PARANOIAC_EFFECT_TIME;

    @Override
    public String getName() {
        return "Paranoïaque";
    }

    @Override
    public String shortDescription() {
        return super.shortDescription();
    }

    @Override
    public String getDescription() {
        return "Vous possédez la possibilité de rendre un joueur paranoïaque pendant "+PARANOIAC_EFFECT_TIME+" secondes jusqu'à "+MAX_USE+" fois par partie! Pour rendre une personne paranoïaque faites clique droit en visant un joueur à moins de "+PARANOIAC_DISTANCE_POWER+" blocks.";
    }

    @Override
    public Color getRoleColor() {
        return new Color(97, 0, 116);
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§5";
    }

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU1MjU2ODkyM2U1NDU2YWIzMWRhOThlYzMyN2RiMGFjMDY5YTY4NzZkZTRhOWZkZDZlMGJjNWQwMTI3YzMxOSJ9fX0=";
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_WITCH_CELEBRATE;
    }

    public void removeOneRemainingUse() {
        this.remainingUse = this.remainingUse - 1;
    }
}
