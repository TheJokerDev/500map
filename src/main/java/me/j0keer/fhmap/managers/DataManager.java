package me.j0keer.fhmap.managers;

import lombok.Getter;
import me.j0keer.fhmap.type.DataPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import me.j0keer.fhmap.Main;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class DataManager implements Listener {
    private Main plugin;
    private HashMap<UUID, DataPlayer> players;

    public DataManager(Main plugin){
        this.plugin = plugin;
        plugin.listener(this);
        players = new HashMap<>();
    }

    public DataPlayer getDataPlayer(Player p){
        DataPlayer dataPlayer = players.get(p.getUniqueId());
        if (dataPlayer == null){
            dataPlayer = new DataPlayer(p.getUniqueId());
            players.put(p.getUniqueId(), dataPlayer);
        }
        return dataPlayer;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        players.put(e.getPlayer().getUniqueId(), new DataPlayer(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent e){
        if (players.containsKey(e.getPlayer().getUniqueId())){
            DataPlayer player = players.get(e.getPlayer().getUniqueId());
            //To-Do on removing player from collection.
            players.remove(e.getPlayer().getUniqueId());
            if (player.isInGame()) {
                player.setInGame(false);
            }
        }
    }

}
