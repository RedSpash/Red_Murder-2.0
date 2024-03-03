package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.game.roles.Role;
import org.bukkit.Sound;

import java.awt.*;

public class Innocent extends Role {

    private boolean infiniteBow = false;

    public void setInfiniteBow(boolean infiniteBow) {
        this.infiniteBow = infiniteBow;
    }

    public boolean isInfiniteBow() {
        return infiniteBow;
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§a";
    }

    @Override
    public Color getRoleColor() {
        return new Color(106, 255, 0);
    }

    @Override
    public String getName() {
        return "Innocent";
    }

    @Override
    public String shortDescription() {
        return "Survivez, collaborez et démasquez le meurtrier parmi vous.";
    }

    @Override
    public String getDescription() {
        return "Votre objectif en tant qu'innocent est de survivre en collaborant avec les autres joueurs, partageant des informations et observant attentivement le comportement de chacun pour identifier le meurtrier, créant ainsi une dynamique de confiance et de méfiance au sein du groupe.";
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_ILLUSIONER_MIRROR_MOVE;
    }

}
