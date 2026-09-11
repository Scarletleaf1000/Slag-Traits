package me.scarletleaf1000.slagtraits.events;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.Trait;
import me.scarletleaf1000.slagtraits.content.traits.data.TraitManager;
import me.scarletleaf1000.slagtraits.util.DisplayUtils;
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
        if (traits.isEmpty()) return;

        for (Trait trait : traits) {
            if (trait.isHidden()) continue;
            Component line = Component.literal(trait.getDisplayName())
                    .withStyle(Style.EMPTY.withColor(trait.getColor()));
            event.getToolTip().add(line);
        }
    }
}
