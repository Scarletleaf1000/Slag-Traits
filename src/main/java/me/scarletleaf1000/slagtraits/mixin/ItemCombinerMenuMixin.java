package me.scarletleaf1000.slagtraits.mixin;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(SmithingMenu.class)
public class ItemCombinerMenuMixin {
    @Inject(method = "createInputSlotDefinitions", at = @At("RETURN"), cancellable = true)
    private void slagTraits$allowModularItemsInBaseSlot(
            CallbackInfoReturnable<ItemCombinerMenuSlotDefinition> callback) {
        ItemCombinerMenuSlotDefinition original = callback.getReturnValue();
        ItemCombinerMenuSlotDefinition.Builder builder = ItemCombinerMenuSlotDefinition.create();

        for (ItemCombinerMenuSlotDefinition.SlotDefinition slot : original.getSlots()) {
            Predicate<ItemStack> originalPredicate = slot.mayPlace();
            Predicate<ItemStack> predicate = slot.slotIndex() == SmithingMenu.BASE_SLOT
                    ? stack -> isModular(stack) || originalPredicate.test(stack)
                    : stack -> !isModular(stack) && originalPredicate.test(stack);
            builder.withSlot(slot.slotIndex(), slot.x(), slot.y(), predicate);
        }

        ItemCombinerMenuSlotDefinition.SlotDefinition result = original.getResultSlot();
        callback.setReturnValue(builder.withResultSlot(result.slotIndex(), result.x(), result.y()).build());
    }

    @Inject(method = "canMoveIntoInputSlots", at = @At("HEAD"), cancellable = true)
    private void slagTraits$canMoveModularItem(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
        if (isModular(stack)) callback.setReturnValue(true);
    }

    @Inject(method = "getSlotToQuickMoveTo", at = @At("HEAD"), cancellable = true)
    private void slagTraits$moveModularItemToBase(ItemStack stack, CallbackInfoReturnable<Integer> callback) {
        if (isModular(stack)) callback.setReturnValue(SmithingMenu.BASE_SLOT);
    }

    private static boolean isModular(ItemStack stack) {
        return stack.getItem() instanceof IModularItem;
    }
}
