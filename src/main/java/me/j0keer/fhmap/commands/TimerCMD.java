package me.j0keer.fhmap.commands;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.CMD;
import org.bukkit.ChatColor;
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimerCMD extends CMD {
    public TimerCMD(Main plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "timer";
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
            sendMSG(sender, "&cNo tienes permisos para ejecutar este comando.");
            return true;
        }
        if (args.length > 0){
            if (args[0].equalsIgnoreCase("stop")){
                stopClock();
                clock(5, "");
                return true;
            }
            if (args[0].equalsIgnoreCase("forcestop")){
                clock_secs=-1;
                return true;
            }
            if (args.length >= 2){
                String arg = args[0];
                String var2 = args[1];

                String text = "";
                if (args.length > 2){
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }
                    text = builder.toString();
                }
                if (arg.equalsIgnoreCase("start")) {
                    int time;
                    try {
                        time = Integer.parseInt(var2);
                    } catch (NumberFormatException e) {
                        ScriptEngineManager manager = new ScriptEngineManager();
                        ScriptEngine sp = manager.getEngineByName("JavaScript");
                        try {
                            time = (int) sp.eval(var2);
                        } catch (ScriptException ex) {
                            sender.sendMessage("§c¡Eso no es un número!");
                            return true;
                        }
                    }
                    clock(time, text);
                    sender.sendMessage("§aTimer iniciado de: §r"+getFormattedTimer(time));
                    return true;
                }
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(getPermission())){
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        if (args.length == 1){
            StringUtil.copyPartialMatches(args[0], Arrays.asList("start", "stop", "forcestop"), list);
        }
        return list;
    }

    int clock_secs = 0;
    public BukkitTask clock;

    public BossBar bar;

    public void stopClock(){
        if (clock == null){
            return;
        }
        clock.cancel();
    }


    public BossBar getBar() {
        if (bar == null){
            bar = getPlugin().getServer().createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
        }
        return bar;
    }

    public void clock(int secs, String msg){
        getBar();
        getBar().setProgress(1);
        clock_secs = secs;
        if (clock != null){
            clock.cancel();
        }

        boolean prefix = true;
        if (msg != null && !msg.equals("")){
            if (msg.contains("{prefix}")){
                msg = msg.replace("{prefix}", "");
            } else if (msg.contains("{suffix}")){
                msg = msg.replace("{suffix}", "");
                prefix = false;
            }
            msg = ChatColor.translateAlternateColorCodes('&', msg);
        }
        String finalMsg = msg;
        boolean finalPrefix = prefix;
        clock = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player online : getPlugin().getServer().getOnlinePlayers()) {
                    if (getBar().getPlayers().contains(online)){
                        continue;
                    }
                    getBar().addPlayer(online);
                }
                if (finalMsg != null && !finalMsg.equals("")) {
                    if (finalPrefix){
                        getBar().setTitle( finalMsg+secToTime(clock_secs));
                    } else {
                        getBar().setTitle(secToTime(clock_secs)+finalMsg);
                    }
                } else {
                    getBar().setTitle(secToTime(clock_secs));
                }
                double progress = 0;
                if (clock_secs > 0){
                    progress = (double) clock_secs / (double) secs;
                }
                getBar().setProgress(progress);
                if (clock_secs < 0){
                    cancel();
                    getBar().setTitle("");
                    getBar().removeAll();
                }
                clock_secs--;
            }
        }.runTaskTimer(getPlugin(), 0L, 20L);
    }

    public String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if( hours >= 24) {
                int days = hours / 24;
                return String.format("%d days %02d:%02d:%02d", days,hours%24, minutes, seconds);
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFormattedTimer(int var4){
        int var5 = var4 % 86400 % 3600 % 60;
        int var6 = var4 % 86400 % 3600 / 60;
        int var7 = var4 % 86400 / 3600;
        int var8 = var4 / 86400;
        boolean var9 = true;
        boolean var10 = true;
        boolean var11 = true;
        boolean var12 = true;
        if (var5 == 1) {
            var9 = false;
        }

        if (var6 == 1) {
            var10 = false;
        }

        if (var7 == 1) {
            var11 = false;
        }

        if (var8 == 1) {
            var12 = false;
        }

        String var13 = var9 ? "§f%s §asegs." : "§f%s §aseg.";
        String var14 = String.format(var13, var5);
        String var15 = var10 ? "§f%s §amins, " : "§f%s §amin, ";
        String var16 = String.format(var15, var6);
        String var17 = var11 ? "§f%s §ah, " : "§f%s §ahrs, ";
        String var18 = String.format(var17, var7);
        String var19 = var12 ? "§f%s §ads, " : "§f%s §ad, ";
        String var20 = String.format(var19, var8);
        if (var8 == 0) {
            var20 = "";
        }

        if (var7 == 0) {
            var18 = "";
        }

        if (var6 == 0) {
            var16 = "";
        }

        return var20+var18+var16+var14;
    }
}
