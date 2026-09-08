package me.scarletleaf1000.slagtraits.content.traits.implementation;

import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import static me.scarletleaf1000.slagtraits.util.DisplayUtils.intToRoman;

public class BejeweledTrait implements ISlagTrait {
    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.bejeweled", intToRoman(tier))
                .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void onDamageTaken(LivingDamageEvent.Pre event, ItemStack stack, int tier) {
        if (event.getSource().getEntity() instanceof LivingEntity entity) {
            entity.addEffect(new MobEffectInstance(
                    MobEffects.GLOWING,
                    200 * tier,
                    0,
                    false,
                    true,
                    true
            ));
        }
    }
}
