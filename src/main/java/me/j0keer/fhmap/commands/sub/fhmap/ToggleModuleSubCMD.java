package me.j0keer.fhmap.commands.sub.fhmap;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Modules;
import me.j0keer.fhmap.enums.SenderTypes;
import me.j0keer.fhmap.type.SubCMD;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToggleModuleSubCMD extends SubCMD {
    public ToggleModuleSubCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "togglemodule";
    }

    @Override
    public String getPermission() {
        return "map.admin";
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.BOTH;
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        if (args.length != 1){
            List<String> msg = new ArrayList<>();
            msg.add("{prefix}&cUso del sub-comando: /map togglemodule <module>");
            msg.add("{prefix}&cModules: &7" + Arrays.stream(Modules.values()).map(Modules::name).reduce((a, b) -> a + ", " + b).orElse(""));
            msg.forEach(s -> sendMSG(sender, s));
            return true;
        }
        String module = args[0].toUpperCase();
        Modules mod;
        try {
            mod = Modules.valueOf(module);
        } catch (IllegalArgumentException e){
            sendMSG(sender, "{prefix}&cEl modulo &7" + module + " &cno existe.");
            return true;
        }
        mod.toggle();
        sendMSG(sender, "{prefix}&aEl modulo &7" + module + " &aha sido " + (mod.isEnabled() ? "activado" : "desactivado") + "&a.");
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], Arrays.stream(Modules.values()).map(Modules::name).toList(), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
