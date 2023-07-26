package me.j0keer.fhmap.listeners;

import com.cryptomorin.xseries.XSound;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Direction;
import me.j0keer.fhmap.enums.GameSound;
import me.j0keer.fhmap.enums.Regions;
import me.j0keer.fhmap.handler.AnimationHandler;
import me.j0keer.fhmap.handler.PlayerAnimationHandler;
import me.j0keer.fhmap.type.DataPlayer;
import me.j0keer.fhmap.type.ZombieObject;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameListeners implements Listener {
    private final Main plugin;

    public GameListeners(Main plugin){
        this.plugin = plugin;
    }

    private HashMap<UUID, Integer> villagers = new HashMap<>();
    private HashMap<UUID, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onInteractWithEntity(PlayerInteractEntityEvent event){
        Entity entity = event.getRightClicked();

        Player p = event.getPlayer();

        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;

        if (entity instanceof Villager){
            executeVillagers(p, entity, event);
        }
    }

    public void executeVillagers(Player p, Entity entity, Object event) {
        List<String> messages = new ArrayList<>();
        boolean isInteract = event instanceof PlayerInteractEntityEvent;
        boolean isDamage = event instanceof EntityDamageByEntityEvent;
        if (!isInteract && !isDamage) return;

        if (cooldown.containsKey(entity.getUniqueId())){
            if (cooldown.get(entity.getUniqueId()) > System.currentTimeMillis()){
                long timeLeft = (cooldown.get(entity.getUniqueId()) - System.currentTimeMillis()) / 1000;
                String formattedTime = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60);
                plugin.getUtils().sendMSG(p, "&cDebes esperar &e" + formattedTime + " &cpara volver a hablar con este aldeano.");
                if (isInteract) ((PlayerInteractEntityEvent) event).setCancelled(true);
                if (isDamage) ((EntityDamageByEntityEvent) event).setCancelled(true);
                Villager villager = (Villager) entity;
                villager.shakeHead();
                return;
            }
        }
        cooldown.put(entity.getUniqueId(), System.currentTimeMillis() + (1000 * 15));

        if (Regions.SPAWN.getCuboID() != null && Regions.SPAWN.getCuboID().isIn(p)) {
            messages.addAll(plugin.getConfig().getStringList("villagers.spawn"));
            if (plugin.getGame().isVillainSpawned()) {
                messages.clear();
            }
            if (isInteract) ((PlayerInteractEntityEvent) event).setCancelled(true);
            if (isDamage) ((EntityDamageByEntityEvent) event).setCancelled(true);
            XSound.ENTITY_VILLAGER_YES.play(p);
            int messageId = villagers.computeIfAbsent(entity.getUniqueId(), uuid -> (int) (Math.random() * messages.size()));
            plugin.getUtils().sendMSG(p, messages.get(messageId));
        }
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player p){
            if (event.getEntity() instanceof Villager entity){
                if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
                executeVillagers(p, entity, event);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        Entity entity = event.getEntity();

        if (entity instanceof Zombie zombie){
            List<ZombieObject> obj = new ArrayList<>(plugin.getGame().getSpawners().values().stream().filter(zombieObj -> zombieObj.isZombie(zombie)!=null).toList());
            obj.forEach(ZombieObject::remove);
            plugin.getUtils().debugToDev("Zombie dead: "+zombie.getUniqueId());
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player p = event.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);

        if (!dp.isInGame()) return;

        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            dp.getPlayerAnimationHandler().setPunching(true);
            plugin.getGame().attackEntities(dp);
        }
    }

    @EventHandler
    public void playerMoveInventory(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player p){
            DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
            if (!dp.isInGame()) return;
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) event.setCancelled(true);
        }
    }

    private List<String> jumped = new ArrayList<>();
    @EventHandler
    public void jumpHandler(PlayerMoveEvent event){
        Player p = event.getPlayer();
        if (jumped.contains(p.getName())){
            if (p.isOnGround()) {
                jumped.remove(p.getName());
            }
            return;
        }
        if (p.isOnGround()){
            return;
        }
        if((event.getFrom().getY() + 0.419) < event.getTo().getY()) {
            DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
            if (dp.isInGame()){
                GameSound.JUMP.play(p, 0.1f, 1);
                jumped.add(p.getName());
            }
        }
    }

    @EventHandler
    public void onInteractWithFrames(PlayerInteractAtEntityEvent event){
        Player p = event.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        Entity entity = event.getRightClicked();
        if (entity instanceof ItemFrame || entity instanceof Painting || entity instanceof GlowItemFrame){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageFrames(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        if (entity instanceof ItemFrame || entity instanceof Painting || entity instanceof GlowItemFrame){
            if (event.getDamager() instanceof Player p) {
                if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDamageListener(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player p) {
            DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
            if (!dp.isInGame()) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                p.setFireTicks(0);
                p.setVisualFire(false);
                event.setCancelled(true);
                return;
            }
            AnimationHandler.changeAnimationLeftRight(p, dp.getPlayerAnimationHandler().getDirection(), 24, 23);
        }

    }

    @EventHandler
    public void playerMoveListener(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(player);
        if (!dp.isInGame()) return;
        PlayerAnimationHandler handler = dp.getPlayerAnimationHandler();
        handler.setXSpeed(getXSpeed(event));
        handler.setYSpeed(getYSpeed(event));
        handler.setZSpeed(getZSpeed(event));
        handler.setDirection(getDirection(dp, handler.getXSpeed()));
        if (player.isOnGround()) {
            handler.setYSpeed(0.0);
        }

        if (playerRanIntoBlock(dp)) {
            handler.setXSpeed(0.0);
        }
    }

    private static boolean playerRanIntoBlock(DataPlayer dp) {
        Player player = dp.getPlayer();
        double distance = 0.6;
        double blockX = dp.getPlayerAnimationHandler().getDirection() == Direction.RIGHT ? distance : -distance;
        Block inTheWay = player.getLocation().add(blockX, 0.0, 0.0).getBlock();
        return !inTheWay.isPassable() || !inTheWay.getRelative(0, 1, 0).isPassable() && inTheWay.getType() != Material.OAK_DOOR;
    }

    private static Direction getDirection(DataPlayer dp, double speed) {
        if (speed > 0.0) {
            return Direction.RIGHT;
        } else {
            return speed < 0.0 ? Direction.LEFT : dp.getPlayerAnimationHandler().getDirection();
        }
    }

    private static double getXSpeed(@NotNull PlayerMoveEvent event) {
        return event.getTo().getX() - event.getFrom().getX();
    }

    private static double getYSpeed(@NotNull PlayerMoveEvent event) {
        return event.getTo().getY() - event.getFrom().getY();
    }

    private static double getZSpeed(@NotNull PlayerMoveEvent event) {
        return event.getTo().getZ() - event.getFrom().getZ();
    }
}
