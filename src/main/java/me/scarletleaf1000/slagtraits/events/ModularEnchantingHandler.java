package me.scarletleaf1000.slagtraits.events;

import dev.lopyluna.slag.content.items.dynamic_part.IModularItem;
import me.scarletleaf1000.slagtraits.Config;
import me.scarletleaf1000.slagtraits.SlagTraits;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;

@EventBusSubscriber(modid = SlagTraits.MOD_ID)
public final class ModularEnchantingHandler {
    private ModularEnchantingHandler() {
    }

    @SubscribeEvent
    public static void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        if (!Config.MODULAR_TOOLS_ENCHANTABLE.get()
                && event.getItem().getItem() instanceof IModularItem) {
            event.setEnchantLevel(0);
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!Config.MODULAR_TOOLS_ENCHANTABLE.get()
                && event.getLeft().getItem() instanceof IModularItem
                && !event.getRight().getEnchantments().isEmpty()) {
            event.setCanceled(true);
        }
    }
}
