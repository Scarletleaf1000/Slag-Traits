package me.scarletleaf1000.slagtraits.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.scarletleaf1000.slagtraits.content.traits.data.MaterialTraitData;
import me.scarletleaf1000.slagtraits.content.traits.data.MaterialTraitManager;
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
            MaterialTraitData.CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                    .result()
                    .ifPresent(parsed -> materialMap.put(parsed.material(), parsed.traits()));
        }
        MaterialTraitManager.setMaterialTraits(materialMap);
    }
}
