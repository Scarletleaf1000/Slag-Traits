package me.scarletleaf1000.slagtraits.mixin;

import me.scarletleaf1000.slagtraits.traits.effect.implementation.PassiveTraitHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityFireImmuneMixin {

    @Inject(method = "fireImmune()Z", at = @At("HEAD"), cancellable = true)
    private void slagtraits$onFireImmune(CallbackInfoReturnable<Boolean> cir) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getItem();
        if (stack.isEmpty()) return;

        Boolean override = PassiveTraitHelper.isFireResistant(stack);
        if (override != null) {
            cir.setReturnValue(override);
            cir.cancel();
        }
    }
}
