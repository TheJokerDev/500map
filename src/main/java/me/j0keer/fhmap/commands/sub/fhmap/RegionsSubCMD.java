package me.j0keer.fhmap.commands.sub.fhmap;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Regions;
import me.j0keer.fhmap.enums.SenderTypes;
import me.j0keer.fhmap.type.SubCMD;
import me.j0keer.fhmap.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RegionsSubCMD extends SubCMD {
    public RegionsSubCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "regions";
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
        if (args.length != 2){
            List<String> msg = new ArrayList<>();
            msg.add("{prefix}&cUso del sub-comando: /map regions <set|remove|tp> <region>");
            msg.add("{prefix}&cRegions: &7" + Arrays.stream(Regions.values()).map(Regions::name).reduce((a, b) -> a + ", " + b).orElse(""));
            msg.forEach(s -> sendMSG(sender, s));
            return true;
        }
        String action = args[0].toLowerCase();
        String region = args[1].toUpperCase();
        Regions reg;
        try {
            reg = Regions.valueOf(region);
        } catch (IllegalArgumentException e){
            sendMSG(sender, "{prefix}&cLa region &7" + region + " &cno existe.");
            return true;
        }
        switch (action) {
            case "set" -> {
                try {
                    Region selection = getPlugin().getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(p)).getSelection(BukkitAdapter.adapt(p.getWorld()));
                    Location loc1 = LocationUtil.locationConverter(selection.getWorld(), selection.getMinimumPoint());
                    Location loc2 = LocationUtil.locationConverter(selection.getWorld(), selection.getMaximumPoint());
                    reg.saveRegion(loc1, loc2);
                    //p.chat("/sel");
                    getPlugin().getWorldEdit().getSessionManager().get(BukkitAdapter.adapt(p)).getRegionSelector(BukkitAdapter.adapt(p.getWorld())).clear();
                    getPlugin().getUtils().sendMSG(sender, "{prefix}¡Se ha establecido la región de juego!");
                } catch (IncompleteRegionException e) {
                    getPlugin().getUtils().sendMSG(sender, "{prefix}¡Debes seleccionar una región. Usa //wand para obtener el selector.");
                }
            }
            case "remove" -> {
                if (reg.getCuboID() == null) {
                    sendMSG(sender, "{prefix}&cLa region &7" + region + " &cno ha sido definida aún.");
                    return true;
                }
                reg.removeRegion();
                sendMSG(sender, "{prefix}&aLa region &7" + region + " &aha sido eliminada.");
            }
            case "tp" -> {
                if (reg.getCuboID() == null) {
                    sendMSG(sender, "{prefix}&cLa region &7" + region + " &cno ha sido definida aún.");
                    return true;
                }
                Location loc = reg.getCuboID().getCenter();
                p.teleport(loc);
                sendMSG(sender, "{prefix}&aTeletransportado a la region &7" + region + "&a.");
            }
            default -> sendMSG(sender, "{prefix}&cUso del sub-comando: /map regions <set|remove|tp> <region>");
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("set", "remove", "tp"), new ArrayList<>());
        }
        if (args.length == 2){
            return StringUtil.copyPartialMatches(args[1], Arrays.stream(Regions.values()).map(Regions::name).toList(), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
