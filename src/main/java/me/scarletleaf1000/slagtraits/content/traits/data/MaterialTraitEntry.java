package me.scarletleaf1000.slagtraits.content.traits.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.scarletleaf1000.slagtraits.content.traits.TraitInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MaterialTraitEntry(ResourceLocation material, List<TraitInstance> traits) {
    public static final Codec<MaterialTraitEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("material").forGetter(MaterialTraitEntry::material),
                    TraitInstance.CODEC.listOf().fieldOf("traits").forGetter(MaterialTraitEntry::traits)
            ).apply(instance, MaterialTraitEntry::new)
    );
}
