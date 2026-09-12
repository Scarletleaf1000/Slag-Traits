package me.scarletleaf1000.slagtraits.integration;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.types.ModularType;
import me.scarletleaf1000.slagtraits.traits.EquipmentType;
import net.minecraft.world.item.*;

public class EquipmentClassifier {
    public static EquipmentType getEquipmentType(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof IModularItem modularItem) {
            ModularType modularType = modularItem.getModularTypeFromStack(stack);
            if (modularType == null || modularType.actions == null) {
                return EquipmentType.NONE;
            }

            boolean hasArmor = false;
            boolean hasTool = false;

            for (String action : modularType.actions) {
                switch (action) {
                    case "helmet", "chestplate", "leggings", "boots",
                         "glide", "head", "body", "gloves", "belt", "foot",
                         "ring", "bracelet", "necklace", "charm" -> hasArmor = true;
                    case "pickaxe", "axe", "shovel", "hoe",
                         "sword", "mace", "shield", "bow", "crossbow",
                         "trident", "spear", "throwing", "arrow",
                         "fishing_rod", "shears", "flint_and_steel",
                         "bucket", "wrench", "hammer", "scythe",
                         "cutting", "vein", "harvest" -> hasTool = true;
                    default -> {}
                }
            }

            if (hasArmor && hasTool) return EquipmentType.BOTH;
            if (hasArmor) return EquipmentType.ARMOR;
            if (hasTool) return EquipmentType.TOOL;
            return EquipmentType.NONE; // fallback for unclassified Slag items
        }

        if (item instanceof ArmorItem) return EquipmentType.ARMOR;
        if (item instanceof DiggerItem || item instanceof SwordItem || item instanceof ShearsItem || item instanceof TridentItem)
            return EquipmentType.TOOL;
        return EquipmentType.NONE;
    }

}
