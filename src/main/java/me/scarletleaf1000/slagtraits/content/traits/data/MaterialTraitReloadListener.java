package me.scarletleaf1000.slagtraits.content.traits.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.register.AllMaterialTraits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class MaterialTraitReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, MaterialTraitEntry>> {
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "material_traits";

    @Override
    protected Map<ResourceLocation, MaterialTraitEntry> prepare(ResourceManager rm, ProfilerFiller profiler) {
        Map<ResourceLocation, MaterialTraitEntry> map = new HashMap<>();
        // scan data/*/material_traits/*.json, parse each with GSON,
        // decode with MaterialTraitEntry.CODEC.parse(JsonOps.INSTANCE, json)
        for (var entry : rm.listResources(DIRECTORY, p -> p.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation fileId = entry.getKey();
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                MaterialTraitEntry.CODEC.parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(err -> SlagTraits.LOGGER.error("Bad traits file {}: {}", fileId, err))
                        .ifPresent(e -> map.put(fileId, e));
            } catch (Exception e) {
                SlagTraits.LOGGER.error("Failed reading {}", fileId, e);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, MaterialTraitEntry> prepared, ResourceManager rm, ProfilerFiller profiler) {
        AllMaterialTraits.setAll(prepared); // main thread: swap the static map
    }
}
