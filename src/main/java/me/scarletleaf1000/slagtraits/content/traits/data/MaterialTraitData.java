package me.scarletleaf1000.slagtraits.content.traits.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MaterialTraitData(ResourceLocation material, List<TraitRef> traits) {
    public record TraitRef(ResourceLocation id, int tier) {
        public static final Codec<TraitRef> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(TraitRef::id),
                        Codec.INT.optionalFieldOf("tier", 1).forGetter(TraitRef::tier)
                ).apply(instance, TraitRef::new));
    }

    public static final Codec<MaterialTraitData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("material").forGetter(MaterialTraitData::material),
                    TraitRef.CODEC.listOf().fieldOf("traits").forGetter(MaterialTraitData::traits)
            ).apply(instance, MaterialTraitData::new));
}
