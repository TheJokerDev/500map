package me.j0keer.fhmap.type;

import me.j0keer.fhmap.Main;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;

public class GameMusic {
    Main plugin;
    private Music music;
    private int duration;
    private int second = 0;

    public GameMusic(Main plugin, Music music, int duration){
        this.plugin = plugin;
        this.music = music;
        this.duration = duration;
    }

    public void play(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), "500map:"+music.name().toLowerCase(), SoundCategory.AMBIENT, 0.2f, 1f);
        });
    }

    public void stop(){
        Bukkit.getOnlinePlayers().forEach(player ->{
            player.stopSound("500map:"+music.name().toLowerCase(), SoundCategory.AMBIENT);
        });
    }

    public enum Music{
        OVERWORLD,
        NETHER,
        BOSS
    }
}
