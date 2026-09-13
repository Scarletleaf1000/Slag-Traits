package me.scarletleaf1000.slagtraits.recipe;

import me.scarletleaf1000.slagtraits.recipe.smithing.SmithingRules;
import me.scarletleaf1000.slagtraits.recipe.smithing.PartSwapService;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public class PartSwapRecipe implements SmithingRecipe {
    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return SmithingRules.isModularEquipment(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return SmithingRules.isReplacementPart(stack);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return input.template().isEmpty()
                && PartSwapService.canSwap(input.base(), input.addition());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        return PartSwapService.createResult(input.base(), input.addition());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PART_SWAP.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }
}
