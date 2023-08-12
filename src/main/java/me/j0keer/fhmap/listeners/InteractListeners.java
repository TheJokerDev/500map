package me.j0keer.fhmap.listeners;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.Button;
import me.j0keer.fhmap.type.DataPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
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
        if (click.getType() == Material.VILLAGER_SPAWN_EGG){
            if (dp.isInGame()){
                Inventory inv = Bukkit.createInventory(null, InventoryType.MERCHANT);
                p.openInventory(inv);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMoveItem(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (dp.isInGame()){
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR){
                return;
            }
            if (item.getType() == Material.VILLAGER_SPAWN_EGG){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event){
        Player p = event.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (dp.isInGame()){
            ItemStack item = event.getItemDrop().getItemStack();
            if (item.getType() == Material.AIR){
                return;
            }
            if (item.getType() == Material.VILLAGER_SPAWN_EGG){
                event.setCancelled(true);
            }
        }
    }

}
