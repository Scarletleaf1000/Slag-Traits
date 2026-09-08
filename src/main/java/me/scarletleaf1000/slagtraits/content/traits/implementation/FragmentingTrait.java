package me.scarletleaf1000.slagtraits.content.traits.implementation;

import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import static me.scarletleaf1000.slagtraits.util.DisplayUtils.intToRoman;

public class FragmentingTrait implements ISlagTrait {

    private final float DAMAGE_THRESHOLD = 0.8F;

    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.fragmenting", intToRoman(tier))
                .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void onDamageDealt(LivingDamageEvent.Pre event, ItemStack stack, int tier) {
        if (!stack.isDamageableItem()) return;

        // 0.0 = full durability, 1.0 = nearly broken
        float missing = (float) stack.getDamageValue() / stack.getMaxDamage();

        float modifier = (missing * tier) + DAMAGE_THRESHOLD;
        event.setNewDamage(event.getOriginalDamage() * modifier);
    }
}
