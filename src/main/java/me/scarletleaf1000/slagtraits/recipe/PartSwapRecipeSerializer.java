package me.scarletleaf1000.slagtraits.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PartSwapRecipeSerializer implements RecipeSerializer<PartSwapRecipe> {
    private static final PartSwapRecipe INSTANCE = new PartSwapRecipe();
    private static final MapCodec<PartSwapRecipe> CODEC = MapCodec.unit(INSTANCE);
    private static final StreamCodec<RegistryFriendlyByteBuf, PartSwapRecipe> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public MapCodec<PartSwapRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PartSwapRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
