package me.scarletleaf1000.slagtraits.traits.leveling;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.types.ModularType;
import dev.lopyluna.slag.register.AllDataComponents;
import me.scarletleaf1000.slagtraits.Config;
import me.scarletleaf1000.slagtraits.traits.data.ModDataComponents;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class ToolLeveling {
    private static final Set<String> MINING_ACTIONS = Set.of(
            "pickaxe", "axe", "shovel", "hoe", "hammer", "scythe",
            "shears", "wrench", "cutting", "vein", "harvest");

    private ToolLeveling() {
    }

    /**
     * True for a fully-assembled modular tool. The smithing recipes set
     * {@link AllDataComponents#BUILT} once the tool's textures are baked/positioned,
     * so partly-finished tools and vanilla items are excluded.
     */
    public static boolean isCompleteModular(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof IModularItem
                && stack.has(AllDataComponents.BUILT);
    }

    /** True for items that participate in the leveling system. */
    public static boolean isLevelable(ItemStack stack) {
        return Config.TOOL_LEVELING_ENABLED.get() && isCompleteModular(stack);
    }

    /** True for modular items whose intended function is breaking blocks. */
    public static boolean isMiningItem(ItemStack stack) {
        if (!(stack.getItem() instanceof IModularItem modular)) return false;
        ModularType type = modular.getModularTypeFromStack(stack);
        if (type == null || type.actions == null) return false;
        for (String action : type.actions) {
            if (MINING_ACTIONS.contains(action)) return true;
        }
        return false;
    }

    public static int getXp(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.TOOL_XP, 0);
    }

    public static void addXp(ItemStack stack, int amount) {
        if (amount <= 0 || !isLevelable(stack)) return;
        stack.set(ModDataComponents.TOOL_XP, getXp(stack) + amount);
    }

    /** XP required to advance from {@code level} to {@code level + 1}. */
    public static long xpForNextLevel(int level) {
        double base = Math.max(1.0, Config.TOOL_XP_BASE.get());
        double multiplier = Math.max(0.01, Config.TOOL_XP_MULTIPLIER.get());
        return (long) Math.ceil(base * Math.pow(multiplier, level - 1));
    }

    /** Total accumulated XP required to reach {@code level}. */
    public static long totalXpForLevel(int level) {
        long total = 0;
        for (int l = 1; l < level; l++) {
            total += xpForNextLevel(l);
            if (total < 0) return Long.MAX_VALUE; // overflow guard
        }
        return total;
    }

    /** Level for a total XP amount. Level 1 starts at 0 XP, capped at the configured max. */
    public static int levelForXp(long xp) {
        int max = Math.max(1, Config.TOOL_MAX_LEVEL.get());
        int level = 1;
        long remaining = xp;
        while (level < max) {
            long needed = xpForNextLevel(level);
            if (remaining < needed) break;
            remaining -= needed;
            level++;
        }
        return level;
    }

    public static int getLevel(ItemStack stack) {
        return levelForXp(getXp(stack));
    }

    /** XP accumulated within the current level (progress toward the next). */
    public static long xpIntoLevel(long xp) {
        return xp - totalXpForLevel(levelForXp(xp));
    }

    /**
     * Number of modifier slots available on the tool. One per level while leveling
     * is enabled, otherwise the configured fixed value.
     */
    public static int getModifierSlots(ItemStack stack) {
        if (!Config.TOOL_LEVELING_ENABLED.get()) {
            return Config.MODIFIER_SLOTS.get();
        }
        return getLevel(stack);
    }
}
