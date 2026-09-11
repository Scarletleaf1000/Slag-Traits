package me.scarletleaf1000.slagtraits.content.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TraitEffect {
    private final ResourceLocation type;
    private final Map<String, JsonElement> parameters;

    public static final Codec<TraitEffect> EFFECT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(TraitEffect::getType),
                    Codec.unboundedMap(Codec.STRING, ExtraCodecs.JSON)
                            .optionalFieldOf("parameters", new HashMap<>())
                            .forGetter(TraitEffect::getParameters)
            ).apply(instance, TraitEffect::new));

    public TraitEffect(ResourceLocation type, Map<String, JsonElement> parameters) {
        this.type = type;
        this.parameters = parameters != null ? parameters : Collections.emptyMap();
    }

    public ResourceLocation getType() {
        return type;
    }

    public Map<String, JsonElement> getParameters() {
        return parameters;
    }

    public float getFloat(String key, float fallback) {
        JsonElement v = parameters.get(key);
        if (v == null || v.isJsonNull()) return fallback;
        if (v.isJsonPrimitive()) {
            JsonPrimitive p = v.getAsJsonPrimitive();
            if (p.isNumber()) return p.getAsNumber().floatValue();
            if (p.isString()) {
                try { return Float.parseFloat(p.getAsString()); } catch (NumberFormatException ignored) {}
            }
        }
        return fallback;
    }

    public int getInt(String key, int fallback) {
        JsonElement v = parameters.get(key);
        if (v == null || v.isJsonNull()) return fallback;
        if (v.isJsonPrimitive()) {
            JsonPrimitive p = v.getAsJsonPrimitive();
            if (p.isNumber()) return p.getAsNumber().intValue();
            if (p.isString()) {
                try { return Integer.parseInt(p.getAsString()); } catch (NumberFormatException ignored) {}
            }
        }
        return fallback;
    }

    public String getString(String key) {
        JsonElement v = parameters.get(key);
        if (v == null || v.isJsonNull()) return null;
        if (v.isJsonPrimitive()) return v.getAsString();
        return v.toString();
    }
}
