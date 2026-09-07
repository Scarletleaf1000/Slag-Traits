package me.scarletleaf1000.slagtraits.events;

import com.mojang.datafixers.util.Pair;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.util.TraitUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = SlagTraits.MOD_ID, value = Dist.CLIENT)
public class TraitTooltipHandler {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Pair<ISlagTrait, Float>> traits = TraitUtils.getTraits(stack);
        if (traits.isEmpty()) return;

        List<Component> tooltip = event.getToolTip();
        tooltip.add(Component.translatable("tooltip.slagtraits.traits_header")
                .withStyle(ChatFormatting.GOLD));
        for (var pair : traits) {
            tooltip.add(Component.literal("  ")
                    .append(pair.getFirst().getTooltipLine(pair.getSecond())));
            pair.getFirst().appendTooltip(event, stack, tooltip, pair.getSecond());
        }
    }
}
