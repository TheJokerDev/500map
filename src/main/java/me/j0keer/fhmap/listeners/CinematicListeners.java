package me.j0keer.fhmap.listeners;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.utils.LocationUtil;
import me.thejokerdev.spcinematics.type.Cinematic;
import me.thejokerdev.spcinematics.utils.events.CinematicChangeEvent;
import me.thejokerdev.spcinematics.utils.events.CinematicFinishEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class CinematicListeners implements Listener {
    private final Main plugin;

    public CinematicListeners(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChange(CinematicChangeEvent event){
        Cinematic before = event.getBefore();
        Cinematic after = event.getAfter();
        boolean hasAfter = after != null;

        if (hasAfter) {
            plugin.getUtils().debugToDev("Cambio de cinemática: &e" + before.getId() + " &7-> &c" + after.getId());
        } else {
            plugin.getUtils().debugToDev("Cambio de cinemática: &e" + before.getId() + " &7-> &cfin.");
        }
    }

    @EventHandler
    public void onFinish(CinematicFinishEvent event){
        Cinematic cinematic = event.getCinematic();
        plugin.getUtils().debugToDev("Cinemática finalizada: &e" + cinematic.getId());
        if (cinematic.getId().equals("init_A")){
            List<Player> players = new ArrayList<>();
            players.add(plugin.getServer().getPlayer("J0keer"));
            players.add(plugin.getServer().getPlayer("SpreenDMC"));

            players.forEach(player -> {
                if (player == null) return;
                player.teleport(LocationUtil.getLocation("world,-424.5,94.5,533.5,-1.4,160.3"));
            });
        }
    }
}
