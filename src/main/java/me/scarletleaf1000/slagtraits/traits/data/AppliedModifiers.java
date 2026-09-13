package me.scarletleaf1000.slagtraits.traits.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record AppliedModifiers(Map<ResourceLocation, Integer> traitData) {
    public static final AppliedModifiers EMPTY = new AppliedModifiers(Map.of());

    public static final MapCodec<AppliedModifiers> MAP_CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                    .xmap(AppliedModifiers::new, AppliedModifiers::traitData)
                    .fieldOf("traitData");

    public static final Codec<AppliedModifiers> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, AppliedModifiers> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.VAR_INT),
                    AppliedModifiers::traitData,
                    AppliedModifiers::new
            );

    public AppliedModifiers {
        traitData = Map.copyOf(traitData);
    }

    public int getValue(ResourceLocation id) {
        return traitData.getOrDefault(id, 0);
    }

    public boolean has(ResourceLocation id) {
        return traitData.containsKey(id);
    }

    public AppliedModifiers withAdded(ResourceLocation id, int added) {
        Map<ResourceLocation, Integer> copy = new HashMap<>(traitData);
        if (added <= 0) {
            copy.remove(id);
        } else {
            copy.put(id, copy.getOrDefault(id, 0) + added);
        }
        return new AppliedModifiers(copy);
    }
}
