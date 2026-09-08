package me.scarletleaf1000.slagtraits.content.traits.implementation;

import com.sun.jna.platform.unix.solaris.LibKstat;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Random;

import static me.scarletleaf1000.slagtraits.util.DisplayUtils.intToRoman;

public class RegrowingTrait implements ISlagTrait {
    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.regrowing", intToRoman(tier))
                .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void onPlayerTick(PlayerTickEvent.Post event, ItemStack stack, int tier) {
        if (!stack.isDamageableItem()) return;
        if (stack.getDamageValue() == 0) return;
        Random random = new Random();
        if (random.nextInt(5000) < tier * 10) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }
}
