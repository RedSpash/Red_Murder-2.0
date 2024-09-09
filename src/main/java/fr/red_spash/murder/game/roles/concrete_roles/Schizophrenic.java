package fr.red_spash.murder.game.roles.concrete_roles;

import fr.red_spash.murder.game.roles.Role;
import org.bukkit.Sound;

import java.awt.*;

public class Schizophrenic extends Role {

    private Role subRole;

    public void setSubRole(Role subRole) {
        this.subRole = subRole;
    }

    public boolean hasRole(){
        return this.subRole != null;
    }

    @Override
    public String getHeadUUID() {
        return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVjNGJhZmYxOGQwZGY4YmZjZDRmYWJkYmM0OTQ3ZTI5NzVlODI3MmRmNzQzOWQzMTFmNmJkMTM2ZGZiYzE4ZiJ9fX0=";
    }

    @Override
    public String getMinecraftRoleColor() {
        return "§4";
    }

    @Override
    public Color getRoleColor() {
        return new Color(165, 0, 0);
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
