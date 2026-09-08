package me.scarletleaf1000.slagtraits.mixin;

import dev.lopyluna.slag.content.items.dynamic_part.IDynamicPart;
import dev.lopyluna.slag.content.types.MaterialType;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.register.AllMaterialTraits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Replaces slag's material-level {@code fireProof} flag with the
 * {@code slagtraits:fire_resistant} trait. A part only makes the assembled
 * modular item fire-immune if its material has the trait.
 */
@Mixin(IDynamicPart.class)
public interface IDynamicPartMixin {

    @Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true, remap = false)
    default void slagtraits$traitGateFireImmunity(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        MaterialType material = ((IDynamicPart) this).getMaterialType(stack).orElse(null);
        if (material == null) {
            cir.setReturnValue(false);
            return;
        }
        ResourceLocation fireResistant = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "fire_resistant");
        boolean hasTrait = AllMaterialTraits.getTraitsFor(material.id).stream()
                .anyMatch(instance -> instance.id().equals(fireResistant));
        cir.setReturnValue(hasTrait);
    }
}
