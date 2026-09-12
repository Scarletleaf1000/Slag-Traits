package me.scarletleaf1000.slagtraits.network;

import com.mojang.datafixers.types.Type;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.traits.data.MaterialTraitData;
import me.scarletleaf1000.slagtraits.traits.data.MaterialTraitManager;
import me.scarletleaf1000.slagtraits.traits.data.TraitDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncTraitsPayload(CompoundTag traits, CompoundTag materialTraits) implements CustomPacketPayload {
    public static final Type<SyncTraitsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "sync_traits"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncTraitsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.COMPOUND_TAG, SyncTraitsPayload::traits,
                    ByteBufCodecs.COMPOUND_TAG, SyncTraitsPayload::materialTraits,
                    SyncTraitsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static SyncTraitsPayload create() {
        CompoundTag traitsTag = new CompoundTag();
        TraitDataManager.getAll().forEach((id, trait) ->
                Trait.TRAIT_CODEC.encodeStart(NbtOps.INSTANCE, trait)
                        .result().ifPresent(t -> traitsTag.put(id.toString(), t)));

        CompoundTag matsTag = new CompoundTag();
        MaterialTraitManager.getAll().forEach((mat, refs) ->
                MaterialTraitData.TraitRef.CODEC.listOf().encodeStart(NbtOps.INSTANCE, refs)
                        .result().ifPresent(t -> matsTag.put(mat.toString(), t)));

        return new SyncTraitsPayload(traitsTag, matsTag);
    }

    public static void handle(SyncTraitsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Map<ResourceLocation, Trait> traits = new HashMap<>();
            for (String key : payload.traits().getAllKeys()) {
                Trait.TRAIT_CODEC.parse(NbtOps.INSTANCE, payload.traits().get(key))
                        .result().ifPresent(t -> traits.put(t.getId(), t));
            }
            TraitDataManager.setTraits(traits);

            Map<ResourceLocation, List<MaterialTraitData.TraitRef>> mats = new HashMap<>();
            for (String key : payload.materialTraits().getAllKeys()) {
                MaterialTraitData.TraitRef.CODEC.listOf().parse(NbtOps.INSTANCE, payload.materialTraits().get(key))
                        .result().ifPresent(refs -> mats.put(ResourceLocation.parse(key), refs));
            }
            MaterialTraitManager.setMaterialTraits(mats);
        });
    }
}
