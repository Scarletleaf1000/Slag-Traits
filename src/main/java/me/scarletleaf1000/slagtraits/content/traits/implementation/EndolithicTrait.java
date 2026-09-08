package me.scarletleaf1000.slagtraits.content.traits.implementation;

import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static me.scarletleaf1000.slagtraits.util.DisplayUtils.intToRoman;

public class EndolithicTrait implements ISlagTrait {
    @Override
    public Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.endolithic", intToRoman(tier))
                .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public void onDamageDealt(LivingDamageEvent.Pre event, ItemStack stack, int tier) {
        if (!stack.isDamageableItem()) return;

        double depth = event.getSource().getSourcePosition().get(Direction.Axis.Y);

        float modifier = (float) mapFractionToNegative(calculateValue(depth, tier));
        event.setNewDamage(event.getOriginalDamage() + modifier);
    }

    @Override
    public void onBreakSpeed(PlayerEvent.BreakSpeed event, ItemStack stack, int tier) {
        if (!stack.isDamageableItem()) return;

        double depth = event.getPosition().get().getY();

        float modifier = (float) mapFractionToNegative(calculateValue(depth, tier * 2));
        event.setNewSpeed(event.getOriginalSpeed() + modifier);
    }

    private static double calculateValue(double depth, int tier) {
        // Constant decay factor that preserves the shape/curvature across tiers
        final double B = 0.00818757;

        // Linearly interpolate the amplitude (A) and vertical shift (C) based on the tier
        double A = 1.30000653 * tier - 0.37143043;
        double C = -0.19542028 * tier + 0.62726294;

        return A * Math.exp(-B * depth) + C;
    }

    private static double mapFractionToNegative(double value) {
        // Guard clause to prevent division by zero or unexpected negative inputs
        if (value <= 0) {
            throw new IllegalArgumentException("Input must be strictly greater than 0.");
        }

        // If the value is less than 1 (a fraction), return the negative reciprocal.
        // Otherwise, leave the positive number exactly the same.
        return (value < 1.0) ? (-1.0 / value) : value;
    }
}
