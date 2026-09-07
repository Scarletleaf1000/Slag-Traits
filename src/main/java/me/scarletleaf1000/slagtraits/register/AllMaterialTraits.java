package me.scarletleaf1000.slagtraits.register;

import me.scarletleaf1000.slagtraits.content.traits.TraitInstance;
import me.scarletleaf1000.slagtraits.content.traits.data.MaterialTraitEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllMaterialTraits {
    private static Map<ResourceLocation, List<TraitInstance>> TRAITS = Map.of();

    public static List<TraitInstance> getTraitsFor(ResourceLocation materialId) {
        return TRAITS.getOrDefault(materialId, List.of());
    }

    public static void setAll(Map<ResourceLocation, MaterialTraitEntry> entries) {
        Map<ResourceLocation, List<TraitInstance>> rebuilt = new HashMap<>();
        entries.forEach((fileId, entry) -> rebuilt.put(entry.material(), entry.traits()));
        TRAITS = Map.copyOf(rebuilt);
    }

    public static void setAllTraits(Map<ResourceLocation, List<TraitInstance>> traits) {
        TRAITS = Map.copyOf(traits);
    }

    public static Map<ResourceLocation, List<TraitInstance>> snapshot() {
        return TRAITS; // for the sync packet
    }
}
