package me.scarletleaf1000.slagtraits.mixin;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import me.scarletleaf1000.slagtraits.Config;
import me.scarletleaf1000.slagtraits.recipe.smithing.PartSwapService;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
        SmithingMenu menu = (SmithingMenu) (Object) this;
        if (isModular(stack) && !menu.slots.get(SmithingMenu.BASE_SLOT).hasItem()) {
            callback.setReturnValue(SmithingMenu.BASE_SLOT);
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void slagTraits$handleRemovedPart(Player player, ItemStack result, CallbackInfo ci) {
        SmithingMenu menu = (SmithingMenu) (Object) this;
        ItemStack base = menu.slots.get(SmithingMenu.BASE_SLOT).getItem();
        ItemStack addition = menu.slots.get(SmithingMenu.ADDITIONAL_SLOT).getItem();
        if (!PartSwapService.canSwap(base, addition)) return;

        ItemStack removed = PartSwapService.getRemovedPart(base, addition);
        if (removed.isEmpty()) return;

        ContainerLevelAccess access = ((ItemCombinerMenuAccessor) menu).slagTraits$getAccess();
        access.execute((level, pos) -> {
            if (level.isClientSide) return;
            if (level.random.nextDouble() < Config.PART_SWAP_BREAK_CHANCE.get()) {
                level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                            5, 0.3, 0.2, 0.3, 0.0);
                }
            } else {
                ItemEntity entity = new ItemEntity(level,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, removed.copy());
                entity.setDefaultPickUpDelay();
                level.addFreshEntity(entity);
            }
        });
    }

    private static boolean isModular(ItemStack stack) {
        return stack.getItem() instanceof IModularItem;
    }
}
