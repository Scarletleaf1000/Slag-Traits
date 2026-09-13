package me.scarletleaf1000.slagtraits.traits.data;

import me.scarletleaf1000.slagtraits.traits.Trait;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TraitDataManager {
    private static final Map<ResourceLocation, Trait> TRAITS = new HashMap<>();

    public static Trait get(ResourceLocation id) {
        return TRAITS.get(id);
    }

    public static void setTraits(Map<ResourceLocation, Trait> traits) {
        TRAITS.clear();
        TRAITS.putAll(traits);
    }

    public static Trait findModifierForItem(ItemStack stack) {
        for (Trait trait : TRAITS.values()) {
            if (!trait.isModifier()) continue;
            if (ItemStack.isSameItem(trait.getUpgradeItem(), stack)) return trait;
        }
        return null;
    }

    public static Map<ResourceLocation, Trait> getAll() {
        return Map.copyOf(TRAITS);
    }
}
