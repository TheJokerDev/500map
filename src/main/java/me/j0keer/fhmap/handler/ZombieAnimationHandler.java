package me.j0keer.fhmap.handler;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.fhmap.enums.Direction;
import org.bukkit.entity.LivingEntity;

@Setter
public class ZombieAnimationHandler {
    @Getter private Direction direction = Direction.RIGHT;
    private double xSpeed = 0.0;

    public void updateAnimation(LivingEntity entity) {
        if (Math.abs(xSpeed) > 0.1) {
            AnimationHandler.changeAnimationLeftRight(entity, direction, 26, 25);
        } else {
            AnimationHandler.changeAnimationLeftRight(entity, direction, 28, 27);
        }

    }
}
