package me.j0keer.fhmap.managers;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.utils.FriendlyByteBuf;
import org.bukkit.entity.Player;

public class CameraManager {
    Main plugin;

    public CameraManager(Main plugin){
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "onlyup:camera_lock");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "onlyup:perspective_change");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "onlyup:axis_lock");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "onlyup:change_size");
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "onlyup:shader_effect");
    }

    public void lockCamera(Player player){
       changeLockedCamera(player, true);
    }

    public void unlockCamera(Player player){
        changeLockedCamera(player, false);
    }

    public void lockMovementAxis(Player player, char axis){
        changeLockedMovementAxis(player, axis, true);
    }

    public void unlockMovementAxis(Player player, char axis){
        changeLockedMovementAxis(player, axis, false);
    }

    private void changeLockedMovementAxis(Player player, char axis, boolean lock){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeChar(axis);
            buf.writeBoolean(lock);
            player.sendPluginMessage(plugin, "onlyup:axis_lock", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }

    public void changeCamera(Player player, Perspective perspective){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeUtf(perspective.name());
            player.sendPluginMessage(plugin, "onlyup:perspective_change", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }

    private void changeLockedCamera(Player player, boolean lock){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeBoolean(lock);
            player.sendPluginMessage(plugin, "onlyup:camera_lock", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }

    public void changeSize(Player player, boolean small){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeBoolean(small);
            player.sendPluginMessage(plugin, "onlyup:change_size", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }

    public void sendShaderEffect(Player player, long time, float size, int id){
        FriendlyByteBuf buf = new FriendlyByteBuf();
        try {
            buf.writeInt(id);
            buf.writeLong(time);
            buf.writeFloat(size);
            player.sendPluginMessage(plugin, "onlyup:shader_effect", buf.array());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            buf.clear();
        }
    }

    public enum Perspective {
        FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT;
    }
}
