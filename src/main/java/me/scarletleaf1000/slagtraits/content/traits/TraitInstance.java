package me.scarletleaf1000.slagtraits.content.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record TraitInstance(ResourceLocation id, int tier) {
    public static final Codec<TraitInstance> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(TraitInstance::id),
                    Codec.INT.fieldOf("tier").forGetter(TraitInstance::tier)
            ).apply(instance, TraitInstance::new)
    );

    public static final StreamCodec<ByteBuf, TraitInstance> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, TraitInstance::id,
            ByteBufCodecs.VAR_INT, TraitInstance::tier,
            TraitInstance::new
    );
}
