package me.scarletleaf1000.slagtraits.traits.resolver;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.recipe.smithing.ModifierService;
import me.scarletleaf1000.slagtraits.traits.ActiveTrait;
import me.scarletleaf1000.slagtraits.traits.EquipmentType;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.integration.MaterialAdapter;
import me.scarletleaf1000.slagtraits.traits.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

import java.util.*;

import static me.scarletleaf1000.slagtraits.integration.EquipmentClassifier.getEquipmentType;

public class TraitResolver {

    public static List<ActiveTrait> getActiveTraits(ItemStack stack) {
        if (stack.isEmpty()) return List.of();

        Map<ResourceLocation, ActiveTrait> activeTraits = new HashMap<>();
        EquipmentType type = getEquipmentType(stack);
        List<ResourceLocation> materials = MaterialAdapter.getMaterials(stack);

        for (ResourceLocation material : materials) {
            List<MaterialTraitData.TraitRef> refs = MaterialTraitManager.getTraits(material);
            for (MaterialTraitData.TraitRef ref : refs) {
                Trait trait = TraitDataManager.get(ref.id());
                if (trait == null) {
                    SlagTraits.LOGGER.warn("[TraitManager] missing trait data for id={} on material={}", ref.id(), material);
                    continue;
                }

                if (!matches(trait.getEquipmentType(), type)) continue;

                int tier = Math.min(ref.tier(), trait.getMaxTier());

                ActiveTrait existing = activeTraits.get(trait.getId());
                if (existing == null || tier > existing.tier()) {
                    activeTraits.put(trait.getId(), new ActiveTrait(trait, tier));
                }
            }
        }

        AppliedModifiers modifiers = stack.getOrDefault(ModDataComponents.MODIFIERS, AppliedModifiers.EMPTY);
        for (var modifier : modifiers.traitData().entrySet()) {
            Trait trait = TraitDataManager.get(modifier.getKey());
            if (trait == null) {
                SlagTraits.LOGGER.warn("[TraitManager] missing modifier trait data for id={}", modifier.getKey());
                continue;
            }

            int tier = Math.min(ModifierService.tierForValue(trait, modifier.getValue()), trait.getMaxTier());

            ActiveTrait existing = activeTraits.get(trait.getId());
            if (existing == null || tier > existing.tier()) {
                activeTraits.put(trait.getId(), new ActiveTrait(trait, tier));
            }
        }

        return new ArrayList<>(activeTraits.values());
    }

    public static boolean matches(EquipmentType traitType, EquipmentType stackType) {
        if (traitType == EquipmentType.NONE || stackType == EquipmentType.NONE) return false;
        return traitType == EquipmentType.BOTH || stackType == EquipmentType.BOTH || traitType == stackType;
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
            case "is_equipped" -> {
                for (ItemStack slot : holder.getAllSlots()) {
                    if (slot == tool) yield true;
                }
                yield false;
            }
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
