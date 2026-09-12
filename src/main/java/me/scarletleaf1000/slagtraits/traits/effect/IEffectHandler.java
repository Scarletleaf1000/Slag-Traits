package me.scarletleaf1000.slagtraits.traits.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface IEffectHandler {
    void apply(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier);
}
