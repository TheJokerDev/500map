package me.j0keer.fhmap.game;

import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import lombok.Setter;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Direction;
import me.j0keer.fhmap.enums.GameSound;
import me.j0keer.fhmap.listeners.EntityListener;
import me.j0keer.fhmap.listeners.GameListeners;
import me.j0keer.fhmap.managers.CameraManager;
import me.j0keer.fhmap.type.*;
import me.j0keer.fhmap.utils.LocationUtil;
import me.j0keer.fhmap.utils.SPBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter @Setter
public class Game implements Listener {
    private final Main plugin;

    /* Util lists */
    private List<SPBlock> savedBlocks = new ArrayList<>();
    private List<Material> allowedBlocks = new ArrayList<>();
    private HashMap<Location, ZombieObject> spawners = new HashMap<>();
    private HashMap<String, Pipe> pipeHashMap = new HashMap<>();
    private HashMap<String, DeathRegion> deathRegionHashMap = new HashMap<>();
    private Map<ArmorStand, Villain> villains = new HashMap<>();

    public List<Material> drops = new ArrayList<>();
    public List<DataPlayer> playings = new ArrayList<>();

    private GameMusic overworld_music;
    private GameMusic nether_music;
    private GameMusic boss_music;

    private boolean end = false;

    public void setEnd(boolean end) {
        this.end = end;

        List<String> locs = new ArrayList<>();
        locs.add("world,685.0,121.0,1.0");
        locs.add("world,685.0,120.0,1.0");
        locs.add("world,685.0,119.0,1.0");

        locs.add("world,664.0,121.0,1.0");
        locs.add("world,664.0,120.0,1.0");
        locs.add("world,664.0,119.0,1.0");
        locs.add("world,664.0,118.0,1.0");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (locs.size() == 0){
                    cancel();
                    spawnVillain();
                    return;
                }
                String loc = locs.get(locs.size()-1);
                locs.remove(loc);

                Location location = LocationUtil.getLocation(loc);
                if (location != null){
                    Block b = location.getBlock();
                    SPBlock spBlock = new SPBlock(b);
                    savedBlocks.add(spBlock);
                    b.setType(Material.MAGENTA_GLAZED_TERRACOTTA);
                    //set block facing
                    Directional directional = (Directional) b.getBlockData();
                    directional.setFacing(BlockFace.WEST);
                    b.setBlockData(directional);
                    XSound.BLOCK_STONE_PLACE.play(location);
                }
            }
        }.runTaskTimer(plugin, 10L, 0L);
    }

    List<Player> villainsPlayer = new ArrayList<>();
    public void joinVillain(Player player){
        DataPlayer dataPlayer = plugin.getDataManager().getDataPlayer(player);
        villainsPlayer.add(player);
        dataPlayer.setVillain(true);
        Location location = getVillainGameSpawnLocation().clone();
        location.setPitch(getGameLocation().getPitch());
        location.setYaw(getGameLocation().getYaw());
        dataPlayer.setVanished(false);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(getVillainGameSpawnLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getCameraManager().changeCamera(player, CameraManager.Perspective.THIRD_PERSON_BACK);
                plugin.getCameraManager().lockMovementAxis(player, 'z');
                plugin.getCameraManager().lockCamera(player);
            }
        }.runTaskLater(plugin, 2);
    }

    public void leaveVillain(Player player){
        DataPlayer dataPlayer = plugin.getDataManager().getDataPlayer(player);
        villainsPlayer.remove(player);
        dataPlayer.setVillain(true);
        dataPlayer.setVanished(true);
        player.teleport(plugin.getGame().getSpawn());

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getCameraManager().unlockMovementAxis(player, 'z');
                plugin.getCameraManager().unlockCamera(player);
                plugin.getCameraManager().changeCamera(player, CameraManager.Perspective.FIRST_PERSON);
            }
        }.runTaskLater(plugin, 2);
    }

    public void spawnVillain(){
        Villain villain = new Villain(plugin);
        villain.spawn(getVillainGameSpawnLocation());
        villains.put(villain.getStand(), villain);

        /*new BukkitRunnable() {
            private boolean check = false;
            int i = 0;
            @Override
            public void run() {
                if (i==30) {
                    cancel();
                    stand.remove();
                    return;
                }
                check = !check;
                i++;
                stand.teleport(stand.getLocation().add(check ? 1.5 : -1.5, 0, 0));
            }
        }.runTaskTimer(getPlugin(), 0L, 10L);*/
    }

    public Game(Main plugin){
        this.plugin = plugin;
        plugin.listener(new GameListeners(plugin));
        plugin.listener(new EntityListener(plugin));
        plugin.listener(this);

        allowedBlocks.add(Material.BRICKS);
        allowedBlocks.add(Material.CRAFTING_TABLE);
        allowedBlocks.add(Material.EMERALD_BLOCK);
        allowedBlocks.add(Material.COPPER_BLOCK);
        allowedBlocks.add(Material.DARK_PRISMARINE);
        allowedBlocks.add(Material.PRISMARINE);

        drops.addAll(Arrays.asList(Material.CARROT, Material.POTATO, Material.BREAD, Material.GOLDEN_CARROT, Material.APPLE));

        loadSpawners();
        loadPipes();
        loadDeathRegions();
        overworld_music = new GameMusic(plugin, GameMusic.Music.OVERWORLD, 173);
        nether_music = new GameMusic(plugin, GameMusic.Music.NETHER, 100);
        boss_music = new GameMusic(plugin, GameMusic.Music.BOSS, 160);
    }

    private boolean villainSpawned = false;

    public Location getSpawn(){
        return LocationUtil.getLocation(plugin.getConfig().getString("game.spawn", "world,-499.5,0,500.5,0,0"));
    }

    public void setSpawn(Location location){
        plugin.getConfig().set("game.spawn", LocationUtil.getString(location, true));
        plugin.saveConfig();
    }

    public void setVillainSpawn(Location location){
        plugin.getConfig().set("game.villain.spawn", LocationUtil.getString(location, true));
        plugin.saveConfig();
    }

    public void setVillainGameSpawn(Location location){
        plugin.getConfig().set("game.villain.game-spawn", LocationUtil.getString(location, true));
        plugin.saveConfig();
    }

    public void setGameSpawn(Location location){
        plugin.getConfig().set("game.game-spawn", LocationUtil.getString(location, true));
        plugin.saveConfig();
        plugin.getTasksManager().reloadLocation();
    }

    private List<String> stressEffects = new ArrayList<>();
    int i = 0;
    public void playStressEffect(Player p, int time){
        if (stressEffects.contains(p.getName())) return;
        stressEffects.add(p.getName());

        World w = p.getWorld();
        //set night
        w.setTime(18000);
        w.setStorm(true);

        int amount = time;
        if (amount == 0){
            amount = new Random().nextInt(5, 10);
        }
        int radius = 75;
        XSound.ENTITY_ENDER_DRAGON_GROWL.play(p.getLocation());
        int finalAmount = amount;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (i >= finalAmount *2){
                    cancel();
                    i = 0;
                    return;
                }
                Location loc = getVillainSpawnLocation().add(Math.random() * radius, 1, Math.random() * radius);
                //get top block
                loc.setY(p.getWorld().getHighestBlockYAt(loc));
                loc.add(0, 1, 0);
                w.strikeLightningEffect(loc);
                i++;
            }
        }.runTaskTimer(plugin, 0L, 10L);

        new BukkitRunnable() {
            @Override
            public void run() {
                stressEffects.remove(p.getName());
                w.setTime(6000);
                w.setStorm(false);
            }
        }.runTaskLater(plugin, 20L*amount);
    }

    public void reset(){
        villainSpawned = false;
        cooldownLocs.clear();
        getSavedBlocks().forEach(spBlock -> {
            spBlock.getBlock().setType(spBlock.getType());
            spBlock.getBlock().setBlockData(spBlock.getData());
        });
        savedBlocks.clear();
        repeatableBlocks.clear();
        spawners.values().forEach(obj -> obj.remove(true));
        spawners.clear();
        end = false;
        loadSpawners();
    }

    public void addSpawner(Location loc){
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("game.spawners"));
        list.add(LocationUtil.getString(loc, false));
        plugin.getConfig().set("game.spawners", list);
        plugin.saveConfig();

        loadSpawners();
    }

    public boolean removeSpawner(){
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("game.spawners"));
        if (list.size() == 0) return false;
        list.remove(list.get(list.size()-1));
        plugin.getConfig().set("game.spawners", list);
        plugin.saveConfig();

        loadSpawners();
        return true;
    }

    public boolean addPipe(String id){
        List<String> list = new ArrayList<>(plugin.getConfig().get("game.pipe") == null ? new ArrayList<>() : plugin.getConfig().getStringList("game.pipe"));
        if (list.contains(id)) return false;
        list.add(id);
        plugin.getConfig().set("game.pipe."+id+".created", true);
        plugin.saveConfig();

        loadPipes();
        return true;
    }

    public boolean removePipe(String id){
        List<String> list = new ArrayList<>(plugin.getConfig().get("game.pipe") == null ? new ArrayList<>() : plugin.getConfig().getStringList("game.pipe"));
        if (!list.contains(id)) return false;
        list.remove(id);
        plugin.getConfig().set("game.pipe."+id, null);
        plugin.saveConfig();

        loadPipes();
        return true;
    }

    public Pipe getPipe(String id){
        return pipeHashMap.getOrDefault(id, null);
    }

    public void loadSpawners(){
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("game.spawners"));
        List<Location> locations = new ArrayList<>();
        if (!list.isEmpty()){
            for (String s : list) {
                Location loc = LocationUtil.getLocation(s);
                if (loc != null){
                    locations.add(loc);
                }
            }
        }

        List<Location> toRemove = new ArrayList<>(spawners.keySet().stream().filter(loc -> !locations.contains(loc)).toList());
        if (locations.isEmpty() && !spawners.isEmpty()){
            toRemove.addAll(spawners.keySet());
        }
        toRemove.forEach(loc -> {
            spawners.get(loc).remove();
            spawners.remove(loc);
        });

        locations.stream().filter(loc -> !spawners.containsKey(loc)).forEach(loc -> spawners.put(loc, new ZombieObject(plugin, loc)));
    }

    public void loadDeathRegions(){
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("game.death-regions"));
        List<String> locations = new ArrayList<>(list);

        List<String> toRemove = new ArrayList<>(deathRegionHashMap.keySet().stream().filter(id -> !locations.contains(id)).toList());
        if (locations.isEmpty()){
            toRemove.addAll(deathRegionHashMap.keySet());
        }
        toRemove.forEach(id -> {
            deathRegionHashMap.get(id).unregister();
            deathRegionHashMap.remove(id);
        });

        locations.stream().filter(id -> !deathRegionHashMap.containsKey(id)).forEach(id -> deathRegionHashMap.put(id, new DeathRegion(plugin, id)));
    }

    public void addDeathRegion(Location loc1, Location loc2){
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("game.death-regions"));
        String string = LocationUtil.getString(loc1, true) + ";" + LocationUtil.getString(loc2, true);
        list.add(string);
        plugin.getConfig().set("game.death-regions", list);
        plugin.saveConfig();

        loadDeathRegions();
    }

    public boolean removeDeathRegion(){
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("game.death-regions"));
        if (list.size() == 0) return false;
        String str = list.get(list.size()-1);
        list.remove(str);
        DeathRegion deathRegion = deathRegionHashMap.get(str);
        if (deathRegion != null){
            deathRegion.unregister();
            deathRegionHashMap.remove(str);
        }
        plugin.getConfig().set("game.death-regions", list);
        plugin.saveConfig();

        loadDeathRegions();
        return true;
    }

    public void loadPipes(){
        if (plugin.getConfig().getConfigurationSection("game.pipe") == null) return;
        List<String> list = new ArrayList<>(plugin.getConfig().getConfigurationSection("game.pipe").getKeys(false));
        List<String> toRemove = new ArrayList<>(pipeHashMap.keySet().stream().filter(id -> !list.contains(id)).toList());
        if (!pipeHashMap.isEmpty()){
            toRemove.addAll(pipeHashMap.keySet());
        }
        toRemove.forEach(id -> {
            pipeHashMap.get(id).unregister();
            pipeHashMap.remove(id);
        });

        list.stream().filter(id -> !pipeHashMap.containsKey(id)).forEach(id -> pipeHashMap.put(id, new Pipe(plugin, id)));
    }

    public Location getGameLocation() {
        return LocationUtil.getLocation(plugin.getConfig().getString("game.game-spawn"));
    }

    public Location getVillainSpawnLocation() {
        return LocationUtil.getLocation(plugin.getConfig().getString("game.villain.spawn"));
    }
    public Location getVillainGameSpawnLocation() {
        return LocationUtil.getLocation(plugin.getConfig().getString("game.villain.game-spawn"));
    }

    private List<Location> cooldownLocs = new ArrayList<>();

    @EventHandler
    public void onGameMove(PlayerMoveEvent event){
        Player p = event.getPlayer();
        DataPlayer dataPlayer = plugin.getDataManager().getDataPlayer(p);

        if (!dataPlayer.isInGame()) return;

        spawners.forEach((spawner, obj) -> {
            //get the distance in blocks between player and spawner location
            double distance = p.getLocation().distance(spawner);
            if (distance <= 10 && obj.canSpawn()){
                obj.spawn(p);
            } else if (distance > 20 && !obj.canSpawn()){
                obj.remove();
            }
        });

        Location loc = p.getLocation();
        Block getBlockOnTop = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 2, loc.getBlockZ());
        Material type = getBlockOnTop.getType();
        if (type == Material.AIR) return;

        if (allowedBlocks.contains(type)){
            if (cooldownLocs.contains(getBlockOnTop.getLocation())) return;

            if (type == Material.EMERALD_BLOCK || type == Material.DARK_PRISMARINE) {
                if (dataPlayer.isSmall() || p.isOnGround()) return;
                cooldownLocs.add(getBlockOnTop.getLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cooldownLocs.remove(getBlockOnTop.getLocation());
                    }
                }.runTaskLater(plugin, 20L*2);

                SPBlock spBlock = new SPBlock(getBlockOnTop);
                savedBlocks.add(spBlock);

                //break block and spawn particles of block
                Location particleLoc = getBlockOnTop.getLocation().add(0,0,1);
                //set particleLoc in center of block
                particleLoc.add(0.5, 0.5, 0.5);
                loc.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_CRACK, particleLoc, 25, 0.5, 0.5, 0.5, 0.1, getBlockOnTop.getBlockData());
                getBlockOnTop.setType(Material.AIR);

                GameSound.BRICKS_BREAK.play(p);
            } else if (type == Material.CRAFTING_TABLE || type == Material.BRICKS || type == Material.COPPER_BLOCK || type == Material.PRISMARINE) {
                if (p.isOnGround()) return;
                cooldownLocs.add(getBlockOnTop.getLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cooldownLocs.remove(getBlockOnTop.getLocation());
                    }
                }.runTaskLater(plugin, 10L);

                if (type == Material.COPPER_BLOCK || type == Material.PRISMARINE) {
                    int tries = repeatableBlocks.get(getBlockOnTop.getLocation()) == null ? 0 : repeatableBlocks.get(getBlockOnTop.getLocation());
                    tries++;
                    repeatableBlocks.put(getBlockOnTop.getLocation(), tries);
                    if (tries < 5) {
                        GameSound.BLOCK_COIN.play(p, 0.3f, 1);
                        //dataPlayer.addCoin();
                        ItemObject object = new ItemObject(ItemObject.ItemObjectType.COIN, getPlugin());
                        object.spawn(getBlockOnTop);
                        return;
                    }
                }

                SPBlock spBlock = new SPBlock(getBlockOnTop);
                savedBlocks.add(spBlock);

                getBlockOnTop.setType(Material.PURPUR_BLOCK);
                if (type == Material.COPPER_BLOCK || type == Material.PRISMARINE) {
                    GameSound.BLOCK_COIN.play(p, 0.3f, 1);
                    dataPlayer.addCoin();
                } else {
                    boolean ran = new Random().nextBoolean();
                    if(ran){
                        ItemObject.ItemObjectType[] drop = new ItemObject.ItemObjectType[]{ItemObject.ItemObjectType.FLINT, ItemObject.ItemObjectType.FOOD, ItemObject.ItemObjectType.NORMAL};
                        ItemObject object = new ItemObject(drop[new Random().nextInt(drop.length-1)], getPlugin());
                        object.spawn(getBlockOnTop);
                    }else{
                        ItemObject object = new ItemObject(ItemObject.ItemObjectType.NORMAL, plugin);
                        object.spawn(getBlockOnTop);
                    }

                    p.getInventory().addItem(new ItemStack(drops.get(new Random().nextInt(drops.size()-1))));
                    GameSound.BLOCK_BONUS.play(p,0.5f, 1);
                }
            }
        }
    }

    private HashMap<Location, Integer> repeatableBlocks = new HashMap<>();
    private List<String> cooldownAttack = new ArrayList<>();

    public void attackEntities(@NotNull DataPlayer dp) {
        Player player = dp.getPlayer();
        int xDir = dp.getPlayerAnimationHandler().getDirection() == Direction.RIGHT ? 1 : -1;
        int crouching = player.isSneaking() ? 0 : 1;
        int damage = player.getInventory().getItemInMainHand().getType().name().contains("SWORD") ? 8 : 3;
        double velocityDampener = 0.25;
        Location location = player.getLocation().add(xDir, crouching, 0.0);
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, xDir, 1.0, 1.0);
        nearbyEntities.forEach((entity) -> {
            if (entity instanceof Zombie) {
                ((Zombie)entity).damage(damage);
                entity.setVelocity(new Vector((double)xDir * velocityDampener, velocityDampener, 0.0));
                if (damage == 3) {
                    XSound.ENTITY_PLAYER_ATTACK_WEAK.play(location);
                } else {
                    XSound.ENTITY_PLAYER_ATTACK_STRONG.play(location);
                }
            }
            if (end){
                if (entity instanceof ArmorStand stand){
                    if (cooldownAttack.contains(player.getName())) return;
                    Villain villain = villains.get(stand);

                    //check if is villain
                    if(villain == null)
                        return;

                    villain.damage(1);
                    //entity.setVelocity(new Vector((double)xDir * velocityDampener, velocityDampener, 0.0));
                    //punch player to left
                    //player.setVelocity(new Vector((double)-xDir * velocityDampener, velocityDampener, 0.0));

                    //player.damage(3, stand);
                    //cooldownAttack.add(player.getName());

                    plugin.getUtils().debugToDev("Villain attacked");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            cooldownAttack.remove(player.getName());
                        }
                    }.runTaskLater(plugin, 20L*3);
                }
            }
        });
    }

    public void attact2(Location location){
        location.getWorld().playSound(location, "500map:boss_meteor", 0.6f, -1f);
        double amount = 6;
        for(int i = 0 ; i < 4; i++){
            Location loc = location.clone().add((i * 2.1) - (amount / 2), new Random().nextDouble(6, 8), 1);
            ArmorStand armorStand = location.getWorld().spawn(loc, ArmorStand.class, stand ->{
                ItemStack item = new ItemStack(Material.ENDER_PEARL);
                stand.getEquipment().setItem(EquipmentSlot.HEAD, item, true);
                stand.setGravity(false);
                stand.setInvisible(true);
            });

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(armorStand.getLocation().add(0, 1.6, -1).getBlock().getType().isSolid()){

                        cancel();
                        armorStand.remove();
                        armorStand.getLocation().getWorld().playSound(armorStand.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.5f, 2f);
                        for(DataPlayer player : playings){
                            double distance = player.getPlayer().getLocation().distance(armorStand.getLocation().add(0, 0, -1));
                            if(distance <= 1.9)
                                player.getPlayer().damage(7);
                        }
                        return;
                    }
                    armorStand.getLocation().getWorld().spawnParticle(Particle.PORTAL,armorStand.getLocation().clone().add(0, 1.8, 0), 1);
                    armorStand.teleport(armorStand.getLocation().add(0, -0.25, 0));
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }

    public void attact1(BlockFace face, Location location){
        int rotation = face.equals(BlockFace.EAST) ? 90 : -90;
        double direction_amount = face.equals(BlockFace.EAST) ? -0.3 : 0.3;
        location.setYaw(rotation);
        location.setPitch(90);
        location.add(0, -1.8, 1);
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class, stand ->{
           ItemStack item = new ItemStack(Material.STICK);
           ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setCustomModelData(1);
           item.setItemMeta(meta);
           stand.getEquipment().setItem(EquipmentSlot.HEAD, item, true);
           stand.setHeadPose(stand.getHeadPose().setX(1.6));
           stand.setGravity(false);
           stand.setInvisible(true);
        });
        armorStand.getWorld().playSound(armorStand.getLocation(), "500map:boss_sword_up", 0.4f, 1f);
        new BukkitRunnable() {
            int max_ticks = 16;
            int max_up_ticks = 10;
            int counts = 0;
            int up_counts = 0;
            boolean played = false;
            private float volume = 0.6f;
            @Override
            public void run() {
                if(counts >= max_ticks){
                    if(counts <= max_ticks * 3){
                        if(armorStand != null){
                            int a = counts % 5;
                            if(a == 4){
                                armorStand.getWorld().playSound(armorStand.getLocation(), "500map:boss_sword_rotate", volume, 0.4f);
                                volume -= 0.1;
                            }
                            armorStand.teleport(armorStand.getLocation().add(direction_amount / 1.5, 0.4, 0));
                        }

                    }else{
                        cancel();
                        armorStand.remove();
                        return;
                    }

                }
                if(up_counts < max_up_ticks){
                    armorStand.teleport(armorStand.getLocation().add(0, 0.05, 0));
                    up_counts++;
                    return;
                }else{
                    if(!played){
                        armorStand.getWorld().playSound(armorStand.getLocation(), "500map:boss_sword_attack", 1f, 1f);
                        played = true;
                        for(DataPlayer player : playings){
                            double distance = player.getPlayer().getLocation().distance(armorStand.getLocation().add(0, 0, -1));
                            if(distance <= 2.5)
                                player.getPlayer().damage(15);
                        }
                    }

                }
                    if(direction_amount > 0)
                        armorStand.setHeadPose(armorStand.getHeadPose().add(direction_amount, 0, 0));

                if(direction_amount < 0)
                    armorStand.setHeadPose(armorStand.getHeadPose().subtract(direction_amount, 0, 0));
                counts++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void addVillainItems(Player player){
    }
}
