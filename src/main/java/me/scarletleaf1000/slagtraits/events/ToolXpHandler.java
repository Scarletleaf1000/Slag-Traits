package me.scarletleaf1000.slagtraits.events;

import me.scarletleaf1000.slagtraits.Config;
import me.scarletleaf1000.slagtraits.SlagTraits;
import me.scarletleaf1000.slagtraits.traits.EquipmentType;
import me.scarletleaf1000.slagtraits.traits.leveling.ToolLeveling;
import me.scarletleaf1000.slagtraits.integration.EquipmentClassifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = SlagTraits.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ToolXpHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || event.getLevel().isClientSide()) return;
        ItemStack tool = event.getPlayer().getMainHandItem();
        if (ToolLeveling.isMiningItem(tool)) {
            ToolLeveling.addXp(tool, Config.TOOL_XP_PER_BLOCK.get());
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;

        // Weapons gain XP for damage dealt.
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            EquipmentType type = EquipmentClassifier.getEquipmentType(weapon);
            if (type == EquipmentType.TOOL || type == EquipmentType.BOTH) {
                int xp = Mth.ceil(event.getNewDamage() * Config.TOOL_XP_PER_DAMAGE.get());
                ToolLeveling.addXp(weapon, xp);
            }
        }

        // Armor gains XP for damage taken while worn.
        int xp = Mth.ceil(event.getNewDamage() * Config.TOOL_XP_PER_ABSORBED.get());
        if (xp <= 0) return;
        for (ItemStack armor : event.getEntity().getArmorSlots()) {
            EquipmentType type = EquipmentClassifier.getEquipmentType(armor);
            if (type == EquipmentType.ARMOR || type == EquipmentType.BOTH) {
                ToolLeveling.addXp(armor, xp);
            }
        }
    }
}
