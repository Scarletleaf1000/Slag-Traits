package me.scarletleaf1000.slagtraits.content.traits;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface IEffectHandler {
    void apply(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event);
}
