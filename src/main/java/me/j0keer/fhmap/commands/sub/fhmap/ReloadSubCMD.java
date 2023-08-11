package me.j0keer.fhmap.commands.sub.fhmap;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Regions;
import me.j0keer.fhmap.enums.SenderTypes;
import me.j0keer.fhmap.type.SubCMD;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadSubCMD extends SubCMD {

    public ReloadSubCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "none";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("recargar", "reiniciar");
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.BOTH;
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        if (args.length == 0){
            getPlugin().getConfigUtil().reloadAll();
            getPlugin().getItemsManager().reloadItems();
            Regions.SPAWN.reloadCuboIDs();
            sendMSG(sender, "commands.main.reload.success");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        return new ArrayList<>();
    }
}
