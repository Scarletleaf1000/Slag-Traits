package me.scarletleaf1000.slagtraits.traits.effect;

import me.scarletleaf1000.slagtraits.SlagTraits;
import net.minecraft.resources.ResourceLocation;

public class TraitEffects {
    public static final ResourceLocation REPAIR_ITEM = ResourceLocation.fromNamespaceAndPath(SlagTraits.MOD_ID, "repair_item");

    public static void register() {
        TraitEffectRegistry.register(REPAIR_ITEM, ((holder, tool, effect, event) -> {
            int amount = effect.getInt("amount", 1);
            if (!tool.isDamageableItem()) return;
            if (tool.isDamaged()) {
                int newDamage = Math.max(0, tool.getDamageValue() - amount);
                tool.setDamageValue(newDamage);
            }
        }));
    }
}
