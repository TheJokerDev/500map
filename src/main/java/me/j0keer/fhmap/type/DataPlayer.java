package me.j0keer.fhmap.type;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import lombok.Getter;
import lombok.Setter;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Direction;
import me.j0keer.fhmap.enums.GameSound;
import me.j0keer.fhmap.handler.AnimationHandler;
import me.j0keer.fhmap.handler.PlayerAnimationHandler;
import me.j0keer.fhmap.managers.CameraManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@Getter @Setter
public class DataPlayer {
    private final Main plugin = Main.getPlugin();
    private Player player;
    private String name;
    private UUID uuid;
    @Setter private HashMap<String, Button> items = new HashMap<>();
    private PlayerAnimationHandler playerAnimationHandler;

    private boolean vanished = false;
    private boolean inGame = false;

    private boolean isVillain = false;

    private BossBar gameUpBar;
    private BossBar gameBar;

    public boolean ignoreCancelled = false;

    //Game utils
    boolean small = false;

    public DataPlayer(Player player){
        this.player = player;
        loadItems();
        playerAnimationHandler = new PlayerAnimationHandler();
    }

    private boolean sizeCooldown = false;
    public void setSmall(boolean small) {
        if (sizeCooldown) return;

        if(small){
            getPlayer().setSwimming(true);
            ignoreCancelled = true;
        }else{
            ignoreCancelled = false;
            getPlayer().setSwimming(false);
        }
        sizeAnimation(small);
        plugin.getCameraManager().changeSize(getPlayer(), small);
    }

    public void sizeAnimation(boolean bool){
        sizeCooldown = true;
        new BukkitRunnable() {
            private int i = 0;
            @Override
            public void run() {
                if (i >= 5) {
                    small = bool;
                    plugin.getCameraManager().changeSize(getPlayer(), small);
                    sizeCooldown = false;
                    cancel();
                    return;
                }
                small = !small;
                i++;
            }
        }.runTaskTimer(plugin, 7L, 0L);
    }

    public void playSound(sound s){
        getPlayer().playSound(getPlayer().getLocation(), "500map:"+s.name().toLowerCase(), 1f, 1f);
    }
    public enum sound{
        LEVEL_UP, LEVEL_DOWN
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
        getPlayer().setMetadata("spreen:vanished", new FixedMetadataValue(plugin, vanished));
        if (vanished){
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (player.hasPermission("map.staff.see")){
                    player.showPlayer(plugin, getPlayer());
                } else {
                    player.hidePlayer(plugin, getPlayer());
                }
            });
        } else {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (!player.canSee(getPlayer())){
                    player.showPlayer(plugin, getPlayer());
                }
            });
        }
    }

    public DataPlayer(String name){
        this.name = name;
        player = plugin.getServer().getPlayer(name);
        if (player != null){
            uuid = player.getUniqueId();
        }
        loadItems();
        playerAnimationHandler = new PlayerAnimationHandler();
    }

    public DataPlayer(UUID uuid){
        this.uuid = uuid;
        player = plugin.getServer().getPlayer(uuid);
        if (player != null){
            name = player.getName();
        }
        loadItems();
        playerAnimationHandler = new PlayerAnimationHandler();
    }

    public void loadItems(){
        boolean replace = false;
        List<String> replacing = new ArrayList<>();
        if (items.size() > 0){
            for (ItemStack item : getPlayer().getInventory().getContents()){
                if (item == null) continue;
                for (Map.Entry<String, Button> b : items.entrySet()){
                    if (item.isSimilar(b.getValue().getItem().build(getPlayer()))){
                        replace = true;
                        replacing.add(b.getKey());
                    }
                }
            }
        }
        items.clear();
        if (plugin.getConfigUtil().getItems().getKeys(false).size() > 0){
            for (String key : plugin.getConfigUtil().getItems().getKeys(false)){
                plugin.console("Item "+key+" loading for "+getPlayer().getName()+".");
                Button b = new Button(getPlayer(), plugin.getConfigUtil().getItems(), key);
                items.put(key, b);
                if (replace && replacing.contains(key)){
                    b.getSlot().forEach(integer -> getPlayer().getInventory().setItem(integer, b.getItem().build(getPlayer())));
                }
            }
        }

    }

    private ItemStack[] contents;
    private Location location;
    private GameMode gameMode;
    private double health;
    private int foodLevel;

    private BukkitTask gameTask;
    private int time = 0;
    private int coins = 0;

    public void task(){
        String bar = "";
        if (gameBar == null && gameUpBar == null) {
            gameUpBar = plugin.getServer().createBossBar(bar, BarColor.PURPLE, BarStyle.SOLID);
            gameBar = plugin.getServer().createBossBar(bar, BarColor.PURPLE, BarStyle.SOLID);
            gameUpBar.addPlayer(getPlayer());
            gameBar.addPlayer(getPlayer());
        }
        time++;

        int timeNew = time/20;

        String time = String.format("%02d:%02d", (timeNew / 60), (timeNew % 60));
        //Coins format 001
        String coins = String.format("%03d", this.coins);

        String space = ChatColor.RESET+"                                                                          "+ChatColor.RESET;
        String space2 = ChatColor.RESET+"                                                                     "+ChatColor.RESET;

        bar = time+space+space+coins+ "  ";
        gameBar.setTitle(bar);

        bar = "§lTIEMPO"+space+space2+"§lMONEDAS";
        gameUpBar.setTitle(bar);
    }

    public void addCoin(){
        coins++;
        if (coins%20==0) {
            XSound.ENTITY_PLAYER_LEVELUP.play(getPlayer());
            getPlayer().getInventory().addItem(XMaterial.GOLDEN_SWORD.parseItem());
            new BukkitRunnable() {
                @Override
                public void run() {
                    getPlayer().getInventory().remove(Material.GOLDEN_SWORD);
                }
            }.runTaskLater(plugin, 20L * 30);
        }
    }

    public void setVillain(boolean villain){
        this.isVillain = villain;
    }

    public void resetStats(){
        time = 0;
        coins = 0;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
        if (inGame){
            setSmall(true);
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 3, false, false));
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false));
            contents = getPlayer().getInventory().getContents();
            getPlayer().getInventory().clear();
            location = getPlayer().getLocation();
            getPlayer().teleport(plugin.getGame().getGameLocation());
            gameMode = getPlayer().getGameMode();
            getPlayer().setGameMode(GameMode.ADVENTURE);
            health = getPlayer().getHealth();
            getPlayer().setHealth(20);
            getPlayer().setFoodLevel(20);
            foodLevel = getPlayer().getFoodLevel();

            plugin.getGame().playings.add(this);
            gameTask = new BukkitRunnable() {
                @Override
                public void run() {
                    task();
                }
            }.runTaskTimer(plugin, 1L, 0L);
            plugin.getCameraManager().changeCamera(getPlayer(), CameraManager.Perspective.THIRD_PERSON_BACK);
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getCameraManager().lockCamera(getPlayer());
                    plugin.getCameraManager().lockMovementAxis(getPlayer(), 'z');
                }
            }.runTaskLater(plugin, 3L);
        } else {
            plugin.getCameraManager().changeSize(player, false);
            plugin.getCameraManager().unlockCamera(getPlayer());
            plugin.getCameraManager().changeCamera(getPlayer(), CameraManager.Perspective.FIRST_PERSON);
            plugin.getCameraManager().unlockMovementAxis(getPlayer(), 'z');
            getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
            getPlayer().removePotionEffect(PotionEffectType.JUMP);
            if (contents != null) {
                getPlayer().getInventory().setContents(contents);
            }
            if (location != null) {
                getPlayer().teleport(location);
            }
            if (gameMode != null) {
                getPlayer().setGameMode(gameMode);
            }
            if (health != 0) {
                getPlayer().setHealth(health);
            }
            if (foodLevel != 0) {
                getPlayer().setFoodLevel(foodLevel);
            }
            checkpoint = null;
            if (gameTask != null){
                gameTask.cancel();
            }
            gameBar.removeAll();
            gameBar.setVisible(false);
            gameBar = null;

            gameUpBar.removeAll();
            gameUpBar.setVisible(false);
            gameUpBar = null;
            time = 0;
            coins = 0;

            plugin.getGame().playings.remove(this);
        }
    }

    public boolean dead = false;
    private ArmorStand deadStand;

    public void teleport(Location loc, Pipe.Direction direction, Consumer<Boolean> callback){
        getPlayerAnimationHandler().setPause(true);
        sendFade(35, 0, 70, ChatColor.BLACK, null);
        GameSound.PIPE_TELEPORT.play(getPlayer(), 0.2f, 1f);

        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayer().teleport(loc);
                GameSound.PIPE_TELEPORT.play(getPlayer(), 0.2f, 1f);
                teleportAnim(direction);
                callback.accept(true);
            }
        }.runTaskLater(plugin, 40L);
    }

    public void teleportAnim(Pipe.Direction direction){
        ItemStack helmet = getPlayer().getInventory().getHelmet();
        getPlayer().getEquipment().setHelmet(XMaterial.AIR.parseItem());
        ArmorStand armorStand = getPlayer().getWorld().spawn(getPlayer().getLocation(), ArmorStand.class);

        plugin.getTasksManager().setDirection(getPlayer().getLocation(), armorStand);
        Location aLoc = armorStand.getLocation();
        armorStand.teleport(aLoc);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.getEquipment().setHelmet(helmet, true);

        Location originalLoc = armorStand.getLocation();
        Location loc = armorStand.getLocation().clone();
        if (direction == Pipe.Direction.UP){
            loc.subtract(0,  2, 0);
        } else if (direction == Pipe.Direction.DOWN){
            loc.add(0,  2, 0);
        } else if (direction == Pipe.Direction.LEFT){
            loc.add(2,  0, 0);
        } else if (direction == Pipe.Direction.RIGHT){
            loc.subtract(2,  0, 0);
        }

        double limit = direction == Pipe.Direction.UP || direction == Pipe.Direction.DOWN ? originalLoc.getY() : originalLoc.getX();

        plugin.getUtils().debugToDev("ArmorStand works!");
        new BukkitRunnable() {
            @Override
            public void run() {
                double check = direction == Pipe.Direction.UP || direction == Pipe.Direction.DOWN ? loc.getY() : loc.getX();
                boolean cancel;
                if (direction == Pipe.Direction.UP || direction == Pipe.Direction.LEFT){
                    cancel = check <= limit;
                } else {
                    cancel = check >= limit;
                }
                cancel = !cancel;
                if (cancel) {
                    plugin.getUtils().debugToDev("ArmorStand removed!");
                    armorStand.remove();
                    getPlayerAnimationHandler().setPause(false);
                    cancel();
                    return;
                }
                if (direction == Pipe.Direction.UP || direction == Pipe.Direction.DOWN){
                    if (direction == Pipe.Direction.UP){
                        loc.add(0, 0.075, 0);
                    } else {
                        loc.subtract(0, 0.075, 0);
                    }
                } else {
                    if (direction == Pipe.Direction.LEFT){
                        loc.subtract(0.075, 0, 0);
                    } else {
                        loc.add(0.075, 0, 0);
                    }
                }
                armorStand.teleport(loc);
            }
        }.runTaskTimer(plugin, 1L, 0L);
    }

    public void death(){
        if (!isInGame()) return;

        plugin.getGame().getSpawners().values().forEach(ZombieObject::toggleIA);
        dead = true;
        int id = getPlayerAnimationHandler().getDirection() == Direction.RIGHT ? 21 : 22;
        if (isSmall()){
            id = getPlayerAnimationHandler().getDirection() == Direction.RIGHT ? 39 : 40;
        }
        AnimationHandler.changeAnimation(getPlayer(), id);
        getPlayerAnimationHandler().setPause(true);
        ItemStack helmet = getPlayer().getInventory().getHelmet();
        getPlayer().getEquipment().setHelmet(XMaterial.AIR.parseItem());
        getPlayer().getInventory().clear();
        getPlayer().setHealth(20);
        getPlayer().setFoodLevel(20);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, false, false));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 3, false, false));

        GameSound.DEATH.play(getPlayer());
        sendFade(60, 0, 60, ChatColor.BLACK, null);

        deadStand = getPlayer().getWorld().spawn(getPlayer().getLocation(), ArmorStand.class);
        plugin.getTasksManager().setDirection(getPlayer().getLocation(), deadStand);
        Location aLoc = deadStand.getLocation();
        aLoc.add(-0.15, 0, 1);
        deadStand.teleport(aLoc);
        deadStand.setInvisible(true);
        deadStand.setInvulnerable(true);
        deadStand.setGravity(false);
        deadStand.setCollidable(false);
        deadStand.getEquipment().setHelmet(helmet, true);

        double yOrigin = deadStand.getLocation().getBlockY();
        double yUp = yOrigin+1.3;
        double yDown = yOrigin-12.3;
        new BukkitRunnable() {
            private boolean up = true;
            @Override
            public void run() {
                Location loc = deadStand.getLocation();
                double y = loc.getY();
                double difference = up ? yUp-y : y-yDown;

                plugin.getUtils().debugToDev("Difference: "+difference + " | Up: "+up);

                double speed = 0.025;
                //smooth animation depending on the distance
                if (difference > 0.5){
                    speed = 0.15;
                } else if (difference > 0.3){
                    speed = 0.1;
                } else if (difference > 0.1){
                    speed = 0.05;
                }

                if (!up){
                    speed *= 2;
                }

                if (up){
                    loc.add(0, speed, 0);
                } else {
                    loc.subtract(0, speed, 0);
                }

                deadStand.teleport(loc);

                if (!up && difference <= 0.1){
                    deadStand.remove();
                    cancel();
                    dead = false;
                    plugin.getGame().getSpawners().values().forEach(ZombieObject::toggleIA);
                    plugin.getGame().reset();
                    getPlayerAnimationHandler().setPause(false);
                    teleportToLastLoc();
                    resetStats();
                    setSmall(true);
                    return;
                }
                if (up && difference <= 0.01){
                    up = false;
                }
            }
        }.runTaskTimer(plugin, 1L, 0L);
    }

    private Location checkpoint;
    public void teleportToLastLoc(){
        Location loc = checkpoint != null ? checkpoint : plugin.getGame().getGameLocation();
        getPlayer().teleport(loc);
        plugin.getCameraManager().sendShaderEffect(getPlayer(), 25, 0.8f, 1);
    }

    public void sendFade(int in, int stay, int out, ChatColor color, @Nullable String text){
        String cmd = "screeneffect fullscreen %s %s %s %s freeze %s%s";
        cmd = String.format(cmd, color.name(), in, stay, out, getName(), text == null ? "" : " "+text);
        plugin.getUtils().debugToDev("Command: "+cmd);

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),  cmd);
    }
}
