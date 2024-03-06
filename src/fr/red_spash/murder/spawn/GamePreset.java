package fr.red_spash.murder.spawn;

import fr.red_spash.murder.game.roles.Role;
import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.Array;
import java.util.*;

public class GamePreset {

    private final String name;
    private final List<Role> roleList;
    private final ArrayList<String> descriptionList;

    public GamePreset(String name, Role... roles) {
        this(name,List.of(roles));
    }

    public GamePreset(String name, List<Role> roles) {
        this.name = name;
        this.roleList = roles;
        this.descriptionList = new ArrayList<>();

        this.initializeDescriptionList();
    }

    private void initializeDescriptionList() {
        LinkedHashMap<Role, Integer> rolesNames = new LinkedHashMap<>();
        for(Role role : this.getRoleList()){
            Role findedRole = null;
            for(Role key : rolesNames.keySet()){
                if(key.getClass().equals(role.getClass())){
                    findedRole = key;
                    break;
                }
            }
            if(findedRole != null){
                rolesNames.put(findedRole, rolesNames.get(findedRole)+1);
            }else{
                rolesNames.put(role, 1);
            }
        }

        for(Map.Entry<Role, Integer> entry : rolesNames.entrySet()){
            Role role = entry.getKey();
            int amount = entry.getValue();
            this.descriptionList.add(" §7- "+ ChatColor.of(role.getRoleColor())+amount+" "+role.getName());
        }
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription(){
        return descriptionList;
    }

    public int getAmountOfRole(){
        return this.roleList.size();
    }
}
