package com.iamfmgod.armoredcombatenhancer.modules.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.compatibility.CompatibilityModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class UIModule {

    private final long overlayStartTime = System.currentTimeMillis();
    private static final int DISPLAY_DURATION_MS = 5000;

    public UIModule() {
        System.out.println("[A.C.E.] UIModule initialized.");
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<String> tooltips = event.getToolTip();
        boolean matched = false, hasNBT = false, modded = false;

        // NBT details first…
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("ArmorWeight")) {
                float w = stack.getTagCompound().getFloat("ArmorWeight");
                tooltips.add(TextFormatting.GRAY + "NBT Weight: " + w + TextFormatting.RESET);
                hasNBT = true;
            }
            if (stack.getTagCompound().hasKey("DynamicEffects")) {
                String fx = stack.getTagCompound().getString("DynamicEffects");
                tooltips.add(TextFormatting.GRAY + "NBT Effects: " + fx + TextFormatting.RESET);
                hasNBT = true;
            }
        }

        // fetch registry identifiers
        ResourceLocation rl = stack.getItem().getRegistryName();
        String fullName = rl != null ? rl.toString().toLowerCase() : "";
        String domain   = rl != null ? rl.getResourceDomain().toLowerCase() : "";
        String path     = rl != null ? rl.getResourcePath().toLowerCase()   : "";

        // weapons
        for (String key : ConfigModule.weaponConfigs.keySet()) {
            if (fullName.contains(key) || path.contains(key)) {
                JsonObject wc = ConfigModule.getWeaponConfig(key);
                if (wc.has("critChance")) {
                    int pct = Math.round(wc.get("critChance").getAsFloat() * 100);
                    tooltips.add(TextFormatting.YELLOW + "Crit Chance: " + pct + "%" + TextFormatting.RESET);
                }
                if (wc.has("effects")) {
                    JsonArray arr = wc.getAsJsonArray("effects");
                    for (JsonElement e : arr) {
                        JsonObject fx = e.getAsJsonObject();
                        String  nm   = fx.get("name").getAsString();
                        int     lvl  = fx.get("level").getAsInt();
                        int     dur  = fx.get("duration").getAsInt();
                        int     ch   = Math.round(fx.get("chance").getAsFloat() * 100);
                        tooltips.add(TextFormatting.GRAY +
                                "On Hit: " + nm + " Lv." + lvl + " (" + ch + "%, " + dur + "t)" +
                                TextFormatting.RESET);
                    }
                }
                matched = true;
                break;
            }
        }

        // armors
        for (String key : ConfigModule.armorConfigs.keySet()) {
            if (fullName.contains(key) || path.contains(key)) {
                JsonObject ac = ConfigModule.getArmorConfig(key);
                if (ac.has("armorWeight")) {
                    tooltips.add(TextFormatting.GRAY +
                            "Armor Weight: " + ac.get("armorWeight").getAsFloat() +
                            TextFormatting.RESET);
                }
                if (ac.has("dynamicEffects")) {
                    JsonArray arr = ac.getAsJsonArray("dynamicEffects");
                    for (JsonElement e : arr) {
                        JsonObject fx = e.getAsJsonObject();
                        String nm  = fx.get("name").getAsString();
                        int    lvl = fx.get("level").getAsInt();
                        int    dur = fx.get("duration").getAsInt();
                        int    ch  = Math.round(fx.get("chance").getAsFloat() * 100);
                        tooltips.add(TextFormatting.GRAY +
                                "Passive: " + nm + " Lv." + lvl + " (" + ch + "%, " + dur + "t)" +
                                TextFormatting.RESET);
                    }
                }
                matched = true;
                break;
            }
        }



        if (matched || hasNBT || modded) {
            tooltips.add(TextFormatting.DARK_GREEN + "Enhanced by A.C.E." + TextFormatting.RESET);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post ev) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null && ev.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            long elapsed = System.currentTimeMillis() - overlayStartTime;
            if (elapsed <= DISPLAY_DURATION_MS) {
                mc.fontRenderer.drawString("Press [G] for A.C.E. Options", 5, 5, 0xFFFFFF);
            }
        }
    }
}