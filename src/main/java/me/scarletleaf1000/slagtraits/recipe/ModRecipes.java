package me.scarletleaf1000.slagtraits.recipe;

import me.scarletleaf1000.slagtraits.SlagTraits;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, SlagTraits.MOD_ID);

    public static final Supplier<PartSwapRecipeSerializer> PART_SWAP =
            RECIPE_SERIALIZERS.register("part_swap", PartSwapRecipeSerializer::new);

    public static final Supplier<ModifierApplyRecipeSerializer> MODIFIER_APPLY =
            RECIPE_SERIALIZERS.register("modifier_apply", ModifierApplyRecipeSerializer::new);

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
