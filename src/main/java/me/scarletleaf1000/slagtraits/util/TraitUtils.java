package me.scarletleaf1000.slagtraits.util;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.types.MaterialType;
import com.mojang.datafixers.util.Pair;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.content.traits.TraitInstance;
import me.scarletleaf1000.slagtraits.register.AllMaterialTraits;
import me.scarletleaf1000.slagtraits.register.AllTraits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitUtils {
    public static List<Pair<ISlagTrait, Float>> getTraits(ItemStack stack) {
        if (!(stack.getItem() instanceof IModularItem modularItem)) return List.of();

        var parts = modularItem.getParts(stack);
        if (parts == null || parts.isEmpty()) return List.of();

        Map<ResourceLocation, Pair<ISlagTrait, Float>> resolved = new HashMap<>();
        for (MaterialType material : modularItem.getMaterialTypes(parts)) {
            for (TraitInstance instance : AllMaterialTraits.getTraitsFor(material.id)) {
                ISlagTrait trait = AllTraits.get(instance.id());
                if (trait == null) continue;
                resolved.merge(instance.id(), Pair.of(trait, instance.modifier()),
                        (a, b) -> a.getSecond() >= b.getSecond() ? a : b);
            }
        }
        return List.copyOf(resolved.values());
    }

    /** Formats a trait modifier for display: whole numbers as ints ("3"), otherwise one decimal ("1.5"). */
    public static String formatModifier(float modifier) {
        if (modifier == Math.floor(modifier) && !Float.isInfinite(modifier))
            return String.valueOf((int) modifier);
        return String.valueOf(modifier);
    }
}
