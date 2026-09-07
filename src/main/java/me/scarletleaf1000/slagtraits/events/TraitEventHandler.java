package me.scarletleaf1000.slagtraits.events;

import com.mojang.datafixers.util.Pair;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.content.traits.ISlagTrait;
import me.scarletleaf1000.slagtraits.util.TraitUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.function.BiConsumer;

@EventBusSubscriber(modid = SlagTraits.MOD_ID)
public class TraitEventHandler {

    /** Calls {@code action} once per (trait, stack) pair found on the entity's held item. */
    private static void forHeldItem(LivingEntity entity, BiConsumer<ISlagTrait, HeldContext> action) {
        ItemStack stack = entity.getMainHandItem();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            action.accept(pair.getFirst(), new HeldContext(stack, pair.getSecond()));
    }

    /** Calls {@code action} once per (trait, stack) pair across ALL equipment slots (armor + hands). */
    private static void forAllEquipment(LivingEntity entity, BiConsumer<ISlagTrait, HeldContext> action) {
        for (ItemStack stack : entity.getAllSlots())
            for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
                action.accept(pair.getFirst(), new HeldContext(stack, pair.getSecond()));
    }

    /** The stack a trait was resolved from plus its modifier, so lambdas stay readable. */
    private record HeldContext(ItemStack stack, float modifier) {}

    // ---- Mining / block interaction ----

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        forHeldItem(event.getEntity(), (trait, ctx) ->
                trait.onBreakSpeed(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        forHeldItem(event.getEntity(), (trait, ctx) ->
                trait.onHarvestCheck(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        forHeldItem(event.getPlayer(), (trait, ctx) ->
                trait.onBlockBreak(event, ctx.stack(), ctx.modifier()));
    }

    // ---- Combat (attacker side) ----

    @SubscribeEvent
    public static void onDamageDealt(LivingDamageEvent.Pre event) {
        // damage events fire for the VICTIM — the attacker is the damage source entity:
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        forHeldItem(attacker, (trait, ctx) ->
                trait.onDamageDealt(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        forHeldItem(event.getEntity(), (trait, ctx) ->
                trait.onCriticalHit(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;
        forHeldItem(killer, (trait, ctx) ->
                trait.onKill(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        ItemStack bow = event.getBow();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(bow))
            pair.getFirst().onArrowLoose(event, bow, pair.getSecond());
    }

    // ---- Combat (defender side) — check all equipment, not just the hand ----

    @SubscribeEvent
    public static void onDamageIncoming(LivingIncomingDamageEvent event) {
        forAllEquipment(event.getEntity(), (trait, ctx) ->
                trait.onDamageIncoming(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent.Pre event) {
        forAllEquipment(event.getEntity(), (trait, ctx) ->
                trait.onDamageTaken(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        forAllEquipment(event.getEntity(), (trait, ctx) ->
                trait.onDeath(event, ctx.stack(), ctx.modifier()));
    }

    // ---- Item use / interaction ----

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            pair.getFirst().onRightClickItem(event, stack, pair.getSecond());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            pair.getFirst().onRightClickBlock(event, stack, pair.getSecond());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack stack = event.getItemStack();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            pair.getFirst().onLeftClickBlock(event, stack, pair.getSecond());
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            pair.getFirst().onEntityInteract(event, stack, pair.getSecond());
    }

    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
        ItemStack stack = event.getItem();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            pair.getFirst().onItemUseTick(event, stack, pair.getSecond());
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            pair.getFirst().onItemUseFinish(event, stack, pair.getSecond());
    }

    // ---- Lifecycle / passive ----

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        forHeldItem(event.getEntity(), (trait, ctx) ->
                trait.onPlayerTick(event, ctx.stack(), ctx.modifier()));
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        // only the newly equipped stack — traits on the removed item shouldn't fire "equip" logic
        ItemStack stack = event.getTo();
        for (Pair<ISlagTrait, Float> pair : TraitUtils.getTraits(stack))
            pair.getFirst().onEquipmentChange(event, stack, pair.getSecond());
    }

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        forHeldItem(event.getEntity(), (trait, ctx) ->
                trait.onXpPickup(event, ctx.stack(), ctx.modifier()));
    }
}
