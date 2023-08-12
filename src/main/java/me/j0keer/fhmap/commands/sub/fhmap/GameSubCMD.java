package me.j0keer.fhmap.commands.sub.fhmap;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.SenderTypes;
import me.j0keer.fhmap.type.*;
import me.j0keer.fhmap.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSubCMD extends SubCMD {
    Main plugin;
    public GameSubCMD(Main plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "game";
    }

    @Override
    public String getPermission() {
        return "map.admin";
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.PLAYER;
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;

        if (args.length == 0){
            sendMSG(sender, "commands.main.needArguments");
            return true;
        }
        String var1 = args[0].toLowerCase();
        switch (var1){
            case "setup" -> {
                if (args.length == 1){
                    sendMSG(sender, "{prefix}&cNecesitas especificar el argumento a modificar con el comando /map game setup.");
                    return true;
                }
                String var2 = args[1].toLowerCase();
                if (var2.equals("setvillainspawn")){
                    getPlugin().getGame().setVillainSpawn(p.getLocation());
                    sendMSG(sender, "{prefix}&aHas establecido el spawn del villano en la ubicación actual.");
                    return true;
                }
                if (var2.equals("setvillaingamespawn")){
                    getPlugin().getGame().setVillainGameSpawn(p.getLocation());
                    sendMSG(sender, "{prefix}&aHas establecido el spawn del villano en la ubicación actual.");
                    return true;
                }
                if (var2.equals("setspawn") || var2.equals("setlobby")){
                    getPlugin().getGame().setSpawn(p.getLocation());
                    String type = var2.equals("setspawn") ? "spawn" : "lobby";
                    sendMSG(sender, "{prefix}&aHas establecido el "+type+" del juego.");
                    return true;
                }
                if (var2.equals("setgamespawn") || var2.equals("setgamelobby")){
                    getPlugin().getGame().setGameSpawn(p.getLocation());
                    String type = var2.equals("setgamespawn") ? "spawn" : "lobby";
                    sendMSG(sender, "{prefix}&aHas establecido el "+type+" del juego 2d.");
                    return true;
                }
                if (var2.equals("addspawner")){
                    Location loc = p.getLocation();
                    getPlugin().getGame().addSpawner(loc);
                    sendMSG(sender, "{prefix}&aHas añadido un spawner en la ubicación actual.");
                    return true;
                }
                if (var2.equals("removespawner")){
                    if (getPlugin().getGame().removeSpawner()){
                        sendMSG(sender, "{prefix}&aHas eliminado el último spawner añadido.");
                    } else {
                        sendMSG(sender, "{prefix}&cNo hay ningún spawner para eliminar.");
                    }
                    return true;
                }
                if (var2.equals("addpipe")){
                    if (args.length == 2){
                        sendMSG(sender, "{prefix}&cNecesitas especificar el id de la tubería.");
                        return true;
                    }
                    String id = args[2];
                    boolean result = getPlugin().getGame().addPipe(id);
                    if (result){
                        sendMSG(sender, "{prefix}&aHas añadido una tubería con el id "+id+".");
                    } else {
                        sendMSG(sender, "{prefix}&cYa existe una tubería con el id "+id+".");
                    }
                    return true;
                }
                if (var2.equals("removepipe")){
                    if (args.length == 2){
                        sendMSG(sender, "{prefix}&cNecesitas especificar el id de la tubería.");
                        return true;
                    }
                    String id = args[2];
                    boolean result = getPlugin().getGame().removePipe(id);
                    if (result){
                        sendMSG(sender, "{prefix}&aHas eliminado la tubería con el id "+id+".");
                    } else {
                        sendMSG(sender, "{prefix}&cNo existe una tubería con el id "+id+".");
                    }
                    return true;
                }
                if (var2.equals("editpipe")) {
                    if (args.length == 2) {
                        sendMSG(sender, "{prefix}&cNecesitas especificar el id de la tubería.");
                        return true;
                    }
                    String id = args[2];
                    if (!getPlugin().getGame().getPipeHashMap().containsKey(id)) {
                        sendMSG(sender, "{prefix}&cNo existe una tubería con el id " + id + ".");
                        return true;
                    }
                    if (args.length == 3) {
                        sendMSG(sender, "{prefix}&cNecesitas especificar el argumento a modificar con el comando /map game setup editpipe.");
                        return true;
                    }
                    String option = args[3].toLowerCase();
                    Pipe pipe = getPlugin().getGame().getPipeHashMap().get(id);
                    switch (option) {
                        case "setlocation" -> {
                            pipe.setLocation(p.getLocation());
                            sendMSG(sender, "{prefix}&aHas establecido la ubicación de la tubería con el id " + id + ".");
                        }
                        case "setregion" -> {
                            try {
                                Region selection = getPlugin().getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(p)).getSelection(BukkitAdapter.adapt(p.getWorld()));
                                Location loc1 = LocationUtil.locationConverter(selection.getWorld(), selection.getMinimumPoint());
                                Location loc2 = LocationUtil.locationConverter(selection.getWorld(), selection.getMaximumPoint());
                                pipe.setRegion(loc1, loc2);
                                getPlugin().getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(p)).getRegionSelector(BukkitAdapter.adapt(p.getWorld())).clear();
                                getPlugin().getUtils().sendMSG(sender, "{prefix}&a¡Se ha establecido la región de la tubería! &7(ID: &e" + id + "&7)");
                            } catch (IncompleteRegionException e) {
                                getPlugin().getUtils().sendMSG(sender, "{prefix}&c¡Debes seleccionar una región. Usa //wand para obtener el selector.");
                            }
                        }
                        case "setdirection" -> {
                            if (args.length == 4) {
                                sendMSG(sender, "{prefix}&cNecesitas especificar la dirección de la tubería.");
                                return true;
                            }
                            String dir = args[4].toUpperCase();
                            Pipe.Direction direction;
                            try {
                                direction = Pipe.Direction.valueOf(dir);
                            } catch (IllegalArgumentException e) {
                                sendMSG(sender, "{prefix}&cLa dirección especificada no existe.");
                                return true;
                            }
                            pipe.setDirection(direction);
                            sendMSG(sender, "{prefix}&aHas establecido la dirección de la tubería con el id " + id + ".");
                        }
                        default -> sendMSG(sender, "{prefix}&cEl argumento especificado no existe.");
                    }
                    return true;
                }
                if (var2.equals("adddeathregion")){
                    try {
                        Region selection = getPlugin().getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(p)).getSelection(BukkitAdapter.adapt(p.getWorld()));
                        Location loc1 = LocationUtil.locationConverter(selection.getWorld(), selection.getMinimumPoint());
                        Location loc2 = LocationUtil.locationConverter(selection.getWorld(), selection.getMaximumPoint());
                        getPlugin().getGame().addDeathRegion(loc1, loc2);
                        getPlugin().getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(p)).getRegionSelector(BukkitAdapter.adapt(p.getWorld())).clear();
                        getPlugin().getUtils().sendMSG(sender, "{prefix}&a¡Se ha establecido la región de muerte! &7("+getPlugin().getGame().getDeathRegionHashMap().size()+")");
                    } catch (IncompleteRegionException e) {
                        getPlugin().getUtils().sendMSG(sender, "{prefix}&c¡Debes seleccionar una región. Usa //wand para obtener el selector.");
                    }
                    return true;
                }
                if (var2.equals("removedeathregion")){
                    if (getPlugin().getGame().removeDeathRegion()){
                        sendMSG(sender, "{prefix}&aHas eliminado la última región de muerte añadida.");
                    } else {
                        sendMSG(sender, "{prefix}&cNo hay ninguna región de muerte para eliminar.");
                    }
                    return true;
                }
            }
            case "spawn" -> {
                if (getPlugin().getGame().getSpawn() == null){
                    sendMSG(sender, "{prefix}&cNo has establecido el spawn del juego.");
                    return true;
                }
                p.teleport(getPlugin().getGame().getSpawn());
                sendMSG(sender, "{prefix}&aTe has teletransportado al spawn del juego.");
                XSound.ENTITY_ENDERMAN_TELEPORT.play(p);
                return true;
            }
            case "initend" -> {
                if(getPlugin().getGame().isEnd()) {
                    sendMSG(sender, "{prefix}&cEl juego ya ha terminado.");
                    return true;
                }
                getPlugin().getGame().setEnd(true);
            }
            case "togglesize" -> {
                if (args.length < 2) {
                    sendMSG(sender, "{prefix}&cNecesitas especificar el nombre de un jugador.");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMSG(sender, "{prefix}&cEl jugador especificado no está conectado.");
                    return true;
                }
                DataPlayer dp = getPlugin().getDataManager().getDataPlayer(target);
                if (dp.isSizeCooldown()) {
                    sendMSG(sender, "{prefix}&cEl jugador especificado ya está cambiando de tamaño. Espera un ratito para poder usar de nuevo este comando.");
                    return true;
                }
                boolean small = !dp.isSmall();
                dp.setSmall(small);
                sendMSG(sender, "{prefix}&aHas cambiado el tamaño de " + target.getName() + " a " + (small ? "pequeño" : "grande") + ".");
                return true;
            }
            case "summon" -> {
                if (args.length == 1){
                    sendMSG(sender, "{prefix}&cNecesitas especificar el tipo de entidad a invocar.");
                    return true;
                }
                String var2 = args[1].toUpperCase();
                EntityType type;
                try {
                    type = EntityType.valueOf(var2);
                } catch (IllegalArgumentException e){
                    sendMSG(sender, "{prefix}&cNo se ha encontrado la entidad especificada.");
                    return true;
                }
                int amount = 1;
                if (args.length > 2){
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e){
                        sendMSG(sender, "{prefix}&cEl argumento de cantidad debe ser un número.");
                        return true;
                    }
                }
                double radius = 3;
                if (args.length > 3){
                    try {
                        radius = Double.parseDouble(args[3]);
                    } catch (NumberFormatException e){
                        sendMSG(sender, "{prefix}&cEl argumento de radio debe ser un número.");
                        return true;
                    }
                }

                List<Material> canSpawnMaterials = new ArrayList<>();
                canSpawnMaterials.add(Material.GRASS_BLOCK);
                canSpawnMaterials.add(Material.SAND);
                canSpawnMaterials.add(Material.SANDSTONE);
                canSpawnMaterials.add(Material.COARSE_DIRT);
                canSpawnMaterials.add(Material.DIRT);

                for (int i = 0; i < amount; i++) {
                    Location loc = p.getLocation().add(Math.random() * radius, 1, Math.random() * radius);
                    //get top block
                    loc.setY(p.getWorld().getHighestBlockYAt(loc));
                    if (!canSpawnMaterials.contains(loc.getBlock().getType())){
                        i--;
                        continue;
                    }
                    loc.add(0, 1, 0);
                    p.getWorld().spawnEntity(loc, type);
                }
                sendMSG(sender, "{prefix}&aHas invocado "+amount+" entidades de tipo "+type.name()+" en un radio de "+radius+" bloques.");
                return true;
            }
            case "test" -> {
                if (args.length == 1){
                    sendMSG(sender, "{prefix}&cNecesitas especificar el tipo de test a realizar.");
                    return true;
                }
                String var2 = args[1].toLowerCase();
                if(var2.equals("attack")){
                    BlockFace face = !args[2].equalsIgnoreCase("left") ? BlockFace.EAST : BlockFace.WEST;
                    plugin.getGame().attact1(face, p.getLocation().clone());
                }
                if(var2.equals("attack2")){
                    Location loc = p.getLocation().clone();
                    loc.setYaw(90);
                    plugin.getGame().attact2(loc);
                }
                if(var2.equals("item")){
                    ItemStack item = p.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(Integer.parseInt(args[2]));
                    item.setItemMeta(meta);
                }
                if(var2.equals("shader")){//mapa test shader <player> long_time float_size int_id
                    if(args.length != 6){
                        sendMSG(sender, "{prefix}&c <long_time> <float_size> <ind_id>.");
                        return true;
                    }

                        Player player = Bukkit.getPlayerExact(args[2]);
                        long time = Long.parseLong(args[3]);
                        float size = Float.parseFloat(args[4]);
                        int id = Integer.parseInt(args[5]);
                        plugin.getCameraManager().sendShaderEffect(player, time, size, id);

                }
                if (var2.equals("stress")){
                    if (args.length == 2){
                        sendMSG(sender, "{prefix}&cNecesitas especificar el jugador para estresar.");
                        return true;
                    }
                    Player target = getPlugin().getServer().getPlayer(args[2]);
                    if (target == null){
                        sendMSG(sender, "{prefix}&cNo se ha encontrado al jugador especificado.");
                        return true;
                    }
                    int time = 0;
                    if (args.length > 3){
                        try {
                            time = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e){
                            sendMSG(sender, "{prefix}&cEl argumento de tiempo debe ser un número.");
                            return true;
                        }
                    }
                    getPlugin().getGame().playStressEffect(target, time);
                    sendMSG(sender, "{prefix}&aHas estresado al jugador "+target.getName()+".");
                }
                if (var2.equals("villain")){
                    if (args.length == 2){
                        sendMSG(sender, "{prefix}&cNecesitas especificar la opción.");
                        return true;
                    }
                    String var3 = args[2].toLowerCase();
                    if (var3.equals("summon")){
                        Player t = p;
                        if (task != null) {
                            sendMSG(sender, "{prefix}&cYa hay un villano en el mapa.");
                            return true;
                        }
                        if (args.length == 4){
                            t = getPlugin().getServer().getPlayer(args[3]);
                            if (t == null){
                                sendMSG(sender, "{prefix}&cNo se ha encontrado al jugador especificado.");
                                return true;
                            }
                        }
                        task(t);
                        sendMSG(sender, "{prefix}&aHas invocado un villano.");
                        return true;
                    }
                    if (var3.equals("remove")){
                        getPlugin().getGame().getVillains().values().forEach(target -> target.remove());
                        if (task == null) {
                            sendMSG(sender, "{prefix}&cNo hay ningún villano en el mapa.");
                            return true;
                        }

                        task(p);
                        sendMSG(sender, "{prefix}&aHas eliminado el villano.");
                        return true;
                    }
                    if (var3.equals("particles")){
                        Location loc = getPlugin().getGame().getVillainSpawnLocation().clone();
                        ParticleDisplay display = ParticleDisplay.simple(loc.subtract(0, 2, 0), Particle.DRAGON_BREATH);
                        ParticleDisplay display2 = ParticleDisplay.simple(loc.subtract(0, 2, 0), Particle.FLAME);
                        //XParticle.blackhole(getPlugin(), 15, 2, 15, 2, 15*20, display);
                        //XParticle.blackSun(0.25, 0.5, 0.25, 5, display);
                        XParticle.explosionWave(getPlugin(), 25, display, display2);
                        //XParticle.circularBeam(getPlugin(), 2, 25, 25, 2, display);
                        //XParticle.eye(2, 3, 5, 1, display);
                        sendMSG(sender, "{prefix}&aHas invocado las particulas.");
                    }
                }
                if (var2.equals("item")){
                    ItemObject object = new ItemObject(ItemObject.ItemObjectType.COIN, getPlugin());
                    object.spawn(p.getLocation());

                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> object.remove(), 200);
                }
            }
            case "villainjoin" -> {
                if (args.length == 2){
                    String var2 = args[1].toLowerCase();
                    Player t = getPlugin().getServer().getPlayer(var2);
                    if (t == null){
                        sendMSG(sender, "{prefix}&cNo se ha encontrado al jugador especificado.");
                        return true;
                    }
                    DataPlayer dp = getPlugin().getDataManager().getDataPlayer(t);
                    if (getPlugin().getGame().getGameLocation() == null){
                        sendMSG(sender, "{prefix}&c¡No has establecido la ubicación del juego!");
                        return true;
                    }
                    if (dp.isInGame()){
                        sendMSG(sender, "{prefix}&c¡Ya está en una partida!");
                        return true;
                    }
                    dp.setInGame(true);
                    return true;
                }
                DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
                if (getPlugin().getGame().getGameLocation() == null){
                    sendMSG(sender, "{prefix}&c¡No has establecido la ubicación del juego!");
                    return true;
                }
                if (dp.isInGame()){
                    sendMSG(sender, "{prefix}&c¡Ya estás en una partida!");
                    return true;
                }
                //dp.setInGame(true);
                //Join as villain
                sendMSG(sender, "{prefix}&aHas entrado al juego.");
            }
            case "join" -> {
                if (args.length == 2){
                    String var2 = args[1].toLowerCase();
                    Player t = getPlugin().getServer().getPlayer(var2);
                    if (t == null){
                        sendMSG(sender, "{prefix}&cNo se ha encontrado al jugador especificado.");
                        return true;
                    }
                    DataPlayer dp = getPlugin().getDataManager().getDataPlayer(t);
                    if (getPlugin().getGame().getGameLocation() == null){
                        sendMSG(sender, "{prefix}&c¡No has establecido la ubicación del juego!");
                        return true;
                    }
                    if (dp.isInGame()){
                        sendMSG(sender, "{prefix}&c¡Ya está en una partida!");
                        return true;
                    }
                    dp.setInGame(true);
                    return true;
                }
                DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
                if (getPlugin().getGame().getGameLocation() == null){
                    sendMSG(sender, "{prefix}&c¡No has establecido la ubicación del juego!");
                    return true;
                }
                if (dp.isInGame()){
                    sendMSG(sender, "{prefix}&c¡Ya estás en una partida!");
                    return true;
                }
                dp.setInGame(true);
                sendMSG(sender, "{prefix}&aHas entrado al juego.");
            }
            case "villainleave" -> {
                if (args.length == 2){
                    String var2 = args[1].toLowerCase();
                    Player t = getPlugin().getServer().getPlayer(var2);
                    if (t == null){
                        sendMSG(sender, "{prefix}&cNo se ha encontrado al jugador especificado.");
                        return true;
                    }
                    DataPlayer dp = getPlugin().getDataManager().getDataPlayer(t);
                    if (!dp.isInGame()){
                        sendMSG(sender, "{prefix}&c¡No estás en una partida!");
                        return true;
                    }
                    dp.setInGame(false);
                    sendMSG(sender, "{prefix}&aHas salido del juego.");
                }
                DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
                if (!dp.isInGame()){
                    sendMSG(sender, "{prefix}&c¡No estás en una partida!");
                    return true;
                }
                //dp.setInGame(false);
                //TO-DO: Leave as villain
                sendMSG(sender, "{prefix}&aHas salido del juego.");
            }
            case "leave" -> {
                if (args.length == 2){
                    String var2 = args[1].toLowerCase();
                    Player t = getPlugin().getServer().getPlayer(var2);
                    if (t == null){
                        sendMSG(sender, "{prefix}&cNo se ha encontrado al jugador especificado.");
                        return true;
                    }
                    DataPlayer dp = getPlugin().getDataManager().getDataPlayer(t);
                    if (!dp.isInGame()){
                        sendMSG(sender, "{prefix}&c¡No estás en una partida!");
                        return true;
                    }
                    dp.setInGame(false);
                    sendMSG(sender, "{prefix}&aHas salido del juego.");
                }
                DataPlayer dp = getPlugin().getDataManager().getDataPlayer(p);
                if (!dp.isInGame()){
                    sendMSG(sender, "{prefix}&c¡No estás en una partida!");
                    return true;
                }
                dp.setInGame(false);
                sendMSG(sender, "{prefix}&aHas salido del juego.");
            }
            case "reset" -> {
                getPlugin().getGame().reset();
                sendMSG(sender, "{prefix}&aHas reiniciado el juego.");
            }
            case "music" ->{
                if(args.length != 3){
                    sendMSG(sender, "{prefix}&aUsa music play <name> o music stop <name>.");
                    return true;
                }

                String type = args[1];
                boolean play = type.equalsIgnoreCase("play");

                String sound = args[2].toLowerCase();
                GameMusic music = null;
                switch(sound){
                    case "overworld":{
                        music = plugin.getGame().getOverworld_music();
                        break;
                    }
                    case "nether":{
                        music = plugin.getGame().getNether_music();
                        break;
                    }
                    case "boss":{
                        music = plugin.getGame().getBoss_music();
                        break;
                    }
                }

                if(music == null){
                    sendMSG(sender, "{prefix}&cEsta musica no existe!");
                    return true;
                }else{
                    if(play){
                        music.play();
                        sendMSG(sender, "{prefix}&cSe comenzo a reproducir la musica");
                    }else {
                        music.stop();
                        sendMSG(sender, "{prefix}&cSe detuvo la musica");
                    }
                }
            }
        }
        return true;
    }

    private BukkitTask task;
    private ArmorStand stand;

    public void task(Player p){
        if (task != null){
            task.cancel();
            stand.remove();
            stand = null;
            task = null;
        } else {
            ItemStack item = XMaterial.PHANTOM_MEMBRANE.parseItem();
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(112);
            item.setItemMeta(meta);

            stand = p.getLocation().getWorld().spawn(getPlugin().getUtils().lookAt(getPlugin().getGame().getVillainSpawnLocation(), p.getLocation()), ArmorStand.class);
            stand.setRotation(stand.getLocation().getYaw(), stand.getLocation().getPitch());
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.getEquipment().setHelmet(item);

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    Location lookAt = getPlugin().getUtils().lookAt(stand.getLocation(), p.getLocation());
                    stand.teleport(lookAt);
                    stand.setRotation(stand.getLocation().getYaw(), stand.getLocation().getPitch());
                }
            }.runTaskTimer(getPlugin(), 1L, 0);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("music", "togglesize", "initend", "setup", "spawn", "summon", "test", "join", "leave", "villainjoin", "villainjoin", "reset"), new ArrayList<>());
        }
        if (args.length == 2){
            if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("villainjoin")){
                List<String> list = new ArrayList<>(plugin.getDataManager().getPlayers().values().stream().filter(dp -> !dp.isInGame()).map(DataPlayer::getName).toList());
                return StringUtil.copyPartialMatches(args[1], list, new ArrayList<>());
            }
            if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("villainleave")){
                List<String> list = new ArrayList<>(plugin.getDataManager().getPlayers().values().stream().filter(dp -> dp.isInGame()).map(DataPlayer::getName).toList());
                return StringUtil.copyPartialMatches(args[1], list, new ArrayList<>());
            }
            if (args[0].equalsIgnoreCase("togglesize")) {
                return null;
            }
            if (args[0].equalsIgnoreCase("setup")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("setvillainspawn", "setvillaingamespawn", "setspawn", "setlobby", "setgamespawn",  "setlobbyspawn", "addspawner", "removespawner", "addpipe", "removepipe", "editpipe", "adddeathregion", "removedeathregion"), new ArrayList<>());
            }
            if (args[0].equalsIgnoreCase("summon")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.stream(EntityType.values()).map(EntityType::name).toList(), new ArrayList<>());
            }
            if (args[0].equalsIgnoreCase("test")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("stress", "villain"), new ArrayList<>());
            }
            if(args[0].equalsIgnoreCase("music")){
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("play", "stop"), new ArrayList<>());
            }
        }
        if (args.length == 3){
            String var1 = args[0];
            String var2 = args[1].toLowerCase();
            if(var1.equalsIgnoreCase("music") && (var2.equalsIgnoreCase("play") || var2.equalsIgnoreCase("stop"))){
                List<String> sounds = new ArrayList<>();
                for(GameMusic.Music music : GameMusic.Music.values()){
                    sounds.add(music.name().toLowerCase());
                }
                return StringUtil.copyPartialMatches(args[2], sounds, new ArrayList<>());
            }
            if (var1.equals("setup") && (var2.equals("removepipe") || var2.equals("editpipe"))){
                List<String> pipe = new ArrayList<>(getPlugin().getGame().getPipeHashMap().keySet());
                return StringUtil.copyPartialMatches(args[2], pipe, new ArrayList<>());
            }
            if (var1.equals("test") && (var2.equals("villain"))){
                return StringUtil.copyPartialMatches(args[2], Arrays.asList("summon", "remove"), new ArrayList<>());
            }
        }
        if (args.length == 4){
            String var1 = args[0];
            String var2 = args[1].toLowerCase();
            if (var1.equals("setup") && var2.equals("editpipe")){
                return StringUtil.copyPartialMatches(args[3], Arrays.asList("setlocation", "setregion", "setdirection"), new ArrayList<>());
            }
            if (var1.equals("test") && (var2.equals("villain") && args[2].equals("summon"))){
                return null;
            }
        }
        if (args.length == 5){
            String var1 = args[0];
            String var2 = args[1].toLowerCase();
            String var4 = args[3].toLowerCase();
            if (var1.equals("setup") && var2.equals("editpipe") && var4.equals("setdirection")){
                return StringUtil.copyPartialMatches(args[4], Arrays.stream(Pipe.Direction.values()).map(Pipe.Direction::name).toList(), new ArrayList<>());
            }
        }
        return null;
    }
}
