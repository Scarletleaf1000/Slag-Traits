package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Applies a mob effect to the holder while the item is held/worn.
 * Intended for use with the "on_tick" event and "is_equipped" condition.
 *
 * Params:
 *   effect (required) - mob effect id, e.g. "minecraft:regeneration"
 *   duration / duration_per_tier (default 60)
 *   amplifier / amplifier_per_tier (default 0)
 *   ambient (default true), visible (default false), show_icon (default false)
 */
public class StatusEffectHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        String id = effect.getString("effect");
        if (id == null) return;
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return;
        var mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(rl);
        if (mobEffect.isEmpty()) return;

        int duration = effect.getScaledInt("duration", "duration_per_tier", tier, 60);
        int amplifier = effect.getScaledInt("amplifier", "amplifier_per_tier", tier, 0);
        if (duration <= 0) return;

        boolean ambient = effect.getBoolean("ambient", true);
        boolean visible = effect.getBoolean("visible", false);
        boolean showIcon = effect.getBoolean("show_icon", false);

        holder.addEffect(new MobEffectInstance(mobEffect.get(), duration, amplifier, ambient, visible, showIcon));
    }
}
