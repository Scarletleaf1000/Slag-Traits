package me.scarletleaf1000.slagtraits.content.traits.implementation;

import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static me.scarletleaf1000.slagtraits.util.DisplayUtils.intToRoman;

public class SturdyTrait implements ISlagTrait {
    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.sturdy", intToRoman(tier))
                .withStyle(ChatFormatting.GRAY);
    }
}
