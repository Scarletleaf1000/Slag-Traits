package me.scarletleaf1000.slagtraits.content.traits.data;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.EquipmentType;
import me.scarletleaf1000.slagtraits.content.traits.Trait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TraitManager {

    public static List<Trait> getActiveTraits(ItemStack stack) {
        if (stack.isEmpty()) return List.of();

        List<Trait> result = new ArrayList<>();
        Set<ResourceLocation> seen = new HashSet<>();
        EquipmentType type = getEquipmentType(stack);

        for (ResourceLocation material : MaterialAdapter.getMaterials(stack)) {
            for (MaterialTraitData.TraitRef ref : MaterialTraitManager.getTraits(material)) {
                Trait trait = TraitDataManager.get(ref.id());
                if (trait == null) continue;
                if (seen.contains(trait.getId())) continue;

                // Filter by equipment type
                if (!matches(trait.getEquipmentType(), type)) continue;

                result.add(trait);
                seen.add(trait.getId());
            }
        }
        return result;
    }

    private static boolean matches(EquipmentType traitType, EquipmentType stackType) {
        return traitType == EquipmentType.BOTH || stackType == EquipmentType.BOTH || traitType == stackType;
    }

    private static EquipmentType getEquipmentType(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ArmorItem) return EquipmentType.ARMOR;
        if (item instanceof DiggerItem || item instanceof SwordItem || item instanceof ShearsItem || item instanceof TridentItem) return EquipmentType.TOOL;
        return EquipmentType.BOTH; // fallback
    }

    public static boolean test(String condition, LivingEntity holder, ItemStack tool, Object event) {
        return switch (condition) {
            case "always" -> true;
            case "is_damaged" -> tool.isDamaged();
            case "is_sneaking" -> holder.isCrouching();
            case "is_on_ground" -> holder.onGround();
            case "is_in_water" -> holder.isInWater();
            case "is_on_fire" -> holder.isOnFire();
            case "held_in_main_hand" -> holder.getMainHandItem() == tool;
            case "is_day" -> holder.level().isDay();
            case "is_night" -> !holder.level().isDay();
            case "is_full_health" -> holder.getHealth() >= holder.getMaxHealth();
            case "is_below_half_health" -> holder.getHealth() <= holder.getMaxHealth() / 2;
            default -> {
                SlagTraits.LOGGER.warn("Unknown trait condition '{}', treating as true", condition);
                yield true;
            }
        };
    }
}
