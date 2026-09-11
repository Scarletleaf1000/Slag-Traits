package me.scarletleaf1000.slagtraits.traits.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.data.MaterialTraitData;
import me.scarletleaf1000.slagtraits.traits.data.MaterialTraitManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialTraitDataReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public MaterialTraitDataReloadListener() {
        super(GSON, "slagtraits/material_traits");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, List<MaterialTraitData.TraitRef>> materialMap = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            if (!entry.getValue().isJsonObject()) continue;
            var result = MaterialTraitData.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            if (result.result().isPresent()) {
                MaterialTraitData parsed = result.result().get();
                materialMap.put(parsed.material(), parsed.traits());
                SlagTraits.LOGGER.info("[MaterialTraitReload] loaded material={} traits={} from {}", parsed.material(), parsed.traits().size(), entry.getKey());
            } else {
                SlagTraits.LOGGER.error("[MaterialTraitReload] failed to parse material traits {}: {}", entry.getKey(), result.error().map(Object::toString).orElse("unknown error"));
            }
        }
        MaterialTraitManager.setMaterialTraits(materialMap);
        SlagTraits.LOGGER.info("[MaterialTraitReload] total material entries loaded: {}", materialMap.size());
    }
}
