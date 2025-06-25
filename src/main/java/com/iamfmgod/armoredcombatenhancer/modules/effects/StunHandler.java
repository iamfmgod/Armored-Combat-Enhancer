package com.iamfmgod.armoredcombatenhancer.modules.effects;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Handles true "stun" by freezing an entity's motion,
 * clearing its navigation (if applicable), canceling its attacks,
 * and displaying a particle effect when stunned.
 */
public class StunHandler {
    // Tracks remaining stun ticks per entity.
    private static final Map<UUID, Integer> STUN_MAP = Maps.newConcurrentMap();

    /**
     * Call this during your mod's initialization to register this handler.
     */
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new StunHandler());
    }

    /**
     * Applies or refreshes a stun on an entity.
     * The caller should force the duration to 10 ticks (half a second).
     * Spawns an initial burst of particles when the stun is applied.
     *
     * @param entity        the target entity
     * @param durationTicks the stun duration in ticks (should be 10)
     */
    public static void applyStun(EntityLivingBase entity, int durationTicks) {
        UUID id = entity.getUniqueID();
        int prev = STUN_MAP.getOrDefault(id, 0);
        STUN_MAP.put(id, Math.max(prev, durationTicks));

        // Spawn an initial burst of particles when the stun is applied.
        if (entity.world.isRemote) { // client side check
            for (int i = 0; i < 10; i++) {
                double offsetX = (entity.world.rand.nextDouble() - 0.5) * entity.width;
                double offsetY = entity.world.rand.nextDouble() * entity.height;
                double offsetZ = (entity.world.rand.nextDouble() - 0.5) * entity.width;
                entity.world.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                        entity.posX + offsetX,
                        entity.posY + offsetY,
                        entity.posZ + offsetZ,
                        0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        UUID id = entity.getUniqueID();
        Integer ticks = STUN_MAP.get(id);

        if (ticks != null && ticks > 0) {
            // Freeze entity motion.
            entity.motionX = 0;
            entity.motionY = 0;
            entity.motionZ = 0;

            // For AI-controlled mobs, clear their current navigation.
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).getNavigator().clearPath();
            }

            // Client-side: spawn a particle effect every 5 ticks while stunned.
            if (entity.world.isRemote && ticks % 5 == 0) {
                double offsetX = (entity.world.rand.nextDouble() - 0.5) * entity.width;
                double offsetY = entity.world.rand.nextDouble() * entity.height;
                double offsetZ = (entity.world.rand.nextDouble() - 0.5) * entity.width;
                entity.world.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                        entity.posX + offsetX,
                        entity.posY + offsetY,
                        entity.posZ + offsetZ,
                        0, 0, 0);
            }

            // Decrement remaining stun duration.
            STUN_MAP.put(id, ticks - 1);
        } else if (ticks != null) {
            // Stun ended.
            STUN_MAP.remove(id);
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        // Prevent attacks initiated by stunned entities.
        if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
            if (STUN_MAP.containsKey(attacker.getUniqueID())) {
                event.setCanceled(true);
            }
        }
    }
}