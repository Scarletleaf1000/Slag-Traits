package me.scarletleaf1000.slagtraits.recipe.smithing;

import dev.lopyluna.slag.content.items.dynamic_part.IDynamicPart;
import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.items.modular.DataDynamicParts;
import dev.lopyluna.slag.register.AllDataComponents;
import dev.lopyluna.slag.register.AllDynamicTypes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.OptionalInt;

public final class PartSwapService {
    private PartSwapService() {}

    public static OptionalInt findReplaceablePart(ItemStack tool, ItemStack replacement) {
        if (!(tool.getItem() instanceof IModularItem modular)) return OptionalInt.empty();
        if (!(replacement.getItem() instanceof IDynamicPart replacementPart)) return OptionalInt.empty();

        DataDynamicParts parts = modular.getParts(tool);
        if (parts == null || parts.isEmpty()) return OptionalInt.empty();

        TagKey<Item> replacementSegment = replacementPart.getPartSegment(replacement);
        List<ItemStack> items = parts.itemsCopy();

        for (int index = 0; index < items.size(); index++) {
            ItemStack existing = items.get(index);
            if (existing.getItem() instanceof IDynamicPart existingPart
                    && existingPart.getPartSegment(existing).equals(replacementSegment)) {
                return OptionalInt.of(index);
            }
        }

        return OptionalInt.empty();
    }

    public static boolean canSwap(ItemStack tool, ItemStack replacement) {
        if (tool.isEmpty() || replacement.isEmpty()) return false;
        if (!(tool.getItem() instanceof IModularItem modular)) return false;
        if (!(replacement.getItem() instanceof IDynamicPart)) return false;

        DataDynamicParts currentParts = modular.getParts(tool);
        if (currentParts == null || currentParts.isEmpty()) return false;

        OptionalInt index = findReplaceablePart(tool, replacement);
        if (index.isEmpty()) return false;

        List<ItemStack> newParts = currentParts.itemsCopy();
        ItemStack currentPart = newParts.get(index.getAsInt());

        ItemStack normalizedReplacement = replacement.copyWithCount(1);
        if (ItemStack.isSameItemSameComponents(currentPart, normalizedReplacement)) {
            return false;
        }

        newParts.set(index.getAsInt(), normalizedReplacement);
        DataDynamicParts prospectiveParts = new DataDynamicParts(newParts);

        var currentType = modular.getModularTypeFromParts(currentParts);
        var prospectiveType = modular.getModularTypeFromParts(prospectiveParts);

        return currentType != null
                && prospectiveType != null
                && currentType.equals(prospectiveType);
    }

    public static PartSwapResult swap(ItemStack tool, ItemStack replacement) {
        if (!canSwap(tool, replacement)) return PartSwapResult.EMPTY;
        IModularItem modular = (IModularItem) tool.getItem();

        OptionalInt index = findReplaceablePart(tool, replacement);
        if (index.isEmpty()) return PartSwapResult.EMPTY;

        List<ItemStack> parts = modular.getParts(tool).itemsCopy();
        ItemStack removed = parts.get(index.getAsInt()).copy();
        removed.remove(AllDataComponents.BUILT);

        ItemStack newPart = replacement.copyWithCount(1);
        var typeID = tool.get(AllDataComponents.MODULAR_TYPE);
        if (typeID != null) newPart.set(AllDataComponents.BUILT, typeID);
        parts.set(index.getAsInt(), newPart);

        ItemStack newTool = tool.copyWithCount(1);
        modular.setParts(newTool, parts);
        return new PartSwapResult(newTool, removed);
    }

    public static ItemStack createResult(ItemStack tool, ItemStack replacement) {
        return swap(tool, replacement).result();
    }

    public static boolean preservesToolType(IModularItem modular, ItemStack tool, ItemStack replacement) {
        ItemStack newTool = createResult(tool, replacement);
        if (newTool.isEmpty()) return false;

        var oldType = modular.getModularTypeFromParts(modular.getParts(tool));
        var newType = modular.getModularTypeFromParts(modular.getParts(newTool));
        return oldType != null && oldType.equals(newType);
    }

    public static ItemStack getRemovedPart(ItemStack tool, ItemStack replacement) {
        if (!(tool.getItem() instanceof IModularItem modular)) return ItemStack.EMPTY;
        OptionalInt index = findReplaceablePart(tool, replacement);
        if (index.isEmpty()) return ItemStack.EMPTY;

        DataDynamicParts parts = modular.getParts(tool);
        if (parts == null || parts.isEmpty()) return ItemStack.EMPTY;

        List<ItemStack> items = parts.itemsCopy();
        var removed = items.get(index.getAsInt());
        removed.remove(AllDataComponents.BUILT);
        return removed;
    }
}
