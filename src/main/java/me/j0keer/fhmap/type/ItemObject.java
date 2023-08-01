package me.j0keer.fhmap.type;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ItemObject {
    private Main plugin;
    private ItemObjectType type;
    private double up_distance = 0.3;
    private int up_points = 2;
    private double fireball_height = 0;
    private double fireball_max_height = 1.3;
    private boolean bouncing = false;
    private Entity entity;
    private Direction direction;
    private BukkitTask task;

    private int live_ticks = 300;

    public ItemObject(ItemObjectType type, Main plugin){
        this.type = type;
        this.plugin = plugin;
    }

    public void spawn(Location location){
        spawn(location, Direction.values()[ThreadLocalRandom.current().nextInt(Direction.values().length)]);
    }

    public void spawn(Block block){
        Location location = block.getLocation();
        location.add(.5, .5, 0.5);
        spawn(location);
    }

    public void spawn(Block block, Direction direction){
        Location location = block.getLocation();
        location.add(.5, .5, 0.5);
        spawn(location, direction);
    }

    public void spawn(Location location, Direction direction){
        this.direction = direction;
        Location spawnLocation = location.clone().add(0, 0, 1);
        switch(type){
            case COIN -> {
                entity = location.getWorld().dropItem(spawnLocation, new ItemStack(Material.EMERALD), item ->{
                    item.getItemStack().getItemMeta().setCustomModelData(2);
                    item.setOwner(UUID.randomUUID());
                });
            }
            case FLINT -> {
                entity = location.getWorld().dropItem(spawnLocation, new ItemStack(Material.FLINT_AND_STEEL), item ->{
                    item.getItemStack().getItemMeta().setCustomModelData(3);
                    item.setOwner(UUID.randomUUID());
                });
            }
            case FOOD -> {
                entity = location.getWorld().dropItem(spawnLocation, new ItemStack(plugin.getGame().drops.get(new Random().nextInt(plugin.getGame().drops.size()-1))), item ->{
                    item.setOwner(UUID.randomUUID());
                });
            }
            case FIREBALL -> {
                entity = location.getWorld().spawn(spawnLocation, ArmorStand.class, armor -> {
                    armor.setSmall(true);
                    armor.setInvisible(true);
                    armor.setGravity(false);
                });
            }
        }
        entity.setGravity(false);
        entity.setVelocity(new Vector(0, 0, 0));
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> tick(), 0L, 1L);
    }

    public void tick(){
        live_ticks--;
        double speed = 0.15;
        double gravity = 0.2;

        if(entity == null){
            remove();
            return;
        }

        if(!type.equals(ItemObjectType.FIREBALL)){
            if(up_points > 0){
                up_points--;
                entity.teleport(entity.getLocation().clone().add(0, up_distance, 0));
                return;
            }

            double x = direction.equals(Direction.RIGHT) ? speed : -speed;
            double old_y = entity.getLocation().clone().getY();
            double y = 0;

            Location location = entity.getLocation().clone().add(x, 0, -1);

            boolean inFloor = location.clone().add(0, -gravity, 0).getBlock().getType().isSolid();
            boolean isSolid = location.getBlock().getType().isSolid();

            if(!inFloor){
                location.setY(old_y-gravity);
                y = -gravity;
            }


            direction = isSolid ? (direction.equals(Direction.LEFT) ? Direction.RIGHT : Direction.LEFT) : direction;
            plugin.getLogger().info(entity.getLocation().toVector().toString()+" -> "+location.toVector().toString());
            Vector vector = new Vector(x, y, 0);
            entity.setVelocity(vector);
            //entity.teleport(location);

            if(live_ticks <= 0){
                remove();
            }
            return;
        }

        double x = direction.equals(Direction.RIGHT) ? speed : -speed;
        Location location = entity.getLocation().clone().add(x, 0, 0);
        boolean inFloor = location.clone().add(0, -gravity, 0).getBlock().getType().isSolid();
        boolean isSolid = location.getBlock().getType().isSolid();

        if(isSolid){
            remove();
            return;
        }

        if(inFloor && !bouncing)
            bouncing = true;

        if(bouncing){

        }

    }

    public void remove(){
        if(task != null)
            task.cancel();

        if(entity != null)
            entity.remove();
    }

    public enum ItemObjectType{
        COIN, FLINT, FOOD, FIREBALL, NORMAL
    }
}
