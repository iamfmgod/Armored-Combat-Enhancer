package com.iamfmgod.armoredcombatenhancer.modules.combat;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import com.iamfmgod.armoredcombatenhancer.modules.effects.StunHandler;
import com.iamfmgod.armoredcombatenhancer.modules.effects.EffectsModule;

/**
 * CombatModule handles combat logic such as critical hit detection,
 * applying custom stun effects, and triggering weapon-specific debuffs.
 */
public class CombatModule {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        // Use getTrueSource() to obtain the attacking entity.
        if (source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) source.getTrueSource();
            EntityLivingBase target = event.getEntityLiving();

            if (isCriticalHit(attacker, target)) {
                // Instead of applying a stagger effect, use the custom stun function.
                StunHandler.applyStun(target, 10);
                // Also apply any weapon-specific debuffs.
                applyWeaponDebuffs(attacker, target, attacker.getHeldItem(EnumHand.MAIN_HAND));
            }
        }
    }

    /**
     * Determines if a hit qualifies as a critical hit.
     * This simple implementation checks whether the attacker is in mid-air,
     * not on the ground and not riding another entity.
     *
     * @param attacker The attacking player.
     * @param target   The target entity.
     * @return True if the hit is critical, otherwise false.
     */
    private boolean isCriticalHit(EntityPlayer attacker, EntityLivingBase target) {
        // Basic vanilla-like critical logic:
        return attacker.fallDistance > 0.0F && !attacker.onGround && !attacker.isRiding();
    }

    /**
     * Applies weapon-specific debuffs based on the type of weapon used.
     * In this example, if the weapon's unlocalized name contains "sword",
     * a bleeding effect is applied to the target.
     *
     * @param attacker    The player attacking.
     * @param target      The entity being hit.
     * @param weaponStack The ItemStack representing the held weapon.
     */
    private void applyWeaponDebuffs(EntityPlayer attacker, EntityLivingBase target, ItemStack weaponStack) {
        if (weaponStack != null) {
            String weaponType = weaponStack.getItem().getUnlocalizedName().toLowerCase();
            if (weaponType.contains("sword")) {
                System.out.println("Bleeding effect applied to " + target.getName());
                // Apply the bleeding effect for 40 ticks at level 1.
                EffectsModule.addOrRefreshEffect(target, "bleeding", 40, 1);
            }
            // Additional weapon types and debuffs can be added here.
        }
    }
}