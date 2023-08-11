package me.j0keer.fhmap.type;

import lombok.Getter;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.handler.ZombieAnimationHandler;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class ZombieObject {
    private final Main plugin;

    private final Location spawnLoc;

    private Zombie zombie;
    private ArmorStand armorStand;
    private ZombieAnimationHandler handler;

    private boolean canUpdate = false;
    private boolean canSpawn = true;

    public ZombieObject(Main plugin, Location loc){
        this.plugin = plugin;
        this.spawnLoc = loc;
        this.handler = new ZombieAnimationHandler();
    }

    public void spawn(Player player) {
        if (!canSpawn) return;
        if (zombie != null) {
            zombie.remove();
        }
        if (armorStand != null){
            armorStand.remove();
        }
        zombie = spawnLoc.getWorld().spawn(spawnLoc, Zombie.class);
        zombie.setInvisible(true);
        zombie.setCollidable(false);
        zombie.setAI(false);
        zombie.setSilent(true);
        zombie.setAdult();
        zombie.getEquipment().clear();

        armorStand = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);

        canUpdate = true;
        updateAnimation();
        canUpdate = false;

        initSpawnAnimation(player);
    }

    public void initSpawnAnimation(Player player){
        plugin.getTasksManager().setDirection(this);

        Location originalLoc = armorStand.getLocation();
        Location loc = armorStand.getLocation().clone();
        loc.subtract(0,  2, 0);

        double y = originalLoc.getBlockY();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (loc.getY() >= y) {
                    canUpdate = true;
                    zombie.setAI(true);
                    zombie.setSilent(false);
                    zombie.setTarget(player);
                    cancel();
                    return;
                }
                armorStand.teleport(loc);
                loc.add(0, 0.05, 0);
            }
        }.runTaskTimer(plugin, 1L, 0L);
    }

    public ZombieObject isZombie(Zombie zombie){
        if (this.zombie == null || this.zombie.isDead()){
            return this.zombie != null && this.zombie.isDead() ? this : null;
        }
        return this.zombie.getUniqueId().equals(zombie.getUniqueId()) ? this : null;
    }

    public boolean canSpawn(){
        if (armorStand == null || zombie == null){
            return true;
        }

        return armorStand.isDead() && zombie.isDead() && canSpawn;
    }

    public void remove() {
        remove(false);
    }

    public void toggleIA(){
        if (zombie == null || zombie.isDead()) return;
        zombie.setAI(!zombie.hasAI());
        zombie.setSilent(!zombie.isSilent());
    }

    public void remove(boolean force){
        if (zombie != null && !zombie.isDead()){
            zombie.remove();
        }

        if (armorStand != null && !armorStand.isDead()){
            armorStand.remove();
        }
        canSpawn = false;

        if (force) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                canSpawn = true;
            }
        }.runTaskLater(plugin, 20L*2);
    }

    public void updateAnimation(){
        if (!canUpdate) return;
        handler.updateAnimation(armorStand);
    }
}
