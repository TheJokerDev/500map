package me.j0keer.fhmap.commands;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.commands.sub.fhmap.*;
import me.j0keer.fhmap.type.CMD;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FHMapCMD extends CMD {

    public FHMapCMD(Main plugin) {
        super(plugin);
        addSubCMD(new ReloadSubCMD(plugin));
        addSubCMD(new ReloadPluginSubCMD(plugin));
        addSubCMD(new ToggleModuleSubCMD(plugin));
        addSubCMD(new RegionsSubCMD(plugin));
        addSubCMD(new GameSubCMD(plugin));
    }

    @Override
    public String getName() {
        return "500map";
    }

    @Override
    public String getDescription() {
        return "Main command of plugin.";
    }

    @Override
    public String getPermission() {
        return "none";
    }

    @Override
    public String getPermissionError() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("map", "mapa");
    }

    @Override
    public boolean isTabComplete() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sendMSG(sender, "commands.main.needArguments");
            return true;
        }
        return executeCMD(sender, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0){
            return execute(sender, label, args);
        }
        return new ArrayList<>();
    }
}
