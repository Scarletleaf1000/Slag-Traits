package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

/**
 * Negates or reduces damage from specific damage types.
 * Intended for use with the "on_incoming_damage" event.
 *
 * Params:
 *   types (required) - string or list of damage type ids or tags ("#minecraft:is_fire")
 *   reduce / reduce_per_tier - fraction of damage to remove (default 1.0 = full negate)
 */
public class NegateDamageTypeHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        if (!(event instanceof LivingIncomingDamageEvent incoming)) return;
        if (incoming.getEntity() != holder) return;

        DamageSource source = incoming.getSource();
        if (source == null) return;

        List<String> types = effect.getStringList("types");
        if (types.isEmpty()) return;

        boolean matched = false;
        for (String t : types) {
            if (t.startsWith("#")) {
                ResourceLocation tag = ResourceLocation.tryParse(t.substring(1));
                if (tag != null && source.is(TagKey.create(Registries.DAMAGE_TYPE, tag))) {
                    matched = true;
                    break;
                }
            } else {
                ResourceLocation rl = ResourceLocation.tryParse(t);
                if (rl != null && source.typeHolder().unwrapKey().map(k -> k.location().equals(rl)).orElse(false)) {
                    matched = true;
                    break;
                }
            }
        }
        if (!matched) return;

        float reduce = effect.getScaledFloat("reduce", "reduce_per_tier", tier, 1f);
        if (reduce >= 1f) {
            incoming.setCanceled(true);
        } else if (reduce > 0f) {
            incoming.setAmount(Math.max(0f, incoming.getAmount() * (1f - reduce)));
        }
    }
}
