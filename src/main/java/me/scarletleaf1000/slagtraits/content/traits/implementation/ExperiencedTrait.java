package me.scarletleaf1000.slagtraits.content.traits.implementation;

import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import static me.scarletleaf1000.slagtraits.util.DisplayUtils.intToRoman;

public class ExperiencedTrait implements ISlagTrait {
    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.experienced", intToRoman(tier))
                .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void onXpPickup(PlayerXpEvent.PickupXp event, ItemStack stack, int tier) {
        ExperienceOrb orb = event.getOrb();

        orb.value = orb.getValue() * ((tier / 2) + 1);
    }
}
