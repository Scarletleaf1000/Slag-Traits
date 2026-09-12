package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

/**
 * Applies attribute modifiers via ItemAttributeModifierEvent.
 * This handler is invoked directly from TraitEventHandler.onItemAttributeModifier,
 * not through the normal event dispatch.
 *
 * Params:
 *   attribute (required) - attribute id, e.g. "minecraft:generic.movement_speed"
 *   amount / amount_per_tier
 *   operation - "add_value" | "add_multiplied_base" | "add_multiplied_total" (default add_value)
 *   slot - "any" | "mainhand" | "offhand" | "hand" | "armor" | "head" | "chest" | "legs" | "feet" | "body" (default any)
 */
public class AttributeModifierHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        if (!(event instanceof ItemAttributeModifierEvent attrEvent)) return;

        String attributeId = effect.getString("attribute");
        if (attributeId == null) return;
        ResourceLocation rl = ResourceLocation.tryParse(attributeId);
        if (rl == null) return;
        var attribute = BuiltInRegistries.ATTRIBUTE.getHolder(rl);
        if (attribute.isEmpty()) return;

        float amount = effect.getScaledFloat("amount", "amount_per_tier", tier, 0f);
        if (amount == 0f) return;

        AttributeModifier.Operation operation = parseOperation(effect.getString("operation"));
        EquipmentSlotGroup slot = parseSlot(effect.getString("slot"));

        ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID,
                "trait." + rl.getPath() + "." + Integer.toHexString(effect.getParameters().hashCode()));

        attrEvent.addModifier(attribute.get(), new AttributeModifier(modifierId, amount, operation), slot);
    }

    private static AttributeModifier.Operation parseOperation(String op) {
        if (op == null) return AttributeModifier.Operation.ADD_VALUE;
        return switch (op.toLowerCase()) {
            case "add_multiplied_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case "add_multiplied_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> AttributeModifier.Operation.ADD_VALUE;
        };
    }

    private static EquipmentSlotGroup parseSlot(String slot) {
        if (slot == null) return EquipmentSlotGroup.ANY;
        return switch (slot.toLowerCase()) {
            case "mainhand" -> EquipmentSlotGroup.MAINHAND;
            case "offhand" -> EquipmentSlotGroup.OFFHAND;
            case "hand" -> EquipmentSlotGroup.HAND;
            case "armor" -> EquipmentSlotGroup.ARMOR;
            case "head" -> EquipmentSlotGroup.HEAD;
            case "chest" -> EquipmentSlotGroup.CHEST;
            case "legs" -> EquipmentSlotGroup.LEGS;
            case "feet" -> EquipmentSlotGroup.FEET;
            case "body" -> EquipmentSlotGroup.BODY;
            default -> EquipmentSlotGroup.ANY;
        };
    }
}
