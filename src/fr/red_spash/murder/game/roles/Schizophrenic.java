package fr.red_spash.murder.game.roles;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Schizophrenic implements Role{
    @Override
    public String getRoleColor() {
        return "§4";
    }

    @Override
    public String getName() {
        return "Schizophrène";
    }

    @Override
    public String shortDescription() {
        return "Créez de la confusion entre innocent et meurtrier.";
    }

    @Override
    public String getDescription() {
        return "Incarnant le rôle unique de schizophrène, vous devez créer de la confusion en oscillant entre des comportements qui semblent à la fois innocents et meurtriers, ajoutant une dimension intrigante au jeu en défiant les attentes des autres participants et en manipulant habilement les perceptions.";
    }

    @Override
    public Sound getSound() {
        return Sound.ENTITY_VINDICATOR_HURT;
    }

    @Override
    public void giveItems(Player p) {

    }
}
