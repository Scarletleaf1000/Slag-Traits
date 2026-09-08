package me.scarletleaf1000.slagtraits.content.traits;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

/**
 * Contract for a material trait. Every hook is a default no-op so a trait
 * only overrides what it uses. The {@code stack} parameter is always the
 * modular ItemStack the trait was resolved from; {@code tier} is the
 * per-instance tier value from the material's JSON entry.
 */
public interface ISlagTrait {

    // ---- Mining / block interaction ----

    /** Fires while the player is breaking a block. Modify via event.setNewSpeed(). */
    default void onBreakSpeed(PlayerEvent.BreakSpeed event, ItemStack stack, int tier) {}

    /** Fires when checking whether the held item can harvest a block. */
    default void onHarvestCheck(PlayerEvent.HarvestCheck event, ItemStack stack, int tier) {}

    /** Fires after the player successfully breaks a block. */
    default void onBlockBreak(BlockEvent.BreakEvent event, ItemStack stack, int tier) {}

    // ---- Combat (attacker side) ----

    /** Fires when the holder damages another entity. Adjust event.getNewDamage()/setNewDamage(). */
    default void onDamageDealt(LivingDamageEvent.Pre event, ItemStack stack, int tier) {}

    /** Fires when the holder lands a critical hit. */
    default void onCriticalHit(CriticalHitEvent event, ItemStack stack, int tier) {}

    /** Fires when the holder kills an entity. */
    default void onKill(LivingDeathEvent event, ItemStack stack, int tier) {}

    /** Fires when the holder releases an arrow from a bow/crossbow. */
    default void onArrowLoose(ArrowLooseEvent event, ItemStack stack, int tier) {}

    // ---- Combat (defender side) ----

    /** Fires when the holder is about to take damage (before armor/reductions). */
    default void onDamageIncoming(LivingIncomingDamageEvent event, ItemStack stack, int tier) {}

    /** Fires when the holder takes damage (after reductions). Adjust event.getNewDamage(). */
    default void onDamageTaken(LivingDamageEvent.Pre event, ItemStack stack, int tier) {}

    /** Fires when the holder dies. */
    default void onDeath(LivingDeathEvent event, ItemStack stack, int tier) {}

    // ---- Item use / interaction ----

    /** Fires when the holder right-clicks with the item. */
    default void onRightClickItem(PlayerInteractEvent.RightClickItem event, ItemStack stack, int tier) {}

    /** Fires when the holder right-clicks a block with the item. */
    default void onRightClickBlock(PlayerInteractEvent.RightClickBlock event, ItemStack stack, int tier) {}

    /** Fires when the holder left-clicks a block with the item. */
    default void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, ItemStack stack, int tier) {}

    /** Fires when the holder right-clicks an entity with the item. */
    default void onEntityInteract(PlayerInteractEvent.EntityInteract event, ItemStack stack, int tier) {}

    /** Fires while the holder is actively using the item (bow draw, eating, etc.). */
    default void onItemUseTick(LivingEntityUseItemEvent.Tick event, ItemStack stack, int tier) {}

    /** Fires when the holder finishes using the item. */
    default void onItemUseFinish(LivingEntityUseItemEvent.Finish event, ItemStack stack, int tier) {}

    // ---- Lifecycle / passive ----

    /** Fires every player tick while the item is equipped/held. Keep this cheap. */
    default void onPlayerTick(PlayerTickEvent.Post event, ItemStack stack, int tier) {}

    /** Fires when the item is equipped or unequipped (armor slot or hand). */
    default void onEquipmentChange(LivingEquipmentChangeEvent event, ItemStack stack, int tier) {}

    /** Fires when the holder picks up XP. */
    default void onXpPickup(PlayerXpEvent.PickupXp event, ItemStack stack, int tier) {}

    // ---- Display (client only) ----

    /**
     * The one-line summary shown in the grouped "Traits:" tooltip section,
     * e.g. "Reinforced III". Override with a translatable component.
     */
    default Component getTooltipLine(int tier) {
        return Component.translatable("trait.slagtraits.missing", tier);
    }

    /** Fires when building the item's tooltip, after the grouped trait lines. Use for extra detail lines. Client-side only. */
    default void appendTooltip(ItemTooltipEvent event, ItemStack stack, List<Component> tooltip, int tier) {}
}
