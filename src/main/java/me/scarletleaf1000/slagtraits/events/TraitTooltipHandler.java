package me.scarletleaf1000.slagtraits.events;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.Trait;
import me.scarletleaf1000.slagtraits.content.traits.data.TraitManager;
import me.scarletleaf1000.slagtraits.util.DisplayUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = SlagTraits.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TraitTooltipHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Trait> traits = TraitManager.getActiveTraits(stack);
        SlagTraits.LOGGER.debug("[TraitTooltip] item={} activeTraits={}", BuiltInRegistries.ITEM.getKey(stack.getItem()), traits.size());
        if (traits.isEmpty()) return;

        List<Component> tooltip = event.getToolTip();
        int insertAt = 1;

        if (Screen.hasAltDown()) {
            Component header = Component.translatable("tooltip.slagtraits.traits_header")
                    .withStyle(ChatFormatting.GRAY);
            tooltip.add(insertAt, header);

            int lineIndex = insertAt + 1;
            for (Trait trait : traits) {
                if (trait.isHidden()) continue;

                Component name = Component.literal(trait.getDisplayName());
                name = name.copy().withStyle(Style.EMPTY.withColor(trait.getColor()));

                tooltip.add(lineIndex, Component.literal("  ").append(name));
                lineIndex++;
            }
        } else {
            Component alt = Component.literal("Alt").withStyle(ChatFormatting.GRAY);
            Component prompt = Component.translatable("tooltip.slagtraits.hold_alt", alt)
                    .withStyle(ChatFormatting.DARK_GRAY);
            tooltip.add(insertAt, prompt);
        }
    }
}
