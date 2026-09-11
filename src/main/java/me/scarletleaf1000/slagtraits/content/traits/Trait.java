package me.scarletleaf1000.slagtraits.content.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trait {
    //general variables
    private final ResourceLocation id;
    private final String displayName;
    private final String description;
    private final EquipmentType equipmentType;
    private final int maxLevel;

    private final List<Trigger> triggers;

    //for modifier traits
    private final boolean modifier;
    private final int baseCostPerLevel;
    private final float scalingMultiplier;

    //optional
    private final boolean hidden;
    private final int color;
    private final Set<String> exclusiveWith;

    public static final Codec<List<Trigger>> TRIGGER_LIST_CODEC = Trigger.TRIGGER_CODEC.listOf();
    public static final Codec<Set<String>> STRING_SET_CODEC =
            Codec.STRING.listOf().xmap(
                    list -> new HashSet<>(list),
                    set -> new ArrayList<>(set)
            );

    public static final Codec<Trait> TRAIT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(Trait::getId),
                    Codec.STRING.fieldOf("display_name").forGetter(Trait::getDisplayName),
                    Codec.STRING.fieldOf("description").forGetter(Trait::getDescription),
                    EquipmentType.CODEC.fieldOf("equipment_type").forGetter(Trait::getEquipmentType),
                    Codec.INT.optionalFieldOf("max_level", 255).forGetter(Trait::getMaxLevel),
                    Codec.BOOL.optionalFieldOf("modifier", false).forGetter(Trait::isModifier),
                    Codec.INT.optionalFieldOf("base_cost", 0).forGetter(Trait::getBaseCostPerLevel),
                    Codec.FLOAT.optionalFieldOf("scaling_multiplier", 1f).forGetter(Trait::getScalingMultiplier),
                    TRIGGER_LIST_CODEC.fieldOf("triggers").forGetter(Trait::getTriggers),
                    Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Trait::isHidden),
                    Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(Trait::getColor),
                    STRING_SET_CODEC.optionalFieldOf("exclusive_with", new HashSet<>())
                            .forGetter(Trait::getExclusiveWith)
            ).apply(instance, Trait::new));

    public Trait(ResourceLocation id, String displayName, String description, EquipmentType type,
                 int maxLevel, boolean modifier, int baseCostPerLevel, float scalingMultiplier,
                 List<Trigger> triggers, boolean hidden, int color, Set<String> exclusiveWith) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.equipmentType = type;
        this.maxLevel = maxLevel;
        this.triggers = triggers != null ? triggers : new ArrayList<>();

        this.modifier = modifier;
        this.baseCostPerLevel = baseCostPerLevel;
        this.scalingMultiplier = scalingMultiplier;

        this.hidden = hidden;
        this.color = color;
        this.exclusiveWith = exclusiveWith != null ? exclusiveWith : Set.of();
    }

    public boolean isModifier() {
        return modifier;
    }

    public int getBaseCostPerLevel() {
        return baseCostPerLevel;
    }

    public float getScalingMultiplier() {
        return scalingMultiplier;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getColor() {
        return color;
    }

    public Set<String> getExclusiveWith() {
        return exclusiveWith;
    }
}
