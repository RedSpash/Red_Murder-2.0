package fr.red_spash.murder.game.roles.concrete_roles;

import org.bukkit.Sound;

import java.awt.*;

public class Lucky extends Innocent {
    @Override
    public String getMinecraftRoleColor() {
        return "§e";
    }

    @Override
    public Color getRoleColor() {
        return new Color(255, 234, 0);
    }

    @Override
    public String getName() {
        return "Chanceux";
    }

    @Override
    public String shortDescription() {
        return "Vous récupérez 2x plus d'or";
    }

    @Override
    public String getDescription() {
        return "Vous incarnez le Chanceux, doté d'un don rare qui vous permet de récupérer deux fois plus d'or au sol que les autres. Cette chance exceptionnelle vous offre un avantage considérable, vous permettant d'amasser richesses et ressources avec une efficacité accrue.";
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_PLAYER_LEVELUP;
    }
}
