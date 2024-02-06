package fr.red_spash.murder.game.roles;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Innocent implements Role{
    @Override
    public String getRoleColor() {
        return "§a";
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
    public void giveItems(Player p) {

    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_ILLUSIONER_MIRROR_MOVE;
    }
}
