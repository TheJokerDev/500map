package me.j0keer.fhmap.commands;

import lombok.Getter;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.CMD;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

@Getter
public class ObjectiveCMD extends CMD {
    private BossBar bar;
    private BukkitTask task;
    public ObjectiveCMD(Main plugin) {
        super(plugin);
        bar = plugin.getServer().createBossBar("", BarColor.PURPLE, BarStyle.SOLID);

    }

    @Override
    public String getName() {
        return "objective";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getPermission() {
        return "map.admin";
    }

    @Override
    public String getPermissionError() {
        return null;
    }

    @Override
    public boolean isTabComplete() {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(getPermission())){
            sendMSG(sender, "{prefix}&cNo tienes permiso para ejecutar este comando.");
            return true;
        }
        if (args.length >= 1){
            String var1 = args[0].toLowerCase();
            switch (var1){
                case "remove" -> {
                    if (task!=null){
                        task.cancel();
                        task = null;
                    } else {
                        sendMSG(sender, "{prefix}&cNo hay ninguna barra de progreso activa.");
                    }
                    bar.removeAll();
                    return true;
                }
                case "set" -> {
                    if (task == null) {
                        if (args.length == 1){
                            sendMSG(sender, "{prefix}&cDebes especificar un mensaje.");
                            return true;
                        }
                        task = new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Player onlinePlayer : getPlugin().getServer().getOnlinePlayers()) {
                                    if (bar.getPlayers().contains(onlinePlayer)) {
                                        continue;
                                    }
                                    bar.addPlayer(onlinePlayer);
                                }
                            }
                        }.runTaskTimer(getPlugin(), 0L, 20L);
                        if (args.length == 2){
                            String var2 = args[1].toLowerCase();
                            if (var2.contains("{key}")){
                                String text = getPlugin().getConfig().getString("objectives."+var2.replace("{key}", ""));
                                if (text != null) {
                                    text = getPlugin().getUtils().formatMSG(sender, text);
                                    bar.setTitle(text);
                                    sendMSG(sender, "{prefix}&aBarra de progreso establecida.");
                                    return true;
                                }
                            }
                        }
                        StringBuilder builder = new StringBuilder();
                        Vector<String> vector = new Vector<>(Arrays.asList(args));
                        vector.remove(0);
                        args = vector.toArray(new String[0]);
                        for (int i = 0; i < args.length; i++) {
                            builder.append(args[i]);
                            if (i != args.length - 1) {
                                builder.append(" ");
                            }
                        }
                        bar.setTitle(getPlugin().getUtils().formatMSG(sender, builder.toString()));
                        sendMSG(sender, "{prefix}&aBarra de progreso establecida.");
                    } else {
                        sendMSG(sender, "{prefix}&cYa hay una barra de progreso activa.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(getPermission())){
            return new ArrayList<>();
        }
        if (args.length == 1){
            return Arrays.asList("set", "remove");
        }
        if (args.length == 2){
            if (args[0].equalsIgnoreCase("set")){
                if (getPlugin().getConfig().getConfigurationSection("objectives") == null){
                    return new ArrayList<>();
                }
                List<String> cmds = StringUtil.copyPartialMatches(args[1], getPlugin().getConfig().getConfigurationSection("objectives").getKeys(false), new ArrayList<>());
                cmds = cmds.stream().map(s-> "{key}"+s).toList();
                return cmds;
            }
        }
        return new ArrayList<>();
    }
}
