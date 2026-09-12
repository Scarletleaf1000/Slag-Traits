package me.scarletleaf1000.slagtraits.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModifierApplyRecipeSerializer implements RecipeSerializer<ModifierApplyRecipe> {
    private static final ModifierApplyRecipe INSTANCE = new ModifierApplyRecipe();
    private static final MapCodec<ModifierApplyRecipe> CODEC = MapCodec.unit(INSTANCE);
    private static final StreamCodec<RegistryFriendlyByteBuf, ModifierApplyRecipe> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public MapCodec<ModifierApplyRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ModifierApplyRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
