package me.j0keer.fhmap.handler;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.fhmap.enums.Direction;
import org.bukkit.entity.Player;

@Getter @Setter
public class PlayerAnimationHandler {
    private Direction direction = Direction.RIGHT;
    private double xSpeed = 0.0;
    private double ySpeed = 0.0;
    private double zSpeed = 0.0;
    private boolean isPunching = false;

    private boolean pause = false;
    public void updateAnimation(Player player) {
        minecraftAnimationHandler(player);
        isPunching = false;
    }

    private void minecraftAnimationHandler(Player player) {
        if (pause) return;
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
