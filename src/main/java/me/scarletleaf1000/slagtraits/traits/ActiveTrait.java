package me.scarletleaf1000.slagtraits.traits;

import net.minecraft.resources.ResourceLocation;

public record ActiveTrait(Trait trait, int tier) {
    public ResourceLocation getId() {return trait.getId();}
}
