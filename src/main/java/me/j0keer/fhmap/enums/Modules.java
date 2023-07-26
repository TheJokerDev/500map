package me.j0keer.fhmap.enums;

import me.j0keer.fhmap.Main;

public enum Modules {
    ENTITY_SPAWN("modules.entity-spawn"),
    PHYSICAL_INTERACTIONS("modules.physical-interactions"),
    BLOCK_BREAK("modules.block-break"),
    BLOCK_PLACE("modules.block-place"),
    FALL_DAMAGE("modules.fall-damage"),
    FOOD_LEVEL("modules.food-level"),
    PVP("modules.pvp");

    private String configKey;

    private Modules(String configKey){
        this.configKey = configKey;
    }

    Modules(){

    }

    public boolean isEnabled(){
        return Main.getPlugin().getConfig().getBoolean(configKey, false);
    }

    public void toggle(){
        Main.getPlugin().getConfig().set(configKey, !isEnabled());
        Main.getPlugin().saveConfig();
    }

    public static void loadValues(Main plugin){
        for (Modules module : values()){
            if (plugin.getConfig().get(module.configKey) == null){
                plugin.getConfig().set(module.configKey, false);
            }
        }
        plugin.saveConfig();
        plugin.reloadConfig();
    }
}
