package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class XpMultiplierHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        if (!(event instanceof PlayerXpEvent.PickupXp pickup)) return;

        ExperienceOrb orb = pickup.getOrb();
        if (orb == null) return;

        float multiplier = effect.getScaledFloat("multiplier", "multiplier_per_tier", tier, 1f);
        if (multiplier <= 0f) return;

        orb.value = Math.round(orb.getValue() * multiplier);
    }
}
