package me.scarletleaf1000.slagtraits.traits.loader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.traits.data.TraitDataManager;
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
            var result = Trait.TRAIT_CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            if (result.result().isPresent()) {
                Trait trait = result.result().get();
                traits.put(trait.getId(), trait);
                SlagTraits.LOGGER.info("[TraitReload] loaded trait id={} from {}", trait.getId(), entry.getKey());
            } else {
                SlagTraits.LOGGER.error("[TraitReload] failed to parse trait {}: {}", entry.getKey(), result.error().map(Object::toString).orElse("unknown error"));
            }
        }
        TraitDataManager.setTraits(traits);
        SlagTraits.LOGGER.info("[TraitReload] total traits loaded: {}", traits.size());
    }
}
