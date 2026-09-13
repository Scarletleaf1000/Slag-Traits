package me.scarletleaf1000.slagtraits.mixin;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import me.scarletleaf1000.slagtraits.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void slagTraits$modularEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
        if (stack.getItem() instanceof IModularItem && !Config.MODULAR_TOOLS_ENCHANTABLE.get()) {
            callback.setReturnValue(false);
        }
    }
}
