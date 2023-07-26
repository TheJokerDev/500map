package me.j0keer.fhmap.commands;

import lombok.Getter;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.CMD;
import me.j0keer.fhmap.type.DataPlayer;
import me.j0keer.fhmap.utils.visual.Animation;
import me.j0keer.fhmap.utils.visual.Colors;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class VanishCommand extends CMD implements Listener {
    private BossBar bossBar;
    private BukkitTask bossBarTask;

    public VanishCommand(Main plugin){
        super(plugin);

        getPlugin().listener(this);

        bossBar = getPlugin().getServer().createBossBar(" ", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setColor(BarColor.PURPLE);
        bossBar.setStyle(BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1);
        bossBar.removeAll();

        bossBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                bossBar.setTitle(Animation.wave("ESTÁS EN MODO OCULTO", Colors.GREEN, Colors.AQUA));
            }
        }.runTaskTimerAsynchronously(getPlugin(), 0, 0L);
    }

    @Override
    public String getName() {
        return "vanish";
    }

    @Override
    public String getDescription() {
        return "Ajustes de visibilidad para staff de mapa.";
    }

    @Override
    public String getPermission() {
        return "map.staff";
    }

    @Override
    public String getPermissionError() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("v", "invisible");
    }

    @Override
    public boolean isTabComplete() {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!check(sender)){
            sender.sendMessage("{prefix}No tienes permisos para ejecutar este comando.");
            return true;
        }
        if (!(sender instanceof Player p)){
            getPlugin().getUtils().sendMSG(sender, "{prefix}Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }
        if (args.length == 0){
            DataPlayer j = getPlugin().getDataManager().getDataPlayer(p);
            j.setVanished(!j.isVanished());
            String msg;
            if (j.isVanished()){
                msg = "{prefix}Has &aentrado&7 en modo &eoculto&7.";
                bossBar.addPlayer(p);
            } else {
                msg = "{prefix}Has &cdejado&7 de estar &eoculto&7.";
                bossBar.removePlayer(p);
            }
            getPlugin().getUtils().sendMSG(sender, msg);
            return true;
        }
        if (args.length == 1){
            String nick = args[0];
            if (nick.equals("silent")){
                DataPlayer j = getPlugin().getDataManager().getDataPlayer(p);
                j.setVanished(!j.isVanished());
                if (j.isVanished()){
                    bossBar.addPlayer(p);
                } else {
                    bossBar.removePlayer(p);
                }
                return true;
            }
            Player target = getPlugin().getServer().getPlayer(nick);
            if (target == null){
                getPlugin().getUtils().sendMSG(sender, "{prefix}El jugador &e" + nick + " &7no está conectado.");
                return true;
            }
            DataPlayer j = getPlugin().getDataManager().getDataPlayer(target);
            j.setVanished(!j.isVanished());
            String msg;
            String msg2;
            if (j.isVanished()){
                msg = "{prefix}Has &aentrado&7 en modo &eoculto&7.";
                msg2 = "{prefix}El jugador &e" + target.getName() + " &7ha &aentrado&7 en modo &eoculto&7.";
                bossBar.addPlayer(target);
            } else {
                msg = "{prefix}Has &cdejado&7 de estar &eoculto&7.";
                msg2 = "{prefix}El jugador &e" + target.getName() + " &7ha &cdejado&7 de estar &eoculto&7.";
                bossBar.removePlayer(target);
            }
            getPlugin().getUtils().sendMSG(target, msg);
            getPlugin().getUtils().sendMSG(sender, msg2);
            return true;
        }
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        DataPlayer j = getPlugin().getDataManager().getDataPlayer(p);
        if (j.isVanished()){
            j.setVanished(false);
            bossBar.removePlayer(p);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("map.autovanish")){
            p.chat("/500map:vanish");
            return;
        }
        if (!p.hasPermission("map.staff.see")){
            for (Player t : getPlugin().getServer().getOnlinePlayers()){
                if (p==t)continue;
                DataPlayer j = getPlugin().getDataManager().getDataPlayer(t);
                if (j.isVanished()){
                    p.hidePlayer(getPlugin(), t);
                } else {
                    if (!p.canSee(t)){
                        p.showPlayer(getPlugin(), t);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!check(sender)){
            return new ArrayList<>();
        }

        if (args.length == 1){
            return StringUtil.copyPartialMatches(args[0], getPlugin().getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
