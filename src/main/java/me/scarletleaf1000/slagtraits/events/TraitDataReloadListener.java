package me.scarletleaf1000.slagtraits.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.Trait;
import me.scarletleaf1000.slagtraits.content.traits.data.TraitDataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class TraitDataReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public TraitDataReloadListener() {
        super(GSON, "slagtraits/traits");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, Trait> traits = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            if (!entry.getValue().isJsonObject()) continue;
            Trait.TRAIT_CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                    .result()
                    .ifPresent(trait -> traits.put(trait.getId(), trait));
        }
        TraitDataManager.setTraits(traits);
    }
}
