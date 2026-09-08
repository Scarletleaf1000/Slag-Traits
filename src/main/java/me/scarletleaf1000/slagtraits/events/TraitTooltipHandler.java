package me.scarletleaf1000.slagtraits.events;

import com.mojang.datafixers.util.Pair;
import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.util.TraitUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = SlagTraits.MOD_ID, value = Dist.CLIENT)
public class TraitTooltipHandler {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof IModularItem)) return;

        List<Pair<ISlagTrait, Float>> traits = TraitUtils.getTraits(stack);
        if (traits.isEmpty()) return;

        List<Component> tooltip = event.getToolTip();
        List<Component> lines = new ArrayList<>();

        if (!Screen.hasAltDown()) {
            lines.add(Component.translatable("tooltip.slagtraits.hold_alt",
                            Component.literal("Alt").withStyle(ChatFormatting.GRAY))
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            lines.add(Component.translatable("tooltip.slagtraits.traits_header")
                    .withStyle(ChatFormatting.GOLD));
            for (var pair : traits) {
                lines.add(Component.literal("  ")
                        .append(pair.getFirst().getTooltipLine(pair.getSecond())));
            }
        }

        // Insert right after the item name (index 0) so the line sits with
        // the other hold-key hints, independent of what slag adds.
        tooltip.addAll(Math.min(1, tooltip.size()), lines);

        if (Screen.hasAltDown()) {
            for (var pair : traits)
                pair.getFirst().appendTooltip(event, stack, tooltip, pair.getSecond());
        }
    }
}
