package me.j0keer.fhmap.enums;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum GameSound {
    BRICKS_BREAK("bricks-break"),
    DEATH("death"),
    PIPE_TELEPORT("pipe-teleport"),
    JUMP("jump"),
    BLOCK_BONUS("block-bonus"),
    BLOCK_COIN("block-coin"),
    ;

    private final String sound;

    GameSound(String sound){
        this.sound = "500map:"+sound;
    }

    public void play(Location loc){
        loc.getWorld().playSound(loc, sound, 0.5f, 1);
    }

    public void play(Location loc, float volume, float pitch){
        loc.getWorld().playSound(loc, sound, volume, pitch);
    }

    public void play(Player p){
        play(p.getLocation());
    }

    public void play(Player p, float volume, float pitch){
        play(p.getLocation(), volume, pitch);
    }
}
