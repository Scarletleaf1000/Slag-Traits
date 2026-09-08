package me.scarletleaf1000.slagtraits.content.traits.implementation;

import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class FireResistantTrait implements ISlagTrait {
    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.fire_resistant")
                .withStyle(ChatFormatting.GRAY);
    }
}
