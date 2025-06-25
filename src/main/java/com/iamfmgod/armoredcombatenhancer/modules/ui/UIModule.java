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

    public static void resetOverlayTimer() {
        overlayStartTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();
        boolean matched = false;
        boolean hasNBT = false;

        // Cache the tag compound to avoid repeated calls and potential NPEs.
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
        if (rl == null)
            return;

        String path = rl.getPath().toLowerCase();
        String fullName = rl.toString().toLowerCase();

        // Process weapon configs.
        JsonObject weaponConfig = ConfigModule.getWeaponConfig(path);
        if (weaponConfig != null) {
            if (weaponConfig.has("critChance")) {
                int pct = Math.round(weaponConfig.get("critChance").getAsFloat() * 100);
                tooltip.add(TextFormatting.YELLOW + "Crit Chance: " + pct + "%" + TextFormatting.RESET);
            }

            if (weaponConfig.has("effects")) {
                JsonArray arr = weaponConfig.getAsJsonArray("effects");
                appendEffects(tooltip, arr, "On Hit: ");
            }
            matched = true;
        }

        // Process armor configs.
        JsonObject armorConfig = ConfigModule.getArmorConfig(path);
        if (armorConfig != null) {
            if (armorConfig.has("armorWeight")) {
                float weight = armorConfig.get("armorWeight").getAsFloat();
                tooltip.add(TextFormatting.GRAY + "Armor Weight: " + weight + TextFormatting.RESET);
            }

            if (armorConfig.has("dynamicEffects")) {
                JsonArray arr = armorConfig.getAsJsonArray("dynamicEffects");
                appendEffects(tooltip, arr, "Passive: ");
            }
            matched = true;
        }

        // If any config matched or if the full name contains specific mod IDs, add a final enhancement tag.
        if (matched || hasNBT ||
                fullName.contains("tconstruct") ||
                fullName.contains("botania") ||
                fullName.contains("thaumcraft")) {
            tooltip.add(TextFormatting.DARK_GREEN + "Enhanced by A.C.E." + TextFormatting.RESET);
        }
    }

    /**
     * Appends visual effect descriptions to the provided tooltip.
     *
     * @param tooltip  the tooltip text list to add lines to
     * @param effectsArray the JSON array containing effect objects
     * @param prefix   the text prefix (e.g., "On Hit: " or "Passive: ")
     */
    private void appendEffects(List<String> tooltip, JsonArray effectsArray, String prefix) {
        for (JsonElement e : effectsArray) {
            JsonObject fx = e.getAsJsonObject();
            String name = fx.get("name").getAsString();
            int level = fx.get("level").getAsInt();
            int dur = fx.get("duration").getAsInt();
            int chance = Math.round(fx.get("chance").getAsFloat() * 100);
            tooltip.add(TextFormatting.GRAY + prefix + name + " Lv." + level +
                    " (" + chance + "%, " + dur + "t)" + TextFormatting.RESET);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (Minecraft.getMinecraft().currentScreen == null &&
                event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            long elapsed = System.currentTimeMillis() - overlayStartTime;
            if (elapsed <= DISPLAY_DURATION_MS) {
                Minecraft.getMinecraft().fontRenderer.drawString(
                        "Press [G] for A.C.E. Options", 5, 5, 0xFFFFFF);
            }
        }
    }
}