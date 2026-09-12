package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ScaleStatHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        String stat = effect.getString("stat");
        String mode = effect.getString("mode");
        float perTier = effect.getFloat("per_tier", 0f);
        float base = effect.getFloat("base", 0f);

        float scale = computeScale(mode, holder, tool, effect);
        float bonus = base + tier * perTier * scale;

        if (bonus == 0f || scale == 0f) return;

        if ("break_speed".equals(stat) && event instanceof PlayerEvent.BreakSpeed breakSpeed) {
            float newSpeed = breakSpeed.getNewSpeed() * (1f + bonus);
            breakSpeed.setNewSpeed(Math.max(0.0f, newSpeed));
        } else if ("attack_damage".equals(stat) && event instanceof LivingDamageEvent.Pre damagePre) {
            // Only apply when the holder is the one dealing damage
            if (damagePre.getSource() == null || damagePre.getSource().getEntity() != holder) return;
            float newDamage = damagePre.getNewDamage() * (1f + bonus);
            damagePre.setNewDamage(Math.max(0.0f, newDamage));
        }
    }

    private static float computeScale(String mode, LivingEntity holder, ItemStack tool, TraitEffect effect) {
        if (mode == null || holder == null) return 0f;

        return switch (mode) {
            case "y_level" -> {
                int minY = effect.getInt("min_y", -64);
                int maxY = effect.getInt("max_y", 128);
                int y = holder.blockPosition().getY();
                float t = Mth.inverseLerp(y, minY, maxY);
                // lower y -> higher scale, clamped
                yield Mth.clamp(1f - t, 0f, 1f);
            }
            case "durability_low" -> {
                if (tool == null || !tool.isDamageableItem()) yield 0f;
                float t = (float) tool.getDamageValue() / tool.getMaxDamage();
                yield Mth.clamp(t, 0f, 1f);
            }
            case "durability_high" -> {
                if (tool == null || !tool.isDamageableItem()) yield 0f;
                float t = (float) tool.getDamageValue() / tool.getMaxDamage();
                yield Mth.clamp(1f - t, 0f, 1f);
            }
            default -> 0f;
        };
    }
}
