package me.j0keer.fhmap.listeners;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.Button;
import me.j0keer.fhmap.type.DataPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListeners implements Listener {
    private Main plugin;

    public InteractListeners(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        ItemStack click = e.getItem();

        if (click == null){
            return;
        }
        if (click.getType() == Material.AIR){
            return;
        }
        if (dp.getItems().keySet().size()>0){
            for (Button b : dp.getItems().values()){
                if (b.getItem().build(p).isSimilar(click)){
                    b.executePhysicallyItemsActions(e);
                    break;
                }
            }
        }
    }

}
