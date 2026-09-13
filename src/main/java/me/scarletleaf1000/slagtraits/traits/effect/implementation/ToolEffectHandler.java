package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

/**
 * Handles tool-related effects.
 *
 * Params:
 *   speed_multiplier / speed_multiplier_per_tier   (on_break_speed)
 *   auto_smelt                                     (on_block_drops, boolean)
 *   silk_touch                                     (on_block_drops, boolean)
 *   fortune_level / fortune_level_per_tier         (on_block_drops)
 *   repair_on_break / repair_on_break_per_tier     (on_block_break)
 *   repair_on_kill / repair_on_kill_per_tier       (on_kill)
 */
public class ToolEffectHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        if (event instanceof PlayerEvent.BreakSpeed breakSpeed) {
            float mult = effect.getScaledFloat("speed_multiplier", "speed_multiplier_per_tier", tier, 0f);
            if (mult != 0f) {
                breakSpeed.setNewSpeed(Math.max(0f, breakSpeed.getNewSpeed() * (1f + mult)));
            }
        } else if (event instanceof BlockDropsEvent drops) {
            handleDrops(drops, effect, tier);
        } else if (event instanceof BlockEvent.BreakEvent) {
            repair(tool, effect.getScaledInt("repair_on_break", "repair_on_break_per_tier", tier, 0));
        } else if (event instanceof LivingDeathEvent) {
            repair(tool, effect.getScaledInt("repair_on_kill", "repair_on_kill_per_tier", tier, 0));
        }
    }

    private static void handleDrops(BlockDropsEvent drops, TraitEffect effect, int tier) {
        if (!(drops.getLevel() instanceof ServerLevel level)) return;

        if (effect.getBoolean("silk_touch", false)) {
            ItemStack blockItem = new ItemStack(drops.getState().getBlock());
            if (!blockItem.isEmpty()) {
                drops.getDrops().clear();
                drops.getDrops().add(new ItemEntity(level,
                        drops.getPos().getX() + 0.5, drops.getPos().getY() + 0.5, drops.getPos().getZ() + 0.5,
                        blockItem));
            }
            return;
        }

        int fortuneLevel = effect.getScaledInt("fortune_level", "fortune_level_per_tier", tier, 0);
        if (fortuneLevel > 0) {
            ItemStack fortuneTool = drops.getTool().copy();
            var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            fortuneTool.enchant(enchantments.getOrThrow(Enchantments.FORTUNE), fortuneLevel);
            List<ItemStack> fortuneDrops = Block.getDrops(drops.getState(), level, drops.getPos(),
                    drops.getBlockEntity(), drops.getBreaker(), fortuneTool);

            drops.getDrops().clear();
            for (ItemStack stack : fortuneDrops) {
                drops.getDrops().add(new ItemEntity(level,
                        drops.getPos().getX() + 0.5, drops.getPos().getY() + 0.5, drops.getPos().getZ() + 0.5,
                        stack));
            }
        }

        if (effect.getBoolean("auto_smelt", false)) {
            var recipeManager = level.getRecipeManager();
            for (ItemEntity drop : drops.getDrops()) {
                ItemStack stack = drop.getItem();
                var recipe = recipeManager.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);
                if (recipe.isPresent()) {
                    ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
                    if (!result.isEmpty()) {
                        result.setCount(result.getCount() * stack.getCount());
                        drop.setItem(result);
                    }
                }
            }
        }
    }

    private static void repair(ItemStack tool, int amount) {
        if (amount <= 0 || tool == null || !tool.isDamageableItem() || !tool.isDamaged()) return;
        tool.setDamageValue(Math.max(0, tool.getDamageValue() - amount));
    }
}
