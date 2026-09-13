package me.scarletleaf1000.slagtraits.traits.data;

import com.mojang.serialization.Codec;
import me.scarletleaf1000.slagtraits.SlagTraits;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(SlagTraits.MOD_ID);

    public static final Supplier<DataComponentType<AppliedModifiers>> MODIFIERS =
            DATA_COMPONENTS.registerComponentType("modifiers", builder -> builder
                    .persistent(AppliedModifiers.CODEC)
                    .networkSynchronized(AppliedModifiers.STREAM_CODEC));

    public static final Supplier<DataComponentType<Integer>> TOOL_XP =
            DATA_COMPONENTS.registerComponentType("tool_xp", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
