package me.scarletleaf1000.slagtraits.mixin;

import com.mojang.datafixers.util.Pair;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.content.traits.implementation.SturdyTrait;
import me.scarletleaf1000.slagtraits.util.TraitUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Gives modular items with the {@code slagtraits:sturdy} trait a chance to
 * ignore durability loss entirely. All durability damage (tools, weapons,
 * armor) funnels through {@code hurtAndBreak}, so one hook covers every
 * item type.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void slagtraits$sturdyDurabilitySave(int amount, ServerLevel level, @Nullable LivingEntity entity,
                                                 Consumer<Item> onBreak, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        for (Pair<ISlagTrait, Integer> pair : TraitUtils.getTraits(self)) {
            if (pair.getFirst() instanceof SturdyTrait) {
                RandomSource random = entity != null ? entity.getRandom() : level.getRandom();
                // 5% chance per tier to ignore the durability loss entirely
                if (random.nextFloat() < Math.min(0.05f * pair.getSecond(), 1.0f)) {
                    ci.cancel();
                }
                return;
            }
        }
    }
}
