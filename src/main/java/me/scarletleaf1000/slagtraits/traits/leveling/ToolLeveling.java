package me.scarletleaf1000.slagtraits.traits.leveling;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import dev.lopyluna.slag.content.types.ModularType;
import me.scarletleaf1000.slagtraits.Config;
import me.scarletleaf1000.slagtraits.traits.EquipmentType;
import me.scarletleaf1000.slagtraits.traits.data.ModDataComponents;
import me.scarletleaf1000.slagtraits.integration.EquipmentClassifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;

import java.util.Set;

public final class ToolLeveling {
    private static final int MAX_LEVEL = 10000;

    private static final Set<String> MINING_ACTIONS = Set.of(
            "pickaxe", "axe", "shovel", "hoe", "hammer", "scythe",
            "shears", "wrench", "cutting", "vein", "harvest");

    private ToolLeveling() {
    }

    /** True for any item that can gain XP and has a level (tools, weapons, armor). */
    public static boolean isLevelable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return EquipmentClassifier.getEquipmentType(stack) != EquipmentType.NONE;
    }

    /** True for items whose intended function is breaking blocks. */
    public static boolean isMiningItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof IModularItem modular) {
            ModularType type = modular.getModularTypeFromStack(stack);
            if (type == null || type.actions == null) return false;
            for (String action : type.actions) {
                if (MINING_ACTIONS.contains(action)) return true;
            }
            return false;
        }
        return stack.getItem() instanceof DiggerItem
                || stack.getItem() instanceof ShearsItem
                || stack.has(DataComponents.TOOL);
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

    /** Level for a total XP amount. Level 1 starts at 0 XP. */
    public static int levelForXp(long xp) {
        int level = 1;
        long remaining = xp;
        while (level < MAX_LEVEL) {
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

    /** Number of modifier slots available on the tool (one per level). */
    public static int getModifierSlots(ItemStack stack) {
        return getLevel(stack);
    }
}
