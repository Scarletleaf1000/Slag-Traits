package me.scarletleaf1000.slagtraits.content.traits;

public class Trigger {
    private final String event;
    private final String condition;
    private final float chance;
    private final TraitEffect effect;

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
