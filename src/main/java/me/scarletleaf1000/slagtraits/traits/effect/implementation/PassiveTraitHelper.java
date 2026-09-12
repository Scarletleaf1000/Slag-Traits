package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.ActiveTrait;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.traits.Trigger;
import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import me.scarletleaf1000.slagtraits.traits.resolver.TraitResolver;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class PassiveTraitHelper {
    private static final ResourceLocation FIRE_RESISTANT = ResourceLocation.fromNamespaceAndPath("slagtraits", "fire_resistant");
    private static final ResourceLocation STURDY = ResourceLocation.fromNamespaceAndPath("slagtraits", "sturdy");

    /**
     * Returns true/false to override fire immunity for Slag items, or null to let vanilla decide.
     */
    public static Boolean isFireResistant(ItemStack stack) {
        Item item = stack.getItem();
        boolean isSlagItem = item instanceof dev.lopyluna.slag.content.items.dynamic_part.IModularItem
                || item instanceof dev.lopyluna.slag.content.items.dynamic_part.IDynamicPart;

        if (!isSlagItem) return null;

        for (ActiveTrait active : TraitResolver.getActiveTraits(stack)) {
            if (active.getId().equals(FIRE_RESISTANT)) return true;
        }

        return false;
    }

    public static boolean shouldPreventDurabilityDamage(ItemStack stack) {
        for (ActiveTrait active : TraitResolver.getActiveTraits(stack)) {
            if (active.getId().equals(STURDY)) {
                return rollSturdy(active);
            }
        }
        return false;
    }

    private static boolean rollSturdy(ActiveTrait active) {
        float chancePerTier = 0.05f;
        Trait trait = active.trait();
        if (trait != null && trait.getTriggers() != null) {
            for (Trigger trigger : trait.getTriggers()) {
                TraitEffect effect = trigger.getEffect();
                if (effect != null) {
                    chancePerTier = effect.getFloat("chance_per_tier", chancePerTier);
                    break;
                }
            }
        }

        float chance = active.tier() * chancePerTier;
        return ThreadLocalRandom.current().nextFloat() < chance;
    }
}
