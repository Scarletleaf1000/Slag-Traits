package me.scarletleaf1000.slagtraits.content.traits.implementation;

import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static me.scarletleaf1000.slagtraits.util.DisplayUtils.intToRoman;

public class PolishedTrait implements ISlagTrait {

    private final float DAMAGE_INCREASE = 1.5F;

    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.polished", intToRoman(tier))
                .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void onDamageDealt(LivingDamageEvent.Pre event, ItemStack stack, int tier) {
        if (!stack.isDamageableItem()) return;

        // 0.0 = full durability, 1.0 = nearly broken
        float missing = (float) stack.getDamageValue() / stack.getMaxDamage();

        float modifier = (-missing + DAMAGE_INCREASE) * ((float) Math.sqrt(tier * 2));
        event.setNewDamage(event.getOriginalDamage() * modifier);
    }

    @Override
    public void onBreakSpeed(PlayerEvent.BreakSpeed event, ItemStack stack, int tier) {
        if (!stack.isDamageableItem()) return;

        // 0.0 = full durability, 1.0 = nearly broken
        float missing = (float) stack.getDamageValue() / stack.getMaxDamage();
        float durability = 1 - missing;

        float modifier = durability * tier;
        event.setNewSpeed(event.getOriginalSpeed() + modifier);
    }
}
