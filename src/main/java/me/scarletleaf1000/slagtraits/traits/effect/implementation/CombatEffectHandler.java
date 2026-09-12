package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles combat-related effects for both attacker and victim.
 *
 * Attacker-side params (on_attack_entity_pre / on_attack_entity):
 *   bonus_damage, bonus_damage_per_tier, damage_multiplier, damage_multiplier_per_tier,
 *   knockback, knockback_per_tier, lifesteal, lifesteal_per_tier,
 *   ignite_ticks, ignite_ticks_per_tier,
 *   mob_effect_target / mob_effect_target_duration / mob_effect_target_amplifier (+_per_tier),
 *   mob_effect_self / mob_effect_self_duration / mob_effect_self_amplifier (+_per_tier)
 *
 * Victim-side params (on_incoming_damage / on_hurt_pre):
 *   damage_reduction, damage_reduction_per_tier, evade_chance, evade_chance_per_tier
 */
public class CombatEffectHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        if (event instanceof LivingIncomingDamageEvent incoming) {
            if (incoming.getEntity() == holder) {
                handleVictimIncoming(holder, effect, incoming, tier);
            }
        } else if (event instanceof LivingDamageEvent.Pre pre) {
            if (pre.getSource() != null && pre.getSource().getEntity() == holder) {
                handleAttackPre(holder, effect, pre, tier);
            } else if (pre.getEntity() == holder) {
                handleVictimPre(holder, effect, pre, tier);
            }
        } else if (event instanceof LivingDamageEvent.Post post) {
            if (post.getSource() != null && post.getSource().getEntity() == holder) {
                handleAttackPost(holder, effect, post, tier);
            }
        }
    }

    private static void handleAttackPre(LivingEntity holder, TraitEffect effect, LivingDamageEvent.Pre pre, int tier) {
        float bonus = effect.getScaledFloat("bonus_damage", "bonus_damage_per_tier", tier, 0f);
        float mult = effect.getScaledFloat("damage_multiplier", "damage_multiplier_per_tier", tier, 0f);

        float damage = pre.getNewDamage() + bonus;
        if (mult != 0f) damage *= (1f + mult);
        pre.setNewDamage(Math.max(0f, damage));
    }

    private static void handleAttackPost(LivingEntity holder, TraitEffect effect, LivingDamageEvent.Post post, int tier) {
        LivingEntity target = post.getEntity();
        float dealt = post.getNewDamage();

        float lifesteal = effect.getScaledFloat("lifesteal", "lifesteal_per_tier", tier, 0f);
        if (lifesteal > 0f && dealt > 0f) {
            holder.heal(dealt * lifesteal);
        }

        float knockback = effect.getScaledFloat("knockback", "knockback_per_tier", tier, 0f);
        if (knockback > 0f) {
            double dx = -Mth.sin(holder.getYRot() * ((float) Math.PI / 180f));
            double dz = Mth.cos(holder.getYRot() * ((float) Math.PI / 180f));
            target.knockback(knockback, dx, dz);
        }

        int ignite = effect.getScaledInt("ignite_ticks", "ignite_ticks_per_tier", tier, 0);
        if (ignite > 0) {
            target.igniteForTicks(ignite);
        }

        applyMobEffect(effect, "mob_effect_target", target, tier);
        applyMobEffect(effect, "mob_effect_self", holder, tier);
    }

    private static void handleVictimIncoming(LivingEntity holder, TraitEffect effect, LivingIncomingDamageEvent incoming, int tier) {
        float evade = effect.getScaledFloat("evade_chance", "evade_chance_per_tier", tier, 0f);
        if (evade > 0f && ThreadLocalRandom.current().nextFloat() < evade) {
            incoming.setCanceled(true);
            return;
        }

        float reduction = effect.getScaledFloat("damage_reduction", "damage_reduction_per_tier", tier, 0f);
        if (reduction > 0f) {
            incoming.setAmount(Math.max(0f, incoming.getAmount() * (1f - reduction)));
        }
    }

    private static void handleVictimPre(LivingEntity holder, TraitEffect effect, LivingDamageEvent.Pre pre, int tier) {
        float reduction = effect.getScaledFloat("damage_reduction", "damage_reduction_per_tier", tier, 0f);
        if (reduction > 0f) {
            pre.setNewDamage(Math.max(0f, pre.getNewDamage() * (1f - reduction)));
        }
    }

    private static void applyMobEffect(TraitEffect effect, String key, LivingEntity target, int tier) {
        String id = effect.getString(key);
        if (id == null || target == null) return;
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return;
        var mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(rl);
        if (mobEffect.isEmpty()) return;

        int duration = effect.getScaledInt(key + "_duration", key + "_duration_per_tier", tier, 60);
        int amplifier = effect.getScaledInt(key + "_amplifier", key + "_amplifier_per_tier", tier, 0);
        if (duration <= 0) return;

        target.addEffect(new MobEffectInstance(mobEffect.get(), duration, amplifier));
    }
}
