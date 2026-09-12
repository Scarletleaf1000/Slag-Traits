package me.scarletleaf1000.slagtraits.events;

import me.scarletleaf1000.slagtraits.traits.ActiveTrait;
import me.scarletleaf1000.slagtraits.traits.Trait;
import me.scarletleaf1000.slagtraits.traits.Trigger;
import me.scarletleaf1000.slagtraits.traits.resolver.TraitResolver;
import me.scarletleaf1000.slagtraits.traits.effect.TraitEffectRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TraitEventHandler {

    public TraitEventHandler() {
        NeoForge.EVENT_BUS.register(this);
    }

    // Called every tick for the player
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        dispatchForAllItems("on_tick", event.getEntity(), event);
    }

    @SubscribeEvent
    public void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            dispatch("on_attack_entity_pre", attacker, attacker.getMainHandItem(), event);
        }
        dispatchForArmor("on_hurt_pre", event.getEntity(), event);
    }

    @SubscribeEvent
    public void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            dispatch("on_attack_entity", attacker, attacker.getMainHandItem(), event);
        }
        dispatchForArmor("on_hurt", event.getEntity(), event);
    }

    // Breaking a block
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof LivingEntity player) {
            dispatch("on_block_break", player, player.getMainHandItem(), event);
        }
    }

    // Using an item
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        dispatch("on_item_use", event.getEntity(), event.getItemStack(), event);
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getPlayer();
        if (player == null) return;
        dispatch("on_break_speed", player, player.getMainHandItem(), event);
    }

    @SubscribeEvent
    public void onPickupXp(PlayerXpEvent.PickupXp event) {
        Player player = event.getPlayer();
        if (player == null) return;
        dispatchForArmor("on_pickup_xp", player, event);
    }

    // Central dispatch
    private void dispatch(String eventId, LivingEntity holder, ItemStack tool, Object event) {
        if (tool.isEmpty()) return;
        if (holder.level().isClientSide()) return;

        List<ActiveTrait> activeTraits = TraitResolver.getActiveTraits(tool);
        if (activeTraits.isEmpty()) return;

        for (ActiveTrait activeTrait : activeTraits) {
            Trait trait = activeTrait.trait();
            int tier = activeTrait.tier();
            for (Trigger trigger : trait.getTriggers()) {
                if (!trigger.getEvent().equalsIgnoreCase(eventId)) continue;
                if (ThreadLocalRandom.current().nextFloat() < trigger.getChance()){
                    if (TraitResolver.test(trigger.getCondition(), holder, tool, event)) {
                        TraitEffectRegistry.apply(trigger.getEffect(), holder, tool, event, tier);
                    }
                }
            }
        }
    }

    private void dispatchForAllEquipment(String eventId, LivingEntity holder, Object event) {
        dispatch(eventId, holder, holder.getItemBySlot(EquipmentSlot.MAINHAND), event);
        dispatch(eventId, holder, holder.getItemBySlot(EquipmentSlot.OFFHAND), event);
        dispatchForArmor(eventId, holder, event);
    }

    private void dispatchForArmor(String eventId, LivingEntity holder, Object event) {
        for (ItemStack armorStack : holder.getArmorSlots()) {
            dispatch(eventId, holder, armorStack, event);
        }
    }

    private void dispatchForAllItems(String eventId, LivingEntity holder, Object event) {
        if (holder instanceof Player p) {
            for (ItemStack stack : p.getInventory().items) {
                dispatch(eventId, holder, stack, event);
            }
            return;
        }
        dispatchForAllEquipment(eventId, holder, event);
    }
}
