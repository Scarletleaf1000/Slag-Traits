package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.effect.TraitEffectRegistry;
import net.minecraft.resources.ResourceLocation;

public class TraitEffects {
    public static final ResourceLocation REPAIR_ITEM = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "repair_item");
    public static final ResourceLocation SCALE_STAT = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "scale_stat");
    public static final ResourceLocation XP_MULTIPLIER = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "xp_multiplier");
    public static final ResourceLocation THORNS = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "thorns");

    public static void register() {
        TraitEffectRegistry.register(REPAIR_ITEM, RepairItemHandler::handle);
        TraitEffectRegistry.register(SCALE_STAT, ScaleStatHandler::handle);
        TraitEffectRegistry.register(XP_MULTIPLIER, XpMultiplierHandler::handle);
        TraitEffectRegistry.register(THORNS, ThornsHandler::handle);
    }
}
