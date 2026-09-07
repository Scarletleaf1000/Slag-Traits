package me.scarletleaf1000.slagtraits.network;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.register.AllMaterialTraits;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SlagTraits.MOD_ID)
public class TraitSyncEvents {
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        var payload = new SyncMaterialTraitsS2C(AllMaterialTraits.snapshot());
        if (event.getPlayer() != null) {
            PacketDistributor.sendToPlayer(event.getPlayer(), payload);
        } else {
            PacketDistributor.sendToAllPlayers(payload);
        }
    }
}
