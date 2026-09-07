package me.scarletleaf1000.slagtraits.register;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.ReinforcedTrait;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AllTraits {
    private static final Map<ResourceLocation, ISlagTrait> TRAITS = new HashMap<>();

    public static void register(ResourceLocation id, ISlagTrait trait) {
        TRAITS.put(id, trait);
    }

    public static ISlagTrait get(ResourceLocation id) {
        return TRAITS.get(id);
    }

    public static void register() {
       //register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "trait_name"), new TraitNameTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "reinforced"), new ReinforcedTrait());
    }
}
