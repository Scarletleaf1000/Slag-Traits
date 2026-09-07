package me.scarletleaf1000.slagtraits.content.traits.implementation;


import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.util.TraitUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ReinforcedTrait implements ISlagTrait {
    @Override
    public Component getTooltipLine(float modifier) {
        return Component.translatable("trait.slagtraits.reinforced", TraitUtils.formatModifier(modifier))
                .withStyle(ChatFormatting.GRAY);
    }
}
