package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import me.scarletleaf1000.slagtraits.traits.effect.TraitEffectRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RepairItemHandler {
    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        int amount = effect.getScaledInt("amount", "amount_per_tier", tier, 1);
        if (!tool.isDamageableItem()) return;
        if (tool.isDamaged()) {
            int newDamage = Math.max(0, tool.getDamageValue() - amount);
            tool.setDamageValue(newDamage);
        }
    }
}
