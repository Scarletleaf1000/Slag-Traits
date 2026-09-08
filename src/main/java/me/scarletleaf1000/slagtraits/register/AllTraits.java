package me.scarletleaf1000.slagtraits.register;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.BejeweledTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.EndolithicTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.ExperiencedTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.FireResistantTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.FragmentingTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.PolishedTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.RegrowingTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.SturdyTrait;
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
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "bejeweled"), new BejeweledTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "endolithic"), new EndolithicTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "experienced"), new ExperiencedTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "fire_resistant"), new FireResistantTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "fragmenting"), new FragmentingTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "polished"), new PolishedTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "regrowing"), new RegrowingTrait());
        register(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "sturdy"), new SturdyTrait());
    }
}
