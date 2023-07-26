package me.j0keer.fhmap.type;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.utils.Cuboid;
import me.j0keer.fhmap.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class DeathRegion implements Listener {
    private Cuboid region;
    private final Main plugin;

    public DeathRegion(Main plugin, String string){
        this.plugin = plugin;
        String[] split = string.split(";");
        Location loc1 = LocationUtil.getLocation(split[0]);
        Location loc2 = LocationUtil.getLocation(split[1]);
        if (loc1 != null && loc2 != null){
            region = new Cuboid(loc1, loc2);
        }
        plugin.listener(this);
    }

    public void unregister(){HandlerList.unregisterAll(this);};

    private List<String> list = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if (list.contains(p.getName())) return;

        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (!dp.isInGame() || dp.isDead()) return;

        if (region == null) return;

        if (region.isIn(p)){
            dp.death();
            list.add(p.getName());
            new BukkitRunnable() {
                @Override
                public void run() {
                    list.remove(p.getName());
                }
            }.runTaskLater(plugin, 20L * 5);
        }
    }
}
