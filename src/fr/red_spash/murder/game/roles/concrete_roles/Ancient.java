package fr.red_spash.murder.game.roles.concrete_roles;

import org.bukkit.Sound;

import java.awt.*;

public class Ancient extends Innocent {
    private boolean respawned = false;

    @Override
    public String getMinecraftRoleColor() {
        return "§f";
    }

    @Override
    public Color getRoleColor() {
        return new Color(211, 211, 211);
    }

    @Override
    public String getName() {
        return "Ancien";
    }

    @Override
    public String shortDescription() {
        return "Vous pouvez réapparaître si vous mourrez d'un murder";
    }

    @Override
    public String getDescription() {
        return "Vous incarnez l'Ancien, une entité presque immortelle qui réapparaît uniquement si tuée par un meurtrier, ajoutant une dimension intrigante à votre présence et semant le doute parmi les autres joueurs.";
    }

    @Override
    public Sound getSound() {
        return Sound.AMBIENT_BASALT_DELTAS_MOOD;
    }

    public boolean isRespawned() {
        return respawned;
    }

    public void setRespawned(boolean respawned) {
        this.respawned = respawned;
    }
}
