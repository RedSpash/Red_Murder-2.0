package fr.Red_Murder.roles;

import fr.Red_Murder.event.Murder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class RoleManager {

    private String player;
    private Murder.Role role;
    private Murder.Role subrole = null;
    private Boolean Spectator = false;

    public RoleManager(Murder.Role r){
        this.role = r;
    }

    public Murder.Role get_role_type(){
        return role;
    }

    public void set_specator(Boolean specator){
        this.Spectator = specator;
    }

    public void set_sub_roletype(Murder.Role sub_roletype){
        this.subrole = sub_roletype;
    }

    public boolean get_specator(){ return this.Spectator; }

    public Murder.Role get_subrole_type(){
        return subrole;
    }

    public String get_description(){
        switch (role){
            case Murder:
                return "§eVotre but est de tuer tout le monde !\n§eCependant le Schizophrène peut être avec vous.\n";

            case Detective:
                return "§eVotre but est de sauver les innocents !\n§eVous possédez un arc qui peut tirer une flèche toutes les 20 secondes.\n";

            case Schizophrène:
                return "§eVous êtes indécis, qui tuer ?\n§eAttendez un instant avant d'entrer en action..\n";

            case Innocent:
                return "§eVotre but est de tuer le(s) meurtrier(s)\n§eAu bout de 10 ors ramassés vous gagnez un arc et une flèche !\n";

        }
        return null;
    }

    public String get_role_name(){
        switch (role) {
            case Murder:
                return "Meurtrier";
            case Detective:
                return "Detective";
            case Schizophrène:
                return "Schizophrènes";
            case Innocent:
                return "Innocent";
        }
        return "§f§l";
    }

    public String get_role_color(){
        switch (role) {
            case Murder:
                return "§c";
            case Detective:
                return "§a";
            case Schizophrène:
                return "§4";
            case Innocent:
                return "§9";
        }
        return "§f";
    }

    public void give_main_item(){
        Player p = Bukkit.getPlayerExact(this.player);
        if(subrole == Murder.Role.Murder) {
            ItemStack item = new ItemStack(Material.IRON_SWORD);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName("§c§lArme du meurtrier");
            im.spigot().setUnbreakable(true);
            item.setItemMeta(im);

            p.getInventory().setItem(1, item);
            return;
        }else if(subrole == Murder.Role.Detective) {
            ItemStack arc = new ItemStack(Material.BOW);
            ItemMeta arc_im = arc.getItemMeta();
            arc_im.setDisplayName("§6§lArc du Detective");
            arc_im.spigot().setUnbreakable(true);
            arc.setItemMeta(arc_im);
            p.getInventory().setItem(0, arc);
            p.getInventory().setItem(27, new ItemStack(Material.ARROW));
            return;
        }
        switch (role){
            case Murder:
                ItemStack item = new ItemStack(Material.IRON_SWORD);
                ItemMeta im = item.getItemMeta();
                im.setDisplayName("§c§lArme du meurtrier");
                im.spigot().setUnbreakable(true);
                item.setItemMeta(im);

                p.getInventory().setItem(1,item);
                return;
            case Detective:
                ItemStack arc = new ItemStack(Material.BOW);
                ItemMeta arc_im = arc.getItemMeta();
                arc_im.setDisplayName("§6§lArc du Detective");
                arc_im.spigot().setUnbreakable(true);
                arc.setItemMeta(arc_im);
                p.getInventory().setItem(0,arc);
                p.getInventory().setItem(27,new ItemStack(Material.ARROW));
                return;

        }




        return;
    }

    public void give_bow(){
        Player p = Bukkit.getPlayerExact(this.player);
        if(p.getInventory().contains(Material.BOW)){
            if(p.getInventory().contains(Material.ARROW)){
                p.getInventory().addItem(new ItemStack(Material.ARROW));
            }else{
                p.getInventory().setItem(27,new ItemStack(Material.ARROW));
            }
        }else{
            ItemStack item = new ItemStack(Material.BOW);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName("§6Arc");
            im.spigot().setUnbreakable(true);
            item.setItemMeta(im);
            p.getInventory().setItem(0,item);
            if(p.getInventory().contains(Material.ARROW)){
                p.getInventory().addItem(new ItemStack(Material.ARROW));
            }else{
                p.getInventory().setItem(27,new ItemStack(Material.ARROW));
            }
        }
    }

    public void set_Player(String pl){
        this.player = pl;
    }
}
