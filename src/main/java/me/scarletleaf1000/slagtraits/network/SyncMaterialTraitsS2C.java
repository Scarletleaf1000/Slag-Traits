package me.scarletleaf1000.slagtraits.network;

import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.TraitInstance;
import me.scarletleaf1000.slagtraits.register.AllMaterialTraits;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncMaterialTraitsS2C(Map<ResourceLocation, List<TraitInstance>> traits)
        implements CustomPacketPayload {

    public static final Type<SyncMaterialTraitsS2C> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "sync_material_traits"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncMaterialTraitsS2C> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC,
                            TraitInstance.STREAM_CODEC.apply(ByteBufCodecs.list())),
                    SyncMaterialTraitsS2C::traits,
                    SyncMaterialTraitsS2C::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncMaterialTraitsS2C payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> AllMaterialTraits.setAllTraits(payload.traits()));
    }
}
