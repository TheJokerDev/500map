package me.j0keer.fhmap.handler;

import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.enums.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AnimationHandler {
    private final Main plugin;
    public AnimationHandler(Main plugin) {
        this.plugin = plugin;
    }

    public static void changeAnimation(@NotNull LivingEntity entity, int animationID) {
        ItemStack helmet = Objects.requireNonNull(entity.getEquipment()).getHelmet();
        Material animationMaterial = Material.PHANTOM_MEMBRANE;
        if (helmet == null || helmet.getType() != animationMaterial) {
            helmet = new ItemStack(animationMaterial);
        }

        ItemMeta itemMeta = helmet.hasItemMeta() ? helmet.getItemMeta() : Bukkit.getItemFactory().getItemMeta(animationMaterial);
        itemMeta.setCustomModelData(animationID);
        helmet.setItemMeta(itemMeta);
        entity.getEquipment().setHelmet(helmet);
    }

    public static void changeAnimationLeftRight(@NotNull LivingEntity entity, Direction direction, int leftAnimationID, int rightAnimationID) {
        if (direction == Direction.RIGHT) {
            changeAnimation(entity, rightAnimationID);
        } else {
            changeAnimation(entity, leftAnimationID);
        }

    }
}
