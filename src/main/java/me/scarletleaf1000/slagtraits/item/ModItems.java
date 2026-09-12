package me.scarletleaf1000.slagtraits.item;

import me.scarletleaf1000.slagtraits.SlagTraits;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SlagTraits.MOD_ID);

    public static final DeferredItem<Item> MODIFIER_UPGRADE_SMITHING_TEMPLATE = ITEMS.registerSimpleItem("modifier_upgrade_smithing_template");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
