package me.scarletleaf1000.slagtraits.content.traits.data;

import dev.lopyluna.slag.content.items.dynamic_part.IDynamicPart;
import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.types.MaterialType;
import me.scarletleaf1000.slagtraits.SlagTraits;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MaterialAdapter {
    public static List<ResourceLocation> getMaterials(ItemStack stack) {
        if (stack.isEmpty()) return List.of();

        List<ResourceLocation> materials = new ArrayList<>();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (stack.getItem() instanceof IModularItem modular) {
            var parts = modular.getParts(stack);
            SlagTraits.LOGGER.debug("[MaterialAdapter] item={} is IModularItem parts={}", itemId, parts == null ? 0 : parts.size());
            if (parts != null && !parts.isEmpty()) {
                for (MaterialType type : modular.getMaterialTypes(parts)) {
                    SlagTraits.LOGGER.debug("[MaterialAdapter] item={} materialType={}", itemId, type == null ? null : type.id);
                    if (type != null && type.id != null) {
                        materials.add(type.id);
                    }
                }
            }
        } else if (stack.getItem() instanceof IDynamicPart part) {
            SlagTraits.LOGGER.debug("[MaterialAdapter] item={} is IDynamicPart", itemId);
            part.getMaterialType(stack).ifPresent(type -> {
                SlagTraits.LOGGER.debug("[MaterialAdapter] item={} dynamic material={}", itemId, type.id);
                if (type.id != null) materials.add(type.id);
            });
        } else {
            SlagTraits.LOGGER.debug("[MaterialAdapter] item={} is not a modular/dynamic item", itemId);
        }

        SlagTraits.LOGGER.debug("[MaterialAdapter] item={} resolvedMaterials={}", itemId, materials);
        return materials;
    }
}
