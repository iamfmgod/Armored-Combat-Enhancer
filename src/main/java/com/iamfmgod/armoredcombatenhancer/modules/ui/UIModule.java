package com.iamfmgod.armoredcombatenhancer.modules.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class UIModule {

    private static final int DISPLAY_DURATION_MS = 5000;
    private static long overlayStartTime = System.currentTimeMillis();

    /** Call this when any ACE‐GUI opens to restart the on‐screen hint timer */
    public static void resetOverlayTimer() {
        overlayStartTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();
        boolean matched = false;
        boolean hasNBT = false;

        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            if (nbt.hasKey("ArmorWeight")) {
                float w = nbt.getFloat("ArmorWeight");
                tooltip.add(TextFormatting.GRAY + "NBT Weight: " + w + TextFormatting.RESET);
                hasNBT = true;
            }
            if (nbt.hasKey("DynamicEffects")) {
                String fx = nbt.getString("DynamicEffects");
                tooltip.add(TextFormatting.GRAY + "NBT Effects: " + fx + TextFormatting.RESET);
                hasNBT = true;
            }
        }

        ResourceLocation rl = stack.getItem().getRegistryName();
        if (rl == null) return;

        String path = rl.getPath().toLowerCase();
        String fullName = rl.toString().toLowerCase();

        // Weapon configs
        JsonObject weaponConfig = ConfigModule.getWeaponConfig(path);
        if (weaponConfig != null) {
            if (weaponConfig.has("critChance")) {
                int pct = Math.round(weaponConfig.get("critChance").getAsFloat() * 100);
                tooltip.add(TextFormatting.YELLOW + "Crit Chance: " + pct + "%" + TextFormatting.RESET);
            }
            if (weaponConfig.has("effects")) {
                appendEffects(tooltip, weaponConfig.getAsJsonArray("effects"), "On Hit: ");
            }
            matched = true;
        }

        // Armor configs
        JsonObject armorConfig = ConfigModule.getArmorConfig(path);
        if (armorConfig != null) {
            if (armorConfig.has("armorWeight")) {
                float weight = armorConfig.get("armorWeight").getAsFloat();
                tooltip.add(TextFormatting.GRAY + "Armor Weight: " + weight + TextFormatting.RESET);
            }
            if (armorConfig.has("dynamicEffects")) {
                appendEffects(tooltip, armorConfig.getAsJsonArray("dynamicEffects"), "Passive: ");
            }
            matched = true;
        }

        // Final ACE tag
        if (matched || hasNBT
                || fullName.contains("tconstruct")
                || fullName.contains("botania")
                || fullName.contains("thaumcraft")) {
            tooltip.add(TextFormatting.DARK_GREEN + "Enhanced by A.C.E." + TextFormatting.RESET);
        }
    }

    private void appendEffects(List<String> tooltip, JsonArray arr, String prefix) {
        for (JsonElement e : arr) {
            JsonObject fx = e.getAsJsonObject();
            String name   = fx.get("name").getAsString();
            int level     = fx.get("level").getAsInt();
            int dur       = fx.get("duration").getAsInt();
            int chancePct = Math.round(fx.get("chance").getAsFloat() * 100);
            tooltip.add(TextFormatting.GRAY
                    + prefix + name + " Lv." + level
                    + " (" + chancePct + "%, " + dur + "t)"
                    + TextFormatting.RESET);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) return;

        long elapsed = System.currentTimeMillis() - overlayStartTime;
        if (elapsed <= DISPLAY_DURATION_MS) {
            mc.fontRenderer.drawString("Press [G] for A.C.E. Options", 5, 5, 0xFFFFFF);
            mc.fontRenderer.drawString("Press [K] for Skill Tree",      5, 15, 0xFFFFFF);
        }
    }
}