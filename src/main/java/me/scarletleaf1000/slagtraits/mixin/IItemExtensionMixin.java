package me.scarletleaf1000.slagtraits.mixin;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import me.scarletleaf1000.slagtraits.Config;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IItemExtension.class)
public interface IItemExtensionMixin {
    @Inject(method = "supportsEnchantment", at = @At("HEAD"), cancellable = true)
    private void slagTraits$modularSupportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment,
                                                        CallbackInfoReturnable<Boolean> callback) {
        if (stack.getItem() instanceof IModularItem && !Config.MODULAR_TOOLS_ENCHANTABLE.get()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "isPrimaryItemFor", at = @At("HEAD"), cancellable = true)
    private void slagTraits$modularPrimaryItem(ItemStack stack, Holder<Enchantment> enchantment,
                                               CallbackInfoReturnable<Boolean> callback) {
        if (stack.getItem() instanceof IModularItem && !Config.MODULAR_TOOLS_ENCHANTABLE.get()) {
            callback.setReturnValue(false);
        }
    }
}
