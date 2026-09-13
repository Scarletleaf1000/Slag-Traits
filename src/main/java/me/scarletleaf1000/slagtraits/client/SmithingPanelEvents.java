package me.scarletleaf1000.slagtraits.client;

import me.scarletleaf1000.slagtraits.SlagTraits;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = SlagTraits.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class SmithingPanelEvents {
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof SmithingScreen screen) {
            event.addListener(new SmithingInfoPanel(screen));
        }
    }
}
