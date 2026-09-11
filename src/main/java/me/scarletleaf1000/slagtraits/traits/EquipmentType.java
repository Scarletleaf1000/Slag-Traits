package me.scarletleaf1000.slagtraits.traits;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum EquipmentType implements StringRepresentable {
    TOOL("tool"),
    ARMOR("armor"),
    BOTH("both");

    private final String name;

    public static final Codec<EquipmentType> CODEC =
            StringRepresentable.fromEnum(EquipmentType::values);

    EquipmentType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
