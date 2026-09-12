package me.scarletleaf1000.slagtraits.traits.effect.implementation;

import me.scarletleaf1000.slagtraits.traits.effect.TraitEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ScaleStatHandler {

    public static void handle(LivingEntity holder, ItemStack tool, TraitEffect effect, Object event, int tier) {
        String stat = effect.getString("stat");
        String mode = effect.getString("mode");
        float perTier = effect.getFloat("per_tier", 0f);
        float base = effect.getFloat("base", 0f);

        float scale = computeScale(mode, holder, tool, effect, event);
        float bonus = base + tier * perTier * scale;

        if (bonus == 0f || scale == 0f) return;

        if ("break_speed".equals(stat) && event instanceof PlayerEvent.BreakSpeed breakSpeed) {
            float newSpeed = breakSpeed.getNewSpeed() * (1f + bonus);
            breakSpeed.setNewSpeed(Math.max(0.0f, newSpeed));
        } else if ("attack_damage".equals(stat) && event instanceof LivingDamageEvent.Pre damagePre) {
            // Only apply when the holder is the one dealing damage
            if (damagePre.getSource() == null || damagePre.getSource().getEntity() != holder) return;
            float newDamage = damagePre.getNewDamage() * (1f + bonus);
            damagePre.setNewDamage(Math.max(0.0f, newDamage));
        }
    }

    private static float computeScale(String mode, LivingEntity holder, ItemStack tool, TraitEffect effect, Object event) {
        if (mode == null || holder == null) return 0f;
        Level level = holder.level();

        return switch (mode) {
            // --- Environment / position ---
            case "y_level" -> {
                int minY = effect.getInt("min_y", -64);
                int maxY = effect.getInt("max_y", 128);
                int y = holder.blockPosition().getY();
                float t = Mth.inverseLerp(y, minY, maxY);
                // lower y -> higher scale, clamped
                yield Mth.clamp(1f - t, 0f, 1f);
            }
            case "underground" -> level.canSeeSky(holder.blockPosition()) ? 0f : 1f;
            case "exposed_to_sky" -> level.canSeeSky(holder.blockPosition()) ? 1f : 0f;
            case "time_of_day" -> {
                String when = effect.getString("when");
                if ("night".equalsIgnoreCase(when)) yield level.isDay() ? 0f : 1f;
                if ("day".equalsIgnoreCase(when)) yield level.isDay() ? 1f : 0f;
                // default: smooth scale, 0 at midday -> 1 at midnight
                float t = level.getTimeOfDay(1f) / 24000f;
                yield Mth.clamp(0.5f + 0.5f * Mth.sin((float) (t * Math.PI * 2 - Math.PI / 2)), 0f, 1f);
            }
            case "moon_phase" -> level.getMoonBrightness();
            case "weather" -> {
                String when = effect.getString("when");
                if ("thunder".equalsIgnoreCase(when)) yield level.isThundering() ? 1f : 0f;
                if ("rain".equalsIgnoreCase(when)) yield level.isRaining() ? 1f : 0f;
                yield level.isRaining() ? 0f : 1f; // default: clear weather
            }
            case "biome" -> {
                String id = effect.getString("biome");
                if (id == null) yield 0f;
                Holder<Biome> biome = level.getBiome(holder.blockPosition());
                if (id.startsWith("#")) {
                    ResourceLocation tag = ResourceLocation.tryParse(id.substring(1));
                    yield tag != null && biome.is(TagKey.create(Registries.BIOME, tag)) ? 1f : 0f;
                }
                ResourceLocation rl = ResourceLocation.tryParse(id);
                yield rl != null && biome.is(rl) ? 1f : 0f;
            }
            case "temperature" -> {
                float temp = level.getBiome(holder.blockPosition()).value().getModifiedClimateSettings().temperature();
                float min = effect.getFloat("min_temp", 0.15f);
                float max = effect.getFloat("max_temp", 1.0f);
                yield Mth.clamp(Mth.inverseLerp(temp, min, max), 0f, 1f);
            }
            case "dimension" -> {
                String dim = effect.getString("dimension");
                if (dim == null) yield 0f;
                ResourceLocation rl = ResourceLocation.tryParse(dim);
                yield rl != null && level.dimension().location().equals(rl) ? 1f : 0f;
            }

            // --- Entity state (holder) ---
            case "health_low" -> 1f - Mth.clamp(holder.getHealth() / holder.getMaxHealth(), 0f, 1f);
            case "health_high" -> Mth.clamp(holder.getHealth() / holder.getMaxHealth(), 0f, 1f);
            case "hunger" -> {
                if (!(holder instanceof Player p)) yield 0f;
                yield Mth.clamp(p.getFoodData().getFoodLevel() / 20f, 0f, 1f);
            }
            case "xp_level" -> {
                if (!(holder instanceof Player p)) yield 0f;
                int cap = effect.getInt("cap", 30);
                yield cap <= 0 ? 0f : Mth.clamp((float) p.experienceLevel / cap, 0f, 1f);
            }
            case "sneaking" -> holder.isCrouching() ? 1f : 0f;
            case "sprinting" -> holder.isSprinting() ? 1f : 0f;
            case "swimming" -> holder.isSwimming() ? 1f : 0f;
            case "riding" -> holder.isPassenger() ? 1f : 0f;
            case "on_fire" -> holder.isOnFire() ? 1f : 0f;
            case "in_water" -> holder.isInWater() ? 1f : 0f;
            case "in_lava" -> holder.isInLava() ? 1f : 0f;
            case "status_effect" -> {
                String id = effect.getString("effect");
                if (id == null) yield 0f;
                ResourceLocation rl = ResourceLocation.tryParse(id);
                if (rl == null) yield 0f;
                var mobEffect = BuiltInRegistries.MOB_EFFECT.getHolder(rl);
                yield mobEffect.isPresent() && holder.hasEffect(mobEffect.get()) ? 1f : 0f;
            }
            case "armor_count" -> {
                int count = 0;
                for (ItemStack s : holder.getArmorSlots()) {
                    if (!s.isEmpty()) count++;
                }
                yield count / 4f;
            }

            // --- Tool state ---
            case "durability_low" -> {
                if (tool == null || !tool.isDamageableItem()) yield 0f;
                float t = (float) tool.getDamageValue() / tool.getMaxDamage();
                yield Mth.clamp(t, 0f, 1f);
            }
            case "durability_high" -> {
                if (tool == null || !tool.isDamageableItem()) yield 0f;
                float t = (float) tool.getDamageValue() / tool.getMaxDamage();
                yield Mth.clamp(1f - t, 0f, 1f);
            }
            case "enchantment_level" -> {
                String id = effect.getString("enchantment");
                if (id == null || tool == null) yield 0f;
                ResourceLocation rl = ResourceLocation.tryParse(id);
                if (rl == null) yield 0f;
                var ench = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(rl);
                if (ench.isEmpty()) yield 0f;
                int lvl = EnchantmentHelper.getItemEnchantmentLevel(ench.get(), tool);
                int max = ench.get().value().getMaxLevel();
                yield max <= 0 ? 0f : Mth.clamp((float) lvl / max, 0f, 1f);
            }

            // --- Target state (attack events only) ---
            case "target_health_low" -> {
                LivingEntity target = getTarget(holder, event);
                yield target == null ? 0f : 1f - Mth.clamp(target.getHealth() / target.getMaxHealth(), 0f, 1f);
            }
            case "target_health_high" -> {
                LivingEntity target = getTarget(holder, event);
                yield target == null ? 0f : Mth.clamp(target.getHealth() / target.getMaxHealth(), 0f, 1f);
            }
            case "target_on_fire" -> {
                LivingEntity target = getTarget(holder, event);
                yield target != null && target.isOnFire() ? 1f : 0f;
            }
            case "target_in_water" -> {
                LivingEntity target = getTarget(holder, event);
                yield target != null && target.isInWater() ? 1f : 0f;
            }
            case "target_type" -> {
                LivingEntity target = getTarget(holder, event);
                String id = effect.getString("entity");
                if (target == null || id == null) yield 0f;
                if (id.startsWith("#")) {
                    ResourceLocation tag = ResourceLocation.tryParse(id.substring(1));
                    yield tag != null && target.getType().is(TagKey.create(Registries.ENTITY_TYPE, tag)) ? 1f : 0f;
                }
                ResourceLocation rl = ResourceLocation.tryParse(id);
                yield rl != null && BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).equals(rl) ? 1f : 0f;
            }

            default -> 0f;
        };
    }

    private static LivingEntity getTarget(LivingEntity holder, Object event) {
        if (event instanceof LivingDamageEvent dmg && dmg.getEntity() != holder) {
            return dmg.getEntity();
        }
        return null;
    }
}
