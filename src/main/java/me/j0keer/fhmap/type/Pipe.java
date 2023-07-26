package me.j0keer.fhmap.type;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.utils.Cuboid;
import me.j0keer.fhmap.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class Pipe implements Listener {
    private final Main plugin;
    private final String id;
    public Cuboid reg;
    public Location tpLoc;

    private Direction direction = Direction.UP;


    public Pipe(Main plugin, String id){
        this.plugin = plugin;
        this.id = id;
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("game.pipe."+id);
        if (section == null) return;
        if (section.get("reg") != null){
            Location loc1 = LocationUtil.getLocation(section.getString("reg.loc1"));
            Location loc2 = LocationUtil.getLocation(section.getString("reg.loc2"));

            if (loc1 != null && loc2 != null){
                reg = new Cuboid(loc1, loc2);
            }
        }

        if (section.get("tp") != null){
            tpLoc = LocationUtil.getLocation(section.getString("tp"));
        }

        if (section.get("direction") != null){
            direction = Direction.valueOf(section.getString("direction", "UP").toUpperCase());
        }

        plugin.listener(this);
    }

    public void setRegion(Location loc1, Location loc2){
        reg = new Cuboid(loc1, loc2);
        plugin.getConfig().set("game.pipe."+id+".reg.loc1", LocationUtil.getString(loc1, true));
        plugin.getConfig().set("game.pipe."+id+".reg.loc2", LocationUtil.getString(loc2, true));
        plugin.saveConfig();
    }

    public void setLocation(Location loc) {
        tpLoc = loc;
        plugin.getConfig().set("game.pipe."+id+".tp", LocationUtil.getString(loc, false));
        plugin.saveConfig();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        plugin.getConfig().set("game.pipe."+id+".direction", direction.name());
        plugin.saveConfig();
    }

    public void unregister(){
        HandlerList.unregisterAll(this);
    }

    public void teleport(DataPlayer dp){
        list.add(dp.getName());
        dp.teleport(tpLoc, direction, (bool) -> list.remove(dp.getName()));
    }

    private List<String> list = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if (list.contains(p.getName())) return;

        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (!dp.isInGame() || dp.isDead()) return;

        if (reg == null || tpLoc == null) return;

        if (reg.isIn(p)){
            if (!p.isSneaking()) return;
            teleport(dp);
        }
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
