package me.scarletleaf1000.slagtraits.client;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.ActiveTrait;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.traits.resolver.TraitResolver;
import me.scarletleaf1000.slagtraits.util.DisplayUtils;
import net.minecraft.ChatFormatting;
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
        List<ActiveTrait> activeTraits = TraitResolver.getActiveTraits(stack);
        if (activeTraits.isEmpty()) return;

        List<Component> tooltip = event.getToolTip();
        int insertAt = 1;

        if (Screen.hasAltDown()) {
            Component header = Component.translatable("tooltip.slagtraits.traits_header")
                    .withStyle(ChatFormatting.GRAY);
            tooltip.add(insertAt, header);

            int lineIndex = insertAt + 1;
            for (ActiveTrait activeTrait : activeTraits) {
                Trait trait = activeTrait.trait();
                int tier = activeTrait.tier();
                if (trait.isHidden()) continue;

                Component name = Component.literal(trait.getDisplayName() + " " + DisplayUtils.intToRoman(tier));
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
