package me.j0keer.fhmap;

import com.sk89q.worldedit.WorldEdit;
import lombok.Getter;
import me.j0keer.fhmap.config.ConfigUtil;
import me.j0keer.fhmap.enums.Modules;
import me.j0keer.fhmap.game.Game;
import me.j0keer.fhmap.listeners.CinematicListeners;
import me.j0keer.fhmap.listeners.GeneralListeners;
import me.j0keer.fhmap.listeners.InteractListeners;
import me.j0keer.fhmap.managers.*;
import me.j0keer.fhmap.menus.ShopMenu;
import me.j0keer.fhmap.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public final class Main extends JavaPlugin {
    private static Main plugin;
    private PluginManager pluginManager;

    //Class declaration
    private Utils utils;
    private CMDManager cmdManager;
    private ItemsManager itemsManager;
    private MenusManager menusManager;
    private DataManager dataManager;
    private ConfigUtil configUtil;
    private WorldEdit worldEdit;

    private TasksManager tasksManager;
    private CameraManager cameraManager;

    private Game game;

    @Override
    public void onEnable() {
        double ms = System.currentTimeMillis();
        plugin = this;
        saveDefaultConfig();
        pluginManager = getServer().getPluginManager();
        utils = new Utils(this);
        console("{prefix}&fInitializing plugin...", "");

        console(" &e» &fChecking dependencies...");
        if (!checkDependencies("PlaceholderAPI")){
            console("{prefix}&cPlease, check the message above and install all dependencies to work.");
            getPluginManager().disablePlugin(this);
            return;
        }
        if (!checkDependencies("FastAsyncWorldEdit")){
            console("{prefix}&cPlease, check the message above and install all dependencies to work.");
            getPluginManager().disablePlugin(this);
            return;
        }
        worldEdit = WorldEdit.getInstance();
        console(" &e» &aDependencies correctly detected.", "");

        configUtil = new ConfigUtil(this);

        console(" &e» &fRegistering managers");
        cmdManager = new CMDManager(this);
        itemsManager = new ItemsManager(this);
        menusManager = new MenusManager(this);
        dataManager = new DataManager(this);
        cameraManager = new CameraManager(this);

        Modules.loadValues(this);
        listener(new InteractListeners(this), new GeneralListeners(this), new CinematicListeners(this));

        game = new Game(this);
        new ShopMenu(this);

        console(" &e» &aManagers correctly loaded.", "");


        ms = System.currentTimeMillis()-ms;
        console("{prefix}&fPlugin loaded successfully in &e"+ms+"&fms.");

        for (Player p : getServer().getOnlinePlayers()){
            getDataManager().getDataPlayer(p);
            if (p.hasPermission("map.staff")){
                p.chat("/500map:vanish silent");
            }
        }

        tasksManager = new TasksManager(this);
    }

    private boolean checkDependencies(String... dependencies){
        boolean bol = true;
        for (String pl : dependencies) {
            if (getPluginManager().isPluginEnabled(pl)){
                console("   &b→ &fDependency detected: &a"+pl+"&f.");
            } else {
                console("   &b→ &fDependency not detected: &a"+pl+"&f. Please, install to init.");
                bol = false;
            }
        }
        return bol;
    }

    public void listener(Listener... listeners){
        Arrays.stream(listeners).forEach(listener -> {
            getPluginManager().registerEvents(listener, this);
            console("   &b→ &fListener class registered: &a"+listener.getClass().getSimpleName()+"&f.");
        });
    }

    public void console(String... out){
        Arrays.stream(out).forEach(s->utils.sendMSG(getServer().getConsoleSender(), s));
    }

    public void debug(String msg){
        if (!getConfig().getBoolean("settings.debug")){
            return;
        }
        utils.sendMSG(getServer().getConsoleSender(), "{prefix}&e&lDEBUG: &7"+msg);
    }

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        double ms = System.currentTimeMillis();

        if (getCmdManager()!=null) {
            getCmdManager().getVanishCommand().getBossBar().removeAll();
            getCmdManager().getVanishCommand().getBossBarTask().cancel();
            getCmdManager().getObjectiveCMD().getBar().removeAll();
        }



        getDataManager().getPlayers().forEach((uuid, dataPlayer) -> {
            if (dataPlayer.isInGame()){
                dataPlayer.setInGame(false);
                if (dataPlayer.getDeadStand() != null && !dataPlayer.getDeadStand().isDead()){
                    dataPlayer.getDeadStand().remove();
                }
            }
        });

        if(plugin.getGame() != null)
            getGame().reset();

        ms = System.currentTimeMillis()-ms;
        console("{prefix}&fPlugin disabled successfully in &e"+ms+"&fms.");
    }
}
