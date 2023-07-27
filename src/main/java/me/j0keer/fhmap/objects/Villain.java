package me.j0keer.fhmap.objects;

import com.cryptomorin.xseries.XMaterial;
import me.j0keer.fhmap.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class Villain {
    private Main plugin;
    private ArmorStand stand;
    private State state = State.MOVE;
    private int health;
    private int dash_cooldown = 0;
    private int dash_cooldown_max;
    private double damage;
    private BukkitTask task;
    private boolean damaged = false;
    private int move_tick;
    private int dashing_tick = 0;
    private int  dashing_max_tick;
    private int dashing_back_tick = 0;
    private boolean in_left_face = false;
    private int dash_back_cooldown;
    private double dash_distance_movement;
    private boolean round_damaged = false;

    public Villain(Main plugin, int health, int dash_cooldown, double damage, int dash_max_movement, double dash_distance_movement){
        this.plugin = plugin;
        this.health = health;
        this.dash_cooldown_max = dash_cooldown;
        this.dash_back_cooldown = dash_cooldown / 2;
        this.damage = damage;
        this.dashing_max_tick = dash_max_movement;
        this.dash_distance_movement = dash_distance_movement;
    }

    public Villain(Main plugin){
        this(plugin, 5, 5 * 20, 1.2, 8, 1.5);
    }

    public void spawn(Location location){
        plugin.getUtils().debugToDev("Spawning villain...");
        stand = location.getWorld().spawn(location, ArmorStand.class, armor ->{
            armor.setGravity(false);
            armor.setInvulnerable(true);
            armor.setInvisible(true);
            armor.setSmall(true);
            ItemStack item = XMaterial.PHANTOM_MEMBRANE.parseItem();
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(112);
            item.setItemMeta(meta);
            armor.getEquipment().setHelmet(item);
        });

        plugin.getTasksManager().setDirection(location, stand);
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> second(), 0L, 1L);
    }

    public void remove(){
        task.cancel();
        stand.remove();
    }

    public void second(){
        if(!damaged){
            move();
            return;
        }

        switch (state){
            case MOVE -> state = State.MOVE_WAITING_DASH;
            case MOVE_WAITING_DASH -> {
                if(dash_cooldown >= dash_cooldown_max){
                    state = State.DASHING_LEFT;
                    dash_cooldown = 0;
                }else{
                    dash_cooldown++;
                    in_left_face = false;
                    move();
                }
            }
            case DASHING_LEFT -> {
                if(dashing_tick >= dashing_max_tick){
                    state = State.MOVE_WAITING_DASH_BACK;
                    dashing_tick = 0;
                }else{
                    dashing_tick++;
                    dashing(true);
                    checkDashDamage(true);
                }
            }
            case MOVE_WAITING_DASH_BACK -> {
                if(dashing_back_tick >= dash_back_cooldown){
                    state = State.DASHING_RIGHT;
                    dashing_back_tick = 0;
                }else{
                    dashing_back_tick++;
                    in_left_face = true;
                    move();
                }
            }
            case DASHING_RIGHT -> {
                if(dashing_tick >= dashing_max_tick){
                    state = State.MOVE;
                    dashing_tick = 0;
                    round_damaged = false;
                }else{
                    dashing_tick++;
                    dashing(false);
                    checkDashDamage(false);
                }
            }
        }
    }

    private void dashing(boolean left){
        stand.teleport(stand.getLocation().add(!left ? dash_distance_movement : -dash_distance_movement, 0, 0));
    }

    private int move_cooldown = 10;
    private void move(){
        if(move_cooldown <= 0){
            move_cooldown = 10;
        }else{
            move_cooldown--;
            return;
        }

        stand.teleport(stand.getLocation().add((move_tick % 2) != 0 ? dash_distance_movement : -dash_distance_movement, 0, 0));
        move_tick++;
        checkDamage();
    }

    public void damage(int damage){
        if(round_damaged){
            plugin.getUtils().debugToDev("Ya has hecho el daño de esta ronda");
            return;
        }

        stand.getWorld().playSound(stand, Sound.BLOCK_MUD_BRICKS_HIT, 1, -2);

        round_damaged = true;
        health-=damage;

        if(health <= 0)
            death();

        damaged = true;
    }

    private void death(){
        remove();
        plugin.getUtils().debugToDev("enemy death");
    }

    public void checkDamage(){
        int pos = move_tick % 2;
        boolean can_left = (in_left_face && pos == 0);
        boolean can_right = (!in_left_face && pos == 1);

        if(can_left || can_right){
            List<Player> players = Bukkit.getOnlinePlayers().stream().filter(target -> target.getGameMode().equals(GameMode.ADVENTURE) && target.getLocation().distance(stand.getLocation()) <= 2).collect(Collectors.toList());Collectors.toList();
            players.forEach(target -> {
                target.damage(damage);
                boolean left = target.getLocation().getX() <= stand.getLocation().getX();
                double x = left ? -0.7 : 0.7;
                Vector vector = new Vector(x, 0.2, 0);
                target.setVelocity(vector);
            });
        }
    }

    private void checkDashDamage(boolean b) {
        List<Player> players = Bukkit.getOnlinePlayers().stream().filter(target -> target.getGameMode().equals(GameMode.ADVENTURE) && target.getLocation().distance(stand.getLocation()) <= 2).collect(Collectors.toList());Collectors.toList();
        players.forEach(target -> {
        double x = b ? -0.5 : 0.5;
        Vector vector = new Vector(x, 0.2, 0);
        target.setVelocity(vector);
        target.damage(0.1);
        });
    }

    public ArmorStand getStand(){
        return stand;
    }

    private enum State{
        MOVE, MOVE_WAITING_DASH, DASHING_LEFT, MOVE_WAITING_DASH_BACK, DASHING_RIGHT
    }
}
