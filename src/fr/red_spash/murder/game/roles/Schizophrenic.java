package fr.red_spash.murder.game.roles;

import org.bukkit.Sound;

public class Schizophrenic extends Role{

    private Role subRole;

    public void setSubRole(Role subRole) {
        this.subRole = subRole;
    }

    public boolean hasRole(){
        return this.subRole != null;
    }

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
    public boolean isMurder() {
        return this.hasRole() && this.subRole instanceof Murder;
    }

    public Role getSubRole() {
        return this.subRole;
    }
}
