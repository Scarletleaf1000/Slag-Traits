package me.scarletleaf1000.slagtraits.traits.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TraitEffectRegistry {
    private static final Map<ResourceLocation, IEffectHandler> HANDLERS = new HashMap<>();

    public static void register(ResourceLocation type, IEffectHandler handler) {
        HANDLERS.put(type, handler);
    }

    public static void apply(TraitEffect effect, LivingEntity holder, ItemStack tool, Object event, int tier) {
        IEffectHandler handler = HANDLERS.get(effect.getType());
        if (handler != null) {
            handler.apply(holder, tool, effect, event, tier);
        }
    }
}
