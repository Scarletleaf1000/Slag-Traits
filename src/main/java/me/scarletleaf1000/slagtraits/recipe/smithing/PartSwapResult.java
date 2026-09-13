package me.scarletleaf1000.slagtraits.recipe.smithing;

import net.minecraft.world.item.ItemStack;

public record PartSwapResult(ItemStack result, ItemStack removedPart) {
    public static final PartSwapResult EMPTY = new PartSwapResult(ItemStack.EMPTY, ItemStack.EMPTY);

    public boolean isSuccess() {
        return !result.isEmpty();
    }
}
