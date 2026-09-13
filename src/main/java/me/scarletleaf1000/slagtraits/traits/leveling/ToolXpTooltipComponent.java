package me.scarletleaf1000.slagtraits.traits.leveling;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * Carries tool XP progress into the tooltip so a {@code ClientTooltipComponent}
 * can render the progress bar and "(current/needed)" text.
 */
public record ToolXpTooltipComponent(long xpIntoLevel, long xpForNextLevel) implements TooltipComponent {
}
