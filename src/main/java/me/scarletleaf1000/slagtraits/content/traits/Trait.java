package me.scarletleaf1000.slagtraits.content.traits;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Trait {

    //for modifier traits
    private final boolean modifier;
    private final int baseCostPerLevel;
    private final float scalingMultiplier;

    //general variables
    private final ResourceLocation id;
    private final String displayName;
    private final String description;
    private final EquipmentType equipmentType;
    private final int maxLevel;

    private final List<Trigger> triggers;

    //optional
    private final boolean hidden;
    private final int color;
    private final Set<String> exclusiveWith;

    public Trait(ResourceLocation id, String displayName, String description,
                 EquipmentType type, int maxLevel, List<Trigger> triggers) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.equipmentType = type;
        this.maxLevel = maxLevel;
        this.triggers = triggers != null ? triggers : new ArrayList<>();

        this.modifier = false;
        this.baseCostPerLevel = 0;
        this.scalingMultiplier = 1f;

        this.hidden = false;
        this.color = 0xFFFFFF;
        this.exclusiveWith = Set.of();
    }

    public Trait(ResourceLocation id, String displayName, String description,
                 EquipmentType type, int maxLevel, List<Trigger> triggers,
                 boolean hidden, int color, Set<String> exclusiveWith) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.equipmentType = type;
        this.maxLevel = maxLevel;
        this.triggers = triggers != null ? triggers : new ArrayList<>();

        this.modifier = false;
        this.baseCostPerLevel = 0;
        this.scalingMultiplier = 1f;

        this.hidden = hidden;
        this.color = color;
        this.exclusiveWith = exclusiveWith != null ? exclusiveWith : Set.of();
    }

    public Trait(ResourceLocation id, String displayName, String description, EquipmentType type,
                 int maxLevel, int baseCostPerLevel, float scalingMultiplier,
                 List<Trigger> triggers, boolean hidden, int color, Set<String> exclusiveWith) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.equipmentType = type;
        this.maxLevel = maxLevel;
        this.triggers = triggers != null ? triggers : new ArrayList<>();

        this.modifier = true;
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

    public String getId() {
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
