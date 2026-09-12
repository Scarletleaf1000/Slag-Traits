package me.scarletleaf1000.slagtraits.mixin;

import me.scarletleaf1000.slagtraits.traits.effect.implementation.PassiveTraitHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public class ItemStackHurtAndBreakMixin {

    @ModifyVariable(
            method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private int slagtraits$onHurtAndBreak(int amount) {
        if (amount <= 0) return amount;

        ItemStack self = (ItemStack) (Object) this;
        if (PassiveTraitHelper.shouldPreventDurabilityDamage(self)) {
            return 0;
        }

        return amount;
    }
}
