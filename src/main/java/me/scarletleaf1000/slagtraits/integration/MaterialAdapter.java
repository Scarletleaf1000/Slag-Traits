package me.scarletleaf1000.slagtraits.integration;

import dev.lopyluna.slag.content.items.dynamic_part.IDynamicPart;
import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.types.MaterialType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MaterialAdapter {
    public static List<ResourceLocation> getMaterials(ItemStack stack) {
        if (stack.isEmpty()) return List.of();

        List<ResourceLocation> materials = new ArrayList<>();

        if (stack.getItem() instanceof IModularItem modular) {
            var parts = modular.getParts(stack);
            if (parts != null && !parts.isEmpty()) {
                for (MaterialType type : modular.getMaterialTypes(parts)) {
                    if (type != null && type.id != null) {
                        materials.add(type.id);
                    }
                }
            }
        } else if (stack.getItem() instanceof IDynamicPart part) {
            part.getMaterialType(stack).ifPresent(type -> {
                if (type.id != null) materials.add(type.id);
            });
        }

        return materials;
    }
}
