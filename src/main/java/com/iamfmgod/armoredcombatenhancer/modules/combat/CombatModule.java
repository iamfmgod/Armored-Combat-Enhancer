package com.iamfmgod.armoredcombatenhancer.modules.combat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.effects.EffectsModule;
import com.iamfmgod.armoredcombatenhancer.modules.effects.StunHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatModule {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getTrueSource() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer attacker = (EntityPlayer) source.getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        ItemStack held = attacker.getHeldItem(EnumHand.MAIN_HAND);
        if (held.isEmpty()) return;

        ResourceLocation regName = held.getItem().getRegistryName();
        if (regName == null) return;

        String path = regName.getPath().toLowerCase();
        // Retrieve weapon configuration using built-in fallback logic.
        JsonObject config = ConfigModule.getWeaponConfig(path);

        float critChance = 0.1F;
        float damageMultiplier = 1.0F;
        if (config != null) {
            if (config.has("critChance")) {
                critChance = parseFloatSafe(config.get("critChance").getAsString(), 0.1F);
            }
            if (config.has("damageMultiplier")) {
                damageMultiplier = parseFloatSafe(config.get("damageMultiplier").getAsString(), 1.0F);
            }
        }

        // If the hit is critical and passes the chance check
        if (isCriticalHit(attacker) && attacker.getRNG().nextFloat() <= critChance) {
            float modifiedDamage = event.getAmount() * damageMultiplier;
            event.setAmount(modifiedDamage);
            StunHandler.applyStun(target, 10);

            if (config != null && config.has("effects")) {
                JsonArray effects = config.getAsJsonArray("effects");
                for (JsonElement e : effects) {
                    JsonObject fx = e.getAsJsonObject();
                    String name = fx.get("name").getAsString();
                    int level = fx.get("level").getAsInt();
                    int duration = fx.get("duration").getAsInt();
                    float chance = fx.get("chance").getAsFloat();
                    if (attacker.getRNG().nextFloat() <= chance) {
                        EffectsModule.addOrRefreshEffect(target, name, duration, level);
                    }
                }
            }
        }
    }

    private boolean isCriticalHit(EntityPlayer attacker) {
        return attacker.fallDistance > 0.0F && !attacker.onGround && !attacker.isRiding();
    }

    private float parseFloatSafe(String value, float fallback) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}