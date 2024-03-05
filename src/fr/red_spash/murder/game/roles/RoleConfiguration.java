package fr.red_spash.murder.game.roles;

import java.util.ArrayList;
import java.util.List;

public class RoleConfiguration {

    private final ArrayList<Role> roles;

    public RoleConfiguration() {
        roles = new ArrayList<>();
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public int getAmountRole(Role role) {
        int amount = 0;
        for(Role r : this.roles){
            if(r.getClass().equals(role.getClass())){
                amount = amount +1;
            }
        }
        return amount;
    }
}
