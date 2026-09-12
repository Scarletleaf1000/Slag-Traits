package me.scarletleaf1000.slagtraits.traits.data;

import me.scarletleaf1000.slagtraits.traits.Trait;
import net.minecraft.resources.ResourceLocation;

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
}
