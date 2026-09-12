package me.scarletleaf1000.slagtraits.recipe.smithing;

import dev.lopyluna.slag.content.items.dynamic_part.IDynamicPart;
import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import me.scarletleaf1000.slagtraits.integration.EquipmentClassifier;
import me.scarletleaf1000.slagtraits.item.ModItems;
import me.scarletleaf1000.slagtraits.traits.EquipmentType;
import me.scarletleaf1000.slagtraits.traits.data.TraitDataManager;
import net.minecraft.world.item.ItemStack;

public final class SmithingRules {
    private SmithingRules() {}

    public static boolean isModularTool(ItemStack stack) {
        return stack.getItem() instanceof IModularItem
                && EquipmentClassifier.getEquipmentType(stack) == EquipmentType.TOOL;
    }

    public static boolean isReplacementPart(ItemStack stack) {
        return stack.getItem() instanceof IDynamicPart;
    }

    public static boolean sameSegment(ItemStack first, ItemStack second) {
        if (!(first.getItem() instanceof IDynamicPart part1)) return false;
        if (!(second.getItem() instanceof IDynamicPart part2)) return false;
        return part1.getPartSegment(first).equals(part2.getPartSegment(second));
    }

    public static boolean isModifierTemplate(ItemStack stack) {
        return stack.is(ModItems.MODIFIER_UPGRADE_SMITHING_TEMPLATE);
    }

    public static boolean isModifierMaterial(ItemStack stack) {
        return (TraitDataManager.findModifierForItem(stack) != null);
    }
}
