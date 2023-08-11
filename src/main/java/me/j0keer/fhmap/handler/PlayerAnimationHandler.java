package me.j0keer.fhmap.handler;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.fhmap.enums.Direction;
import me.j0keer.fhmap.type.DataPlayer;
import org.bukkit.entity.Player;

@Getter @Setter
public class PlayerAnimationHandler {
    private Direction direction = Direction.RIGHT;
    private double xSpeed = 0.0;
    private double ySpeed = 0.0;
    private double zSpeed = 0.0;
    private boolean isPunching = false;

    private boolean pause = false;
    public void updateAnimation(DataPlayer player) {
        minecraftAnimationHandler(player);
        isPunching = false;
    }

    private void minecraftAnimationHandler(DataPlayer dp) {
        boolean small = dp.isSmall();
        Player player = dp.getPlayer();
        if (pause) return;
        if (small){
            if (isPunching) {
                AnimationHandler.changeAnimationLeftRight(player, direction, 38, 37);
            } else if (player.isSneaking()) {
                AnimationHandler.changeAnimationLeftRight(player, direction, 40, 39);
            } else if (ySpeed > 0.0) {
                AnimationHandler.changeAnimationLeftRight(player, direction, 34, 33);
            } else if (ySpeed < 0.0) {
                AnimationHandler.changeAnimationLeftRight(player, direction, 36, 35);
            } else if (Math.abs(xSpeed) > 0.1) {
                AnimationHandler.changeAnimationLeftRight(player, direction, 32, 31);
            } else {
                AnimationHandler.changeAnimationLeftRight(player, direction, 30, 29);
            }
            return;
        }
        if (isPunching) {
            AnimationHandler.changeAnimationLeftRight(player, direction, 20, 19);
        } else if (player.isSneaking()) {
            AnimationHandler.changeAnimationLeftRight(player, direction, 22, 21);
        } else if (ySpeed > 0.0) {
            AnimationHandler.changeAnimationLeftRight(player, direction, 16, 15);
        } else if (ySpeed < 0.0) {
            AnimationHandler.changeAnimationLeftRight(player, direction, 18, 17);
        } else if (Math.abs(xSpeed) > 0.1) {
            AnimationHandler.changeAnimationLeftRight(player, direction, 14, 13);
        } else {
            AnimationHandler.changeAnimationLeftRight(player, direction, 12, 11);
        }
    }
}
