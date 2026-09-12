package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class ThornsHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        if (!(event instanceof LivingDamageEvent.Post damagePost)) return;

        if (damagePost.getSource() == null || damagePost.getSource().getEntity() == holder) return;

        if (damagePost.getSource().getEntity() instanceof LivingEntity attacker) {
            float damage = effect.getScaledFloat("damage", "damage_per_tier", tier, 1f);
            if (damage <= 0f) return;

            attacker.hurt(holder.level().damageSources().thorns(holder), damage);
        }
    }
}
