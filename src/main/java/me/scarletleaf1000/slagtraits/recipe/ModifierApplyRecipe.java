package me.scarletleaf1000.slagtraits.recipe;

import me.scarletleaf1000.slagtraits.recipe.smithing.ModifierService;
import me.scarletleaf1000.slagtraits.recipe.smithing.SmithingRules;
import me.scarletleaf1000.slagtraits.recipe.smithing.PartSwapService;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public class ModifierApplyRecipe implements SmithingRecipe {
    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return SmithingRules.isModifierTemplate(stack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return SmithingRules.isModularTool(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return SmithingRules.isModifierMaterial(stack);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return ModifierService.canApply(input.template(), input.base(), input.addition());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        return ModifierService.apply(input.template(), input.base(), input.addition()).result();
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
        return ModRecipes.MODIFIER_APPLY.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }
}
