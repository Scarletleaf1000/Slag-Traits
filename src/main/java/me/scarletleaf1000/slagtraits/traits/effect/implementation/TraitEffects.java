package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.effect.TraitEffectRegistry;
import net.minecraft.resources.ResourceLocation;

public class TraitEffects {
    public static final ResourceLocation REPAIR_ITEM = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "repair_item");
    public static final ResourceLocation SCALE_STAT = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "scale_stat");
    public static final ResourceLocation XP_MULTIPLIER = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "xp_multiplier");
    public static final ResourceLocation THORNS = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "thorns");
    public static final ResourceLocation COMBAT_EFFECT = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "combat_effect");
    public static final ResourceLocation TOOL_EFFECT = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "tool_effect");
    public static final ResourceLocation ATTRIBUTE_MODIFIER = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "attribute_modifier");
    public static final ResourceLocation STATUS_EFFECT = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "status_effect");
    public static final ResourceLocation MAGNET = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "magnet");
    public static final ResourceLocation NEGATE_DAMAGE_TYPE = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "negate_damage_type");

    public static void register() {
        TraitEffectRegistry.register(REPAIR_ITEM, RepairItemHandler::handle);
        TraitEffectRegistry.register(SCALE_STAT, ScaleStatHandler::handle);
        TraitEffectRegistry.register(XP_MULTIPLIER, XpMultiplierHandler::handle);
        TraitEffectRegistry.register(THORNS, ThornsHandler::handle);
        TraitEffectRegistry.register(COMBAT_EFFECT, CombatEffectHandler::handle);
        TraitEffectRegistry.register(TOOL_EFFECT, ToolEffectHandler::handle);
        TraitEffectRegistry.register(ATTRIBUTE_MODIFIER, AttributeModifierHandler::handle);
        TraitEffectRegistry.register(STATUS_EFFECT, StatusEffectHandler::handle);
        TraitEffectRegistry.register(MAGNET, MagnetHandler::handle);
        TraitEffectRegistry.register(NEGATE_DAMAGE_TYPE, NegateDamageTypeHandler::handle);
    }
}
