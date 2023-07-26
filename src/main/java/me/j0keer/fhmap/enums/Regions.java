package me.j0keer.fhmap.enums;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.utils.Cuboid;
import me.j0keer.fhmap.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public enum Regions {
    SPAWN,
    ALL_GAME;

    private HashMap<String, Cuboid> cuboidHashMap = new HashMap<>();

    public void reloadCuboIDs(){
        cuboidHashMap.clear();
    }

    public Cuboid getCuboID(){
        String key = name().toLowerCase().replace("_", "-");
        if (cuboidHashMap.containsKey(key)){
            return cuboidHashMap.get(key);
        }
        FileConfiguration config = Main.getPlugin().getConfig();
        if (config.get("regions."+key)!=null){
            String loc1 = config.getString("regions."+key+".loc1");
            String loc2 = config.getString("regions."+key+".loc2");

            if (loc1==null || loc2==null){
                return null;
            }
            Cuboid cuboid;
            try {
                cuboid = new Cuboid(LocationUtil.getLocation(loc1), LocationUtil.getLocation(loc2));
            } catch (Exception ignored) {
                return null;
            }
            cuboidHashMap.put(key, cuboid);
            return cuboid;
        }
        return null;
    }

    public void saveRegion(Location loc1, Location loc2){
        String key = name().toLowerCase().replace("_", "-");
        FileConfiguration config = Main.getPlugin().getConfig();
        config.set("regions."+key+".loc1", LocationUtil.getString(loc1, false));
        config.set("regions."+key+".loc2", LocationUtil.getString(loc2, false));
        Main.getPlugin().saveConfig();
        Main.getPlugin().reloadConfig();

        if (cuboidHashMap.containsKey(key)){
            cuboidHashMap.remove(key);
        }
        cuboidHashMap.put(key, new Cuboid(loc1, loc2));
    }

    public void removeRegion(){
        String key = name().toLowerCase().replace("_", "-");
        FileConfiguration config = Main.getPlugin().getConfig();
        config.set("regions."+key, null);
        Main.getPlugin().saveConfig();
        Main.getPlugin().reloadConfig();

        if (cuboidHashMap.containsKey(key)){
            cuboidHashMap.remove(key);
        }
    }
}
