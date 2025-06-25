package com.iamfmgod.armoredcombatenhancer.modules.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Central effect applier.
 * Maps JSON names → Potions (vanilla + reflectively loaded mods),
 * stacks duration/amplifier, and delegates "stun" to StunHandler.
 */
public class EffectsModule {

    private static final int MAX_EFFECT_LEVEL = 5;
    private static final Map<String, Potion> EFFECT_MAP = new HashMap<>();

    static {
        // ── VANILLA ──
        EFFECT_MAP.put("bleeding",       MobEffects.POISON);
        EFFECT_MAP.put("withering",      MobEffects.WITHER);
        EFFECT_MAP.put("slowness",       MobEffects.SLOWNESS);
        EFFECT_MAP.put("knockbackboost", MobEffects.WEAKNESS);
        EFFECT_MAP.put("armorreduction", MobEffects.MINING_FATIGUE);
        EFFECT_MAP.put("nightvision",    MobEffects.NIGHT_VISION);
        EFFECT_MAP.put("manaleech",      MobEffects.HUNGER);
        EFFECT_MAP.put("petalslice",     MobEffects.POISON);
        EFFECT_MAP.put("visdrain",       MobEffects.POISON);
        EFFECT_MAP.put("voidrip",        MobEffects.BLINDNESS);
        EFFECT_MAP.put("ignite",         MobEffects.FIRE_RESISTANCE);
        EFFECT_MAP.put("energyrip",      MobEffects.WEAKNESS);
        EFFECT_MAP.put("overwhelm",      MobEffects.SLOWNESS);
        // "stun" is handled specially—see StunHandler

        // ── DRACONIC EVOLUTION ──
        if (Loader.isModLoaded("draconicevolution")) {
            try {
                Class<?> c = Class.forName("com.brandon3055.draconicevolution.handlers.DEPotions");
                Field f = c.getField("stagger");
                EFFECT_MAP.put("aetherstagger", (Potion) f.get(null));
            } catch (Exception e) {
                System.err.println("[A.C.E.] DE potions load failed: " + e.getMessage());
            }
        }

        // ── BOTANIA ──
        if (Loader.isModLoaded("botania")) {
            try {
                Class<?> c = Class.forName("vazkii.botania.common.potion.ModPotions");
                Potion thorn  = (Potion) c.getField("thornProtect").get(null);
                Potion mregen = (Potion) c.getField("manaRegen").get(null);
                EFFECT_MAP.put("thornsaura",    thorn);
                EFFECT_MAP.put("manaregenboost", mregen);
            } catch (Exception e) {
                System.err.println("[A.C.E.] Botania potions load failed: " + e.getMessage());
            }
        }

        // ── PROJECTE ──
        if (Loader.isModLoaded("projecte")) {
            try {
                Class<?> c = Class.forName("moze_intel.projecte.gameObjs.potions.PEPotions");
                EFFECT_MAP.put("timeslowfield", (Potion) c.getField("time").get(null));
                EFFECT_MAP.put("energyrip",      (Potion) c.getField("darkMatter").get(null));
                EFFECT_MAP.put("overwhelm",      (Potion) c.getField("redMatter").get(null));
            } catch (Exception e) {
                System.err.println("[A.C.E.] ProjectE potions load failed: " + e.getMessage());
            }
        }

        // ── TWILIGHT FOREST ──
        if (Loader.isModLoaded("twilightforest")) {
            try {
                Class<?> c = Class.forName("twilightforest.potions.TFPotions");
                EFFECT_MAP.put("fortitude", (Potion) c.getField("knightmetal").get(null));
                EFFECT_MAP.put("ignite",    (Potion) c.getField("fire").get(null));
            } catch (Exception e) {
                System.err.println("[A.C.E.] TF potions load failed: " + e.getMessage());
            }
        }
    }

    /**
     * Applies or stacks the named effect on the given entity and spawns particle effects.
     *
     * @param entity     The entity to affect.
     * @param effectName The name of the effect.
     * @param duration   Duration in ticks (ignored for "stun").
     * @param level      Effect level (1-based).
     */
    public static void addOrRefreshEffect(EntityLivingBase entity,
                                          String effectName,
                                          int duration,
                                          int level) {
        if (effectName == null) return;
        String key = effectName.trim().toLowerCase(Locale.ROOT);

        // True stun: force stun duration to 10 ticks (half a second)
        if ("stun".equals(key)) {
            StunHandler.applyStun(entity, 10);
            return;
        }

        Potion potion = EFFECT_MAP.get(key);
        if (potion == null) {
            System.err.println("[A.C.E.] Unknown effect: " + effectName);
            return;
        }

        // Convert level (1 → amplifier 0) and cap it.
        int amp = Math.min(Math.max(level - 1, 0), MAX_EFFECT_LEVEL - 1);
        PotionEffect incoming = new PotionEffect(potion, duration, amp);

        PotionEffect existing = entity.getActivePotionEffect(potion);
        if (existing != null) {
            int finalAmp = Math.max(existing.getAmplifier(), incoming.getAmplifier());
            int finalDur = Math.max(existing.getDuration(), incoming.getDuration());
            entity.removePotionEffect(potion);
            entity.addPotionEffect(new PotionEffect(potion, finalDur, finalAmp));
        } else {
            entity.addPotionEffect(incoming);
        }

        // Spawn particles to visually indicate the effect has been applied.
        spawnEffectParticles(entity);
    }

    /**
     * For runtime additions of custom mod potions.
     *
     * @param name   The effect name (case-insensitive).
     * @param potion The Potion instance to use.
     */
    public static void registerCustomEffect(String name, Potion potion) {
        if (name != null && potion != null) {
            EFFECT_MAP.put(name.trim().toLowerCase(Locale.ROOT), potion);
        }
    }

    /**
     * Spawns particles around the affected entity.
     * Only spawns particles on the client side.
     *
     * @param entity The entity on which to spawn particles.
     */
    private static void spawnEffectParticles(EntityLivingBase entity) {
        // Only spawn particles if this is a client-side call.
        if (entity.getEntityWorld().isRemote) {
            for (int i = 0; i < 10; i++) {
                double offsetX = (entity.getRNG().nextDouble() - 0.5D) * entity.width;
                double offsetY = entity.getRNG().nextDouble() * entity.height;
                double offsetZ = (entity.getRNG().nextDouble() - 0.5D) * entity.width;
                entity.getEntityWorld().spawnParticle(
                        EnumParticleTypes.SPELL_WITCH,
                        entity.posX + offsetX,
                        entity.posY + offsetY,
                        entity.posZ + offsetZ,
                        0.0, 0.1, 0.0);
            }
        }
    }
}