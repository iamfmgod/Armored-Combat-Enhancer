package com.iamfmgod.armoredcombatenhancer.modules.movement;

import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementModule {
    protected static final Map<UUID, Integer> COOLDOWN_MAP = new HashMap<>();

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        UUID id = player.getUniqueID();

        // tick down
        if (COOLDOWN_MAP.containsKey(id)) {
            int cd = COOLDOWN_MAP.get(id);
            if (cd > 0) COOLDOWN_MAP.put(id, cd - 1);
            else        COOLDOWN_MAP.remove(id);
        }

        // armor‐weight speed
        float speedMod = computeSpeedPenalty(player);
        player.capabilities.setPlayerWalkSpeed(0.1F * speedMod);
    }

    public static void triggerDash(EntityPlayer player) {
        if (isOnCooldown(player)) return;
        Vec3d dir = player.getLookVec();
        clientDash(player, dir);
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

        // check shield
        ItemStack shield = player.getHeldItemOffhand();
        if (shield.isEmpty() || !(shield.getItem() instanceof ItemShield)) {
            shield = player.getHeldItemMainhand();
            if (shield.isEmpty() || !(shield.getItem() instanceof ItemShield)) {
                return;
            }
        }

        Vec3d look     = player.getLookVec();
        Vec3d eyePos   = player.getPositionVector()
                .add(0, player.getEyeHeight(), 0);
        Vec3d endPos   = eyePos.add(look.scale(ConfigModule.shieldBashRange));

        // 1) server: hit & knockback
        if (!player.world.isRemote) {
            EntityLivingBase hit = null;
            double             dist = ConfigModule.shieldBashRange;

            AxisAlignedBB rayBB = player.getEntityBoundingBox()
                    .grow(ConfigModule.shieldBashRange)
                    .expand(1, 1, 1);

            for (Entity e : player.world.getEntitiesWithinAABBExcludingEntity(player, rayBB)) {
                if (!(e instanceof EntityLivingBase)) continue;
                RayTraceResult mop = e.getEntityBoundingBox()
                        .grow(0.3D)
                        .calculateIntercept(eyePos, endPos);
                if (mop == null) continue;

                double d = eyePos.distanceTo(mop.hitVec);
                if (d < dist) {
                    dist = d;
                    hit  = (EntityLivingBase) e;
                }
            }

            if (hit != null) {
                hit.attackEntityFrom(
                        DamageSource.causePlayerDamage(player),
                        ConfigModule.shieldBashDamage
                );
                Vec3d kbDir = hit.getPositionVector()
                        .subtract(player.getPositionVector())
                        .normalize();
                hit.knockBack(
                        player,
                        ConfigModule.shieldBashKnockback,
                        -kbDir.x,
                        -kbDir.z
                );
            }
        }
        // 2) client: sound
        else {
            player.playSound(
                    SoundEvents.ITEM_SHIELD_BLOCK,
                    1.0F, 1.0F
            );
        }

        // 3) client: dash after hit
        if (player.world.isRemote) {
            clientDash(player, look);
        }

        // 4) cooldown
        startCooldown(player);
    }

    private static void clientDash(EntityPlayer player, Vec3d look) {
        double speed = ConfigModule.shieldDashSpeed;
        player.motionX += look.x * speed;
        player.motionZ += look.z * speed;
        player.motionY += player.onGround ? 0.2D : 0.1D;

        if (player.world.isRemote) {
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
    }

    private static boolean isOnCooldown(EntityPlayer player) {
        return COOLDOWN_MAP.getOrDefault(
                player.getUniqueID(), 0
        ) > 0;
    }

    private static void startCooldown(EntityPlayer player) {
        COOLDOWN_MAP.put(
                player.getUniqueID(),
                ConfigModule.shieldBashCooldown
        );
    }

    private static float computeSpeedPenalty(EntityPlayer player) {
        float helmetCoeff     = 0.02F;
        float chestplateCoeff = 0.05F;
        float leggingsCoeff   = 0.04F;
        float bootsCoeff      = 0.03F;
        float penalty         = 1.0F;
        int   idx             = 0;

        for (ItemStack s : player.getArmorInventoryList()) {
            if (s != null && s.getItem() instanceof net.minecraft.item.ItemArmor) {
                float w = getArmorWeight(
                        (net.minecraft.item.ItemArmor) s.getItem()
                );
                switch (idx) {
                    case 0: penalty -= w * bootsCoeff;     break;
                    case 1: penalty -= w * leggingsCoeff;  break;
                    case 2: penalty -= w * chestplateCoeff;break;
                    case 3: penalty -= w * helmetCoeff;    break;
                }
            }
            idx++;
        }
        return Math.max(0.5F, penalty);
    }

    private static float getArmorWeight(net.minecraft.item.ItemArmor armor) {
        String mat = armor.getArmorMaterial().name();
        if (mat.contains("LEATHER")) return 1.0F;
        if (mat.contains("IRON"))    return 2.0F;
        if (mat.contains("DIAMOND")) return 4.5F;
        return 1.5F;
    }
}