package me.scarletleaf1000.slagtraits.traits;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;

public class Trigger {
    private final String event;
    private final String condition;
    private final float chance;
    private final TraitEffect effect;

    public static final Codec<Trigger> TRIGGER_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("event").forGetter(Trigger::getEvent),
                    Codec.STRING.fieldOf("condition").forGetter(Trigger::getCondition),
                    Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(Trigger::getChance),
                    TraitEffect.EFFECT_CODEC.fieldOf("effect").forGetter(Trigger::getEffect)
            ).apply(instance, Trigger::new));

    public Trigger(String event, String condition, float chance, TraitEffect effect) {
        this.event = event;
        this.condition = condition;
        this.chance = chance;
        this.effect = effect;
    }

    public String getEvent() {
        return event;
    }

    public String getCondition() {
        return condition;
    }

    public float getChance() {
        return chance;
    }

    public TraitEffect getEffect() {
        return effect;
    }
}
