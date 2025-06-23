package com.iamfmgod.armoredcombatenhancer.modules.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MovementModule handles player mobility adjustments including:
 * - Calculating movement speed penalties based on armor weight.
 * - Implementing two distinct mechanics:
 *   • Dash: a lateral (or forward fallback) dash triggered via a dedicated key.
 *   • Dodge: a swift lateral dodge triggered by double-tapping left/right keys.
 */
public class MovementModule {

    // Map to track cooldowns so that dash/dodge cannot be spammed.
    protected static final Map<UUID, Integer> COOLDOWN_MAP = new HashMap<>();

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            UUID id = player.getUniqueID();

            // Decrement cooldown timer if necessary.
            if (COOLDOWN_MAP.containsKey(id)) {
                int cd = COOLDOWN_MAP.get(id);
                if (cd > 0) {
                    COOLDOWN_MAP.put(id, cd - 1);
                } else {
                    COOLDOWN_MAP.remove(id);
                }
            }
            // Adjust player's walk speed based on armor weight.
            float speedModifier = computeSpeedPenalty(player);
            player.capabilities.setPlayerWalkSpeed(0.1F * speedModifier);
        }
    }

    private float computeSpeedPenalty(EntityPlayer player) {
        // Slot-based penalty coefficients.
        float helmetCoeff = 0.02F;
        float chestplateCoeff = 0.05F;
        float leggingsCoeff = 0.04F;
        float bootsCoeff = 0.03F;
        float penaltyMultiplier = 1.0F;
        int index = 0;

        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                float weight = getArmorWeight((ItemArmor) stack.getItem());
                switch (index) {
                    case 0: // Boots.
                        penaltyMultiplier -= weight * bootsCoeff;
                        break;
                    case 1: // Leggings.
                        penaltyMultiplier -= weight * leggingsCoeff;
                        break;
                    case 2: // Chestplate.
                        penaltyMultiplier -= weight * chestplateCoeff;
                        break;
                    case 3: // Helmet.
                        penaltyMultiplier -= weight * helmetCoeff;
                        break;
                }
            }
            index++;
        }
        return Math.max(0.5F, penaltyMultiplier);
    }

    private float getArmorWeight(ItemArmor armor) {
        String materialName = armor.getArmorMaterial().toString().toUpperCase();
        if (materialName.contains("LEATHER"))
            return 1.0F;
        else if (materialName.contains("IRON"))
            return 2.0F;
        else if (materialName.contains("DIAMOND"))
            return 4.5F;
        return 1.5F;
    }

    /**
     * Dash mechanic: When the dash key is pressed, this method checks the current keyboard state.
     * - If only A is pressed, it dashes left.
     * - If only D is pressed, it dashes right.
     * - If neither (or both) are pressed, it dashes forward.
     *
     * Additionally, if the player is on the ground, we force an upward boost (set motionY to 0.2D)
     * so the dash activates immediately; otherwise, a smaller boost is added.
     *
     * @param player The player performing the dash.
     */
    public void triggerDash(EntityPlayer player) {
        UUID id = player.getUniqueID();
        if (COOLDOWN_MAP.containsKey(id) && COOLDOWN_MAP.get(id) > 0) {
            return;
        }

        Vec3d dashDir;
        boolean aPressed = Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean dPressed = Keyboard.isKeyDown(Keyboard.KEY_D);

        // Determine dash direction.
        if (aPressed && !dPressed) {
            // Dash left.
            Vec3d look = player.getLookVec();
            Vec3d leftVec = new Vec3d(look.z, 0, -look.x);
            dashDir = leftVec;
        } else if (dPressed && !aPressed) {
            // Dash right.
            Vec3d look = player.getLookVec();
            Vec3d leftVec = new Vec3d(look.z, 0, -look.x);
            dashDir = leftVec.scale(-1);
        } else {
            // Default: dash forward.
            dashDir = player.getLookVec();
        }

        double dashSpeed = 1.5D;
        player.motionX += dashDir.x * dashSpeed;
        player.motionZ += dashDir.z * dashSpeed;
        if (player.onGround) {
            player.motionY = 0.2D;  // Force an upward boost if on the ground.
        } else {
            player.motionY += 0.1D;
        }

        // Spawn particle effects for visual feedback.
        if (player.world.isRemote) {
            for (int i = 0; i < 10; i++) {
                double offsetX = (player.world.rand.nextDouble() - 0.5D) * player.width;
                double offsetY = player.world.rand.nextDouble() * player.height;
                double offsetZ = (player.world.rand.nextDouble() - 0.5D) * player.width;
                player.world.spawnParticle(EnumParticleTypes.CLOUD,
                        player.posX + offsetX,
                        player.posY + offsetY,
                        player.posZ + offsetZ,
                        0, 0, 0);
            }
        }
        COOLDOWN_MAP.put(player.getUniqueID(), 40); // 40 ticks cooldown (~2 seconds)
        System.out.println("Dash triggered for " + player.getName());
    }

    /**
     * Dodge mechanic: a swift lateral dodge triggered by double-tapping left/right.
     *
     * @param player The player performing the dodge.
     * @param isLeft True if dodging left; false if dodging right.
     */
    public void triggerDodge(EntityPlayer player, boolean isLeft) {
        UUID id = player.getUniqueID();
        if (COOLDOWN_MAP.containsKey(id) && COOLDOWN_MAP.get(id) > 0) {
            return;
        }
        Vec3d look = player.getLookVec();
        Vec3d leftVec = new Vec3d(look.z, 0, -look.x);
        Vec3d dodgeDir = isLeft ? leftVec : leftVec.scale(-1);
        double dodgeSpeed = 1.0D;
        player.motionX += dodgeDir.x * dodgeSpeed;
        player.motionZ += dodgeDir.z * dodgeSpeed;
        player.motionY += 0.1D;

        if (player.world.isRemote) {
            for (int i = 0; i < 10; i++) {
                double offsetX = (player.world.rand.nextDouble() - 0.5D) * player.width;
                double offsetY = player.world.rand.nextDouble() * player.height;
                double offsetZ = (player.world.rand.nextDouble() - 0.5D) * player.width;
                player.world.spawnParticle(EnumParticleTypes.CLOUD,
                        player.posX + offsetX,
                        player.posY + offsetY,
                        player.posZ + offsetZ,
                        0, 0, 0);
            }
        }
        COOLDOWN_MAP.put(player.getUniqueID(), 40);
        System.out.println("Dodge triggered for " + player.getName() + " (isLeft: " + isLeft + ")");
    }
}