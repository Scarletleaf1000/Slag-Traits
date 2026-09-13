package me.scarletleaf1000.slagtraits;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = SlagTraits.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue PART_SWAP_BREAK_CHANCE = BUILDER
            .comment("Chance that a removed modular tool part breaks instead of dropping when swapped at a smithing table.")
            .defineInRange("partSwapBreakChance", 0.2, 0.0, 1.0);

    public static final ModConfigSpec.BooleanValue MODULAR_TOOLS_ENCHANTABLE = BUILDER
            .comment("Whether modular tools can receive enchantments.")
            .define("modularToolsEnchantable", false);

    public static final ModConfigSpec.DoubleValue TOOL_XP_BASE = BUILDER
            .comment("Base XP cost to go from level 1 to level 2.",
                    "XP needed to go from level L to L+1 = toolXpBase * toolXpMultiplier^(L-1).")
            .defineInRange("toolXpBase", 100.0, 1.0, 1.0e9);

    public static final ModConfigSpec.DoubleValue TOOL_XP_MULTIPLIER = BUILDER
            .comment("Multiplier applied to the XP cost of each successive tool level.")
            .defineInRange("toolXpMultiplier", 2.0, 0.01, 1000.0);

    public static final ModConfigSpec.BooleanValue TOOL_LEVELING_ENABLED = BUILDER
            .comment("Whether tools gain XP and level up. When disabled, tools have no level",
                    "and every tool gets a fixed number of modifier slots (modifierSlots).")
            .define("toolLevelingEnabled", true);

    public static final ModConfigSpec.IntValue TOOL_MAX_LEVEL = BUILDER
            .comment("Maximum level a tool can reach.")
            .defineInRange("toolMaxLevel", 10, 1, 10000);

    public static final ModConfigSpec.IntValue MODIFIER_SLOTS = BUILDER
            .comment("Fixed number of modifier slots every tool gets when toolLevelingEnabled is false.")
            .defineInRange("modifierSlots", 3, 0, 1000);

    public static final ModConfigSpec.IntValue TOOL_XP_PER_BLOCK = BUILDER
            .comment("XP granted to a mining tool each time it breaks a block.")
            .defineInRange("toolXpPerBlock", 1, 0, 100000);

    public static final ModConfigSpec.DoubleValue TOOL_XP_PER_DAMAGE = BUILDER
            .comment("XP granted to a weapon per point of damage dealt.")
            .defineInRange("toolXpPerDamage", 1.0, 0.0, 10000.0);

    public static final ModConfigSpec.DoubleValue TOOL_XP_PER_ABSORBED = BUILDER
            .comment("XP granted to each worn armor piece per point of damage taken.")
            .defineInRange("toolXpPerAbsorbed", 1.0, 0.0, 10000.0);

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {

    }
}
