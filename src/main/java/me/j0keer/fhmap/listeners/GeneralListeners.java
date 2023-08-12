package me.j0keer.fhmap.listeners;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Modules;
import me.j0keer.fhmap.type.DataPlayer;
import me.j0keer.fhmap.type.GameMusic;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneralListeners implements Listener {
    private final Main plugin;

    public GeneralListeners(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event){
        if (Modules.ENTITY_SPAWN.isEnabled()){
            return;
        }
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL){
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Villager villager){
            List<Villager.Profession> professionList = new ArrayList<>(Arrays.stream(Villager.Profession.values()).toList());
            professionList.remove(Villager.Profession.NITWIT);
            Villager.Profession profession = professionList.get((int) (Math.random() * professionList.size()));
            villager.setProfession(profession);
            villager.setVillagerType(Villager.Type.values()[(int) (Math.random() * Villager.Type.values().length)]);
            villager.setVillagerLevel((int) (Math.random() * 5));
            plugin.console("Villager spawned with profession: " + profession.name() + " and type: " + villager.getVillagerType().name() + " and level: " + villager.getVillagerLevel());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.getAction() == Action.PHYSICAL){
            if (Modules.PHYSICAL_INTERACTIONS.isEnabled()) return;
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if (Modules.BLOCK_PLACE.isEnabled()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (Modules.FALL_DAMAGE.isEnabled()) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) event.setCancelled(true);
    }

    //PvP cancel event
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (Modules.PVP.isEnabled()) return;
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event){
        if (Modules.FOOD_LEVEL.isEnabled()) return;
        if (event.getEntity() instanceof Player player){
            DataPlayer dp = plugin.getDataManager().getDataPlayer(player);
            if (dp.isInGame()) return;
            //Set food level to 20
            player.setFoodLevel(20);
            event.setCancelled(true);

            plugin.getUtils().debugToDev("Food level change event cancelled" + player.getName());
        }
    }

    @EventHandler
    public void onDeathListener(PlayerDeathEvent event){
        Player p = event.getEntity();
        event.setDeathMessage(null);
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (dp.isInGame()) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            p.setBedSpawnLocation(p.getLocation(), true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.spigot().respawn();
                    p.teleport(p.getLocation());
                    dp.death();
                }
            }.runTaskLater(plugin, 1L);
            return;
        }

        p.setBedSpawnLocation(plugin.getGame().getSpawn(), true);
        p.spigot().respawn();
        p.teleport(plugin.getGame().getSpawn());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageDeath(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if(player.getHealth() <= event.getFinalDamage()){
            DataPlayer dp = plugin.getDataManager().getDataPlayer(player);
            if(!dp.isInGame())
                return;

            event.setDamage(0.0d);
            if(!dp.isSmall()){
                dp.playSound(DataPlayer.sound.LEVEL_DOWN);
                dp.setSmall(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setHealth(20);
                    }
                }.runTaskLater(plugin, 2);
                return;
            }

            dp.death();
        }
    }

    //Block break cancel event
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (Modules.BLOCK_BREAK.isEnabled()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }
}
