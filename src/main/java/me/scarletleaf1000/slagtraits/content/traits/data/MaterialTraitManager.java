package me.scarletleaf1000.slagtraits.content.traits.data;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialTraitManager {
    private static final Map<ResourceLocation, List<MaterialTraitData.TraitRef>> MATERIAL_TRAIT_MAP = new HashMap<>();

    public static List<MaterialTraitData.TraitRef> getTraits(ResourceLocation material) {
        return MATERIAL_TRAIT_MAP.getOrDefault(material, List.of());
    }

    public static void setMaterialTraits(Map<ResourceLocation, List<MaterialTraitData.TraitRef>> map) {
        MATERIAL_TRAIT_MAP.clear();
        MATERIAL_TRAIT_MAP.putAll(map);
    }
}
