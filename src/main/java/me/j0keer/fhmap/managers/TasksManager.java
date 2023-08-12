package me.j0keer.fhmap.managers;

import lombok.Getter;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.ZombieObject;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Getter
public class TasksManager {
    private final Main plugin;

    private HashMap<String, BukkitTask> tasks = new HashMap<>();

    private int lockedYaw;
    private int lockedPitch;
    private double lockedZAxisCoordinate;

    private boolean haveLocation;

    public TasksManager(Main plugin){
        this.plugin = plugin;

        lockedYaw = 0;
        lockedPitch = 0;
        lockedZAxisCoordinate = 0.0;

        reloadLocation();
        init();
    }

    public void reloadLocation(){
        Location gameLocation = plugin.getGame().getGameLocation();
        if (gameLocation == null){
            haveLocation = false;
            return;
        }
        this.lockedYaw = (int) gameLocation.getYaw();
        this.lockedPitch = (int) gameLocation.getPitch();
        this.lockedZAxisCoordinate = gameLocation.getZ();
        haveLocation = true;
    }

    public void init(){
        BukkitTask animation = tasks.get("animation");
        if (animation != null){
            animation.cancel();
        }
        animation = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getDataManager().getPlayers().values().stream().filter(dp -> dp.isInGame() && !dp.getPlayer().isDead() || (dp.isVillain() && !dp.getPlayer().isDead())).forEach(dp -> {
                    dp.getPlayerAnimationHandler().updateAnimation(dp);
                });
            }
        }.runTaskTimer(plugin, 1L, 0L);
        tasks.put("animation", animation);

        BukkitTask direction = tasks.get("direction");
        if (direction != null){
            direction.cancel();
        }
        direction = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getOnlinePlayers().stream().filter(p-> plugin.getDataManager().getDataPlayer(p).isInGame()).forEach(player -> {
                    Location location = player.getLocation();
                    if (location.getYaw() != lockedYaw || location.getPitch() != lockedPitch || location.getZ() != lockedZAxisCoordinate){
                        resetDirection(player, location);
                    }
                });
                plugin.getGame().getSpawners().values().stream().filter(obj -> !obj.canSpawn() && obj.isCanUpdate()).forEach(obj -> setDirection(obj));
            }
        }.runTaskTimer(plugin, 1L, 0L);

        tasks.put("direction", direction);
    }

    public void setDirection(ZombieObject obj){
        Location location = obj.getZombie().getLocation();
        location.setYaw((float)lockedYaw);
        location.setPitch((float)lockedPitch);
        location.setY(location.getY() + 0.08);
        location.setZ(lockedZAxisCoordinate-0.033);
        obj.getArmorStand().teleport(location);
    }

    public void setDirection(Location loc, ArmorStand stand){
        Location location = loc.clone();
        location.setYaw((float)lockedYaw);
        location.setPitch((float)lockedPitch);
        location.setY(location.getY() + 0.08);
        location.setZ(lockedZAxisCoordinate-0.033);
        stand.teleport(location);
    }

    private void resetDirection(@NotNull Player player, @NotNull Location location) {
        if (!haveLocation) return;
        Vector velocity = player.getVelocity();
        location.setYaw((float)this.lockedYaw);
        location.setPitch((float)this.lockedPitch);
        location.setZ(this.lockedZAxisCoordinate);
        player.teleport(location);
        player.setVelocity(velocity);
    }

}
