package com.iamfmgod.armoredcombatenhancer.modules.movement;

import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementModule {
    private static final Map<UUID, Integer> COOLDOWN_MAP = new HashMap<>();

    // UUID for our movement‐speed modifier
    private static final UUID SPEED_MODIFIER_UUID =
            UUID.fromString("d2931d71-2c4e-49ea-9911-acecfccf2374");

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        UUID id = player.getUniqueID();

        // Tick down cooldowns
        COOLDOWN_MAP.computeIfPresent(id, (u, cd) -> (cd > 0) ? cd - 1 : null);

        // SERVER‐ONLY: adjust movement‐speed attribute
        if (!player.world.isRemote) {
            double speedFactor = computeSpeedPenalty(player);
            IAttributeInstance attr = player.getEntityAttribute(
                    SharedMonsterAttributes.MOVEMENT_SPEED
            );
            if (attr != null) {
                double amount = speedFactor - 1.0;
                AttributeModifier existing = attr.getModifier(SPEED_MODIFIER_UUID);
                if (existing != null) {
                    attr.removeModifier(existing);
                }
                if (Math.abs(amount) > 1e-4) {
                    attr.applyModifier(new AttributeModifier(
                            SPEED_MODIFIER_UUID,
                            "Armor weight penalty",
                            amount,
                            1 // MULTIPLY_BASE
                    ));
                }
            }
        }
    }

    public static void triggerDash(EntityPlayer player) {
        if (isOnCooldown(player)) return;
        clientDash(player, player.getLookVec());
        startCooldown(player);
    }

    public static void triggerDodge(EntityPlayer player, boolean isLeft) {
        if (isOnCooldown(player)) return;
        Vec3d look = player.getLookVec();
        Vec3d side = new Vec3d(look.z, 0, -look.x).normalize();
        Vec3d dir  = isLeft ? side : side.scale(-1.5D);
        clientDash(player, dir);
        startCooldown(player);
    }

    public static void triggerShieldBash(EntityPlayer player) {
        if (isOnCooldown(player)) return;

        // Check for shield in either hand
        ItemStack shield = player.getHeldItemOffhand();
        if (shield.isEmpty() || !(shield.getItem() instanceof ItemShield)) {
            shield = player.getHeldItemMainhand();
            if (shield.isEmpty() || !(shield.getItem() instanceof ItemShield)) {
                return;
            }
        }

        Vec3d look   = player.getLookVec();
        Vec3d eyePos = player.getPositionVector().add(0, player.getEyeHeight(), 0);
        Vec3d endPos = eyePos.add(look.scale(ConfigModule.shieldBashRange));

        // 1) Server: find target, apply damage & knockback
        if (!player.world.isRemote) {
            EntityPlayer hitTarget = null;
            double closestDist = ConfigModule.shieldBashRange;

            AxisAlignedBB scanBB = player.getEntityBoundingBox()
                    .grow(ConfigModule.shieldBashRange)
                    .expand(1, 1, 1);

            for (Entity e : player.world.getEntitiesWithinAABBExcludingEntity(player, scanBB)) {
                if (!(e instanceof EntityPlayer)) continue;
                EntityPlayer candidate = (EntityPlayer) e;
                RayTraceResult mop = candidate.getEntityBoundingBox()
                        .grow(0.3D)
                        .calculateIntercept(eyePos, endPos);
                if (mop == null) continue;
                double dist = eyePos.distanceTo(mop.hitVec);
                if (dist < closestDist) {
                    closestDist = dist;
                    hitTarget = candidate;
                }
            }

            if (hitTarget != null) {
                hitTarget.attackEntityFrom(
                        ConfigModule.shieldBashDamage > 0
                                ? DamageSource.causePlayerDamage(player)
                                : DamageSource.GENERIC,
                        ConfigModule.shieldBashDamage
                );
                Vec3d kbDir = hitTarget.getPositionVector()
                        .subtract(player.getPositionVector())
                        .normalize();
                hitTarget.knockBack(
                        player,
                        ConfigModule.shieldBashKnockback,
                        -kbDir.x,
                        -kbDir.z
                );
            }
        }
        // 2) Client: play sound & dash effect
        else {
            player.playSound(
                    net.minecraft.init.SoundEvents.ITEM_SHIELD_BLOCK,
                    1.0F, 1.0F
            );
            clientDash(player, look);
        }

        startCooldown(player);
    }

    private static void clientDash(EntityPlayer player, Vec3d dir) {
        player.motionX += dir.x * ConfigModule.shieldDashSpeed;
        player.motionZ += dir.z * ConfigModule.shieldDashSpeed;
        player.motionY += player.onGround ? 0.2D : 0.1D;

        // Cloud particles
        for (int i = 0; i < 8; i++) {
            double dx = (player.world.rand.nextDouble() - 0.5D) * player.width;
            double dy = player.world.rand.nextDouble() * player.height;
            double dz = (player.world.rand.nextDouble() - 0.5D) * player.width;
            player.world.spawnParticle(
                    EnumParticleTypes.CLOUD,
                    player.posX + dx,
                    player.posY + dy,
                    player.posZ + dz,
                    0, 0, 0
            );
        }
    }

    private static boolean isOnCooldown(EntityPlayer player) {
        return COOLDOWN_MAP.getOrDefault(player.getUniqueID(), 0) > 0;
    }

    private static void startCooldown(EntityPlayer player) {
        COOLDOWN_MAP.put(player.getUniqueID(), ConfigModule.shieldBashCooldown);
    }

    /**
     * Computes a 0.5–1.0 speed multiplier based on the player’s worn armor.
     * Uses ConfigModule overrides if specified; otherwise falls back to defaults.
     */
    private static double computeSpeedPenalty(EntityPlayer player) {
        // coefficients per armor slot: boots, leggings, chest, helmet
        float[] coeff = { 0.03F, 0.04F, 0.05F, 0.02F };
        double penalty = 1.0;
        int idx = 0;

        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                // guard against null registry names
                if (armor.getRegistryName() != null) {
                    String mat = armor.getRegistryName().toString().toLowerCase();
                    float fallback = getDefaultWeight(armor);
                    float weight   = ConfigModule.getArmorWeight(mat, fallback);
                    penalty -= weight * coeff[idx];
                }
            }
            idx++;
        }
        return Math.max(0.5, penalty);
    }

    /** Returns built-in default weight for armor materials. */
    private static float getDefaultWeight(ItemArmor armor) {
        switch (armor.getArmorMaterial().name()) {
            case "LEATHER": return 1.0F;
            case "IRON":    return 2.0F;
            case "DIAMOND": return 4.5F;
            default:        return 1.5F;
        }
    }
}