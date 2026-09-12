package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Pulls nearby item entities (and optionally XP orbs) toward the holder.
 * Intended for use with the "on_tick" event and "is_equipped" condition.
 *
 * Params:
 *   radius / radius_per_tier (default 4)
 *   speed / speed_per_tier (default 0.05)
 *   include_xp (default false)
 */
public class MagnetHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        float radius = effect.getScaledFloat("radius", "radius_per_tier", tier, 4f);
        float speed = effect.getScaledFloat("speed", "speed_per_tier", tier, 0.05f);
        if (radius <= 0f || speed <= 0f) return;

        AABB box = holder.getBoundingBox().inflate(radius);
        Vec3 center = holder.position().add(0, holder.getBbHeight() / 2.0, 0);

        for (ItemEntity item : holder.level().getEntitiesOfClass(ItemEntity.class, box)) {
            pull(item, center, speed);
        }

        if (effect.getBoolean("include_xp", false)) {
            for (ExperienceOrb orb : holder.level().getEntitiesOfClass(ExperienceOrb.class, box)) {
                pull(orb, center, speed);
            }
        }
    }

    private static void pull(Entity entity, Vec3 target, float speed) {
        Vec3 motion = target.subtract(entity.position());
        double dist = motion.length();
        if (dist < 0.5) return;
        motion = motion.normalize().scale(speed);
        entity.setDeltaMovement(entity.getDeltaMovement().add(motion.scale(0.5)));
    }
}
