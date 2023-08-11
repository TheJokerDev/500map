package me.j0keer.fhmap.commands.sub.fhmap;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Regions;
import me.j0keer.fhmap.enums.SenderTypes;
import me.j0keer.fhmap.type.SubCMD;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadPluginSubCMD extends SubCMD {

    public ReloadPluginSubCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reloadplugin";
    }

    @Override
    public String getPermission() {
        return "none";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("recargarplugin", "reiniciarplugin");
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.BOTH;
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        if (args.length == 0){
            if (getPlugin().getGame() != null){
                getPlugin().getGame().reset();
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "su reloadplugin 500Map");
                }
            }.runTaskLater(getPlugin(), 5L);
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
