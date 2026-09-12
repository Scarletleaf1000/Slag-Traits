package me.scarletleaf1000.slagtraits.recipe.smithing;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.register.AllDataComponents;
import me.scarletleaf1000.slagtraits.integration.EquipmentClassifier;
import me.scarletleaf1000.slagtraits.item.ModItems;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.traits.data.AppliedModifiers;
import me.scarletleaf1000.slagtraits.traits.data.ModDataComponents;
import me.scarletleaf1000.slagtraits.traits.data.TraitDataManager;
import me.scarletleaf1000.slagtraits.traits.resolver.TraitResolver;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ModifierService {

    public static boolean canApply(ItemStack template, ItemStack tool, ItemStack addition) {
        if (!template.is(ModItems.MODIFIER_UPGRADE_SMITHING_TEMPLATE)) return false;

        if (!(tool.getItem() instanceof IModularItem)) return false;
        if (!tool.has(AllDataComponents.MODULAR_TYPE)) return false;

        var trait = TraitDataManager.findModifierForItem(addition);
        if (trait == null) return false;

        if (!TraitResolver.matches(trait.getEquipmentType(),
                EquipmentClassifier.getEquipmentType(tool))) return false;

        AppliedModifiers modifiers = tool.getOrDefault(ModDataComponents.MODIFIERS, AppliedModifiers.EMPTY);
        if (modifiers.getValue(trait.getId()) >= valueForMaxTier(trait)) return false;

        for (ResourceLocation appliedId : modifiers.traitData().keySet()) {
            if (appliedId.equals(trait.getId())) continue;
            Trait applied = TraitDataManager.get(appliedId);
            if (applied != null && applied.getExclusiveWith().contains(trait.getId().toString())) return false;
            if (trait.getExclusiveWith().contains(appliedId.toString())) return false;
        }

        return true;
    }

    public static ModifierResult apply(ItemStack template, ItemStack tool, ItemStack addition) {
        if (!canApply(template, tool, addition)) return new ModifierResult(tool, 0);
        var newTool = tool.copyWithCount(1);
        AppliedModifiers modifiers = tool.getOrDefault(ModDataComponents.MODIFIERS, AppliedModifiers.EMPTY);
        var trait = TraitDataManager.findModifierForItem(addition);
        int stored = modifiers.getValue(trait.getId());
        int needed = valueForMaxTier(trait) - stored;
        int consumed = Math.min(addition.getCount(), needed);
        newTool.set(ModDataComponents.MODIFIERS, modifiers.withAdded(trait.getId(), consumed));
        return new ModifierResult(newTool, consumed);
    }

    public static int costForTier(Trait trait, int tier) {
        // cost to go from (tier-1) -> tier; tier is 1-based
        return (int) Math.round(trait.getBaseCostPerLevel() * Math.pow(trait.getScalingMultiplier(), tier - 1));
    }

    public static int tierForValue(Trait trait, int storedValue) {
        int tier = 0;
        int remaining = storedValue;
        while (tier < trait.getMaxTier() && remaining >= costForTier(trait, tier + 1)) {
            remaining -= costForTier(trait, tier + 1);
            tier++;
        }
        return tier;
    }

    public static int valueForMaxTier(Trait trait) {
        int total = 0;
        for (int t = 1; t <= trait.getMaxTier(); t++) total += costForTier(trait, t);
        return total;
    }

    public static int valueForTier(Trait trait, int tier) {
        int total = 0;
        for (int t = 1; t <= tier; t++) total += costForTier(trait, t);
        return total;
    }

    public record ModifierResult(ItemStack result, int consumed) {}

}
