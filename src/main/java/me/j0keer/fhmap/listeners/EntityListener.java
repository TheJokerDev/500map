package me.j0keer.fhmap.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Direction;
import me.j0keer.fhmap.handler.ZombieAnimationHandler;
import me.j0keer.fhmap.type.DataPlayer;
import me.j0keer.fhmap.type.ZombieObject;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityListener implements Listener {
    private final Main plugin;

    public EntityListener(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onZombieMove(EntityMoveEvent event){
        @NotNull LivingEntity entity = event.getEntity();
        if (entity instanceof Zombie zombie){
            List<ZombieObject> zombies = new ArrayList<>(plugin.getGame().getSpawners().values().stream().filter(obj -> obj.isZombie(zombie)!=null).toList());
            if (!zombies.isEmpty()){
                zombies.forEach(obj -> {
                    double speed = getXSpeed(event);
                    obj.getHandler().setXSpeed(speed);
                    obj.getHandler().setDirection(getDirection(obj.getHandler(), speed));
                    obj.updateAnimation();
                });
            }
        }
    }

    private static Direction getDirection(ZombieAnimationHandler handler, double speed) {
        if (speed > 0.0) {
            return Direction.RIGHT;
        } else {
            return speed < 0.0 ? Direction.LEFT : handler.getDirection();
        }
    }

    private double getXSpeed(@NotNull EntityMoveEvent event) {
        return event.getTo().getX() - event.getFrom().getX();
    }

    @EventHandler
    public void onSwimming(EntityToggleSwimEvent event){
        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        DataPlayer dataPlayer = plugin.getDataManager().getDataPlayer(player);

        if(dataPlayer.ignoreCancelled && !event.isSwimming())
            event.setCancelled(true);
    }
}
