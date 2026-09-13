package me.scarletleaf1000.slagtraits.client;

import com.mojang.datafixers.util.Either;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.leveling.ToolLeveling;
import me.scarletleaf1000.slagtraits.traits.leveling.ToolXpTooltipComponent;
import me.scarletleaf1000.slagtraits.util.DisplayUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = SlagTraits.MOD_ID, value = Dist.CLIENT)
public class ToolXpTooltipHandler {

    /** Registers the client-side renderer for the XP bar tooltip component. */
    @EventBusSubscriber(modid = SlagTraits.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(ToolXpTooltipComponent.class, ClientToolXpTooltipComponent::new);
        }
    }

    @SubscribeEvent
    public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (!ToolLeveling.isLevelable(stack)) return;

        List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
        if (elements.isEmpty()) return;

        int level = ToolLeveling.getLevel(stack);
        long xp = ToolLeveling.getXp(stack);
        long intoLevel = ToolLeveling.xpIntoLevel(xp);
        long needed = ToolLeveling.xpForNextLevel(level);

        Component levelText = Component.translatable("tooltip.slagtraits.level",
                        DisplayUtils.intToRoman(level))
                .withStyle(ChatFormatting.YELLOW);

        // Insert directly under the item name (index 0), above traits/stats.
        elements.add(1, Either.left(levelText));
        elements.add(2, Either.right(new ToolXpTooltipComponent(intoLevel, needed)));
    }
}
