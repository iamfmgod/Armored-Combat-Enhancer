package com.iamfmgod.armoredcombatenhancer.client;

import com.iamfmgod.armoredcombatenhancer.ArmoredCombatEnhancer;
import com.iamfmgod.armoredcombatenhancer.client.gui.GuiArmoredCombatEnhancer;
import com.iamfmgod.armoredcombatenhancer.client.gui.GuiSkillTree;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.movement.MovementModule;
import com.iamfmgod.armoredcombatenhancer.modules.network.ShieldBashMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ModKeyBindings {
    public static KeyBinding openConfigGuiKey;
    public static KeyBinding openSkillsGuiKey;
    public static KeyBinding reloadConfigKey;
    public static KeyBinding dashKey;
    public static KeyBinding shieldBashKey;

    private static long lastLeftTap  = 0;
    private static long lastRightTap = 0;
    private static final long DOUBLE_TAP_THRESHOLD = 250;

    public static void init() {
        openConfigGuiKey  = new KeyBinding("key.ace.config_gui",   Keyboard.KEY_G, "key.categories.ace");
        openSkillsGuiKey  = new KeyBinding("key.ace.skills_gui",   Keyboard.KEY_K, "key.categories.ace");
        reloadConfigKey   = new KeyBinding("key.ace.reload_config",Keyboard.KEY_R, "key.categories.ace");
        dashKey           = new KeyBinding("key.ace.dash",         Keyboard.KEY_F, "key.categories.ace");
        shieldBashKey     = new KeyBinding("key.ace.shield_bash",  Keyboard.KEY_V, "key.categories.ace");

        ClientRegistry.registerKeyBinding(openConfigGuiKey);
        ClientRegistry.registerKeyBinding(openSkillsGuiKey);
        ClientRegistry.registerKeyBinding(reloadConfigKey);
        ClientRegistry.registerKeyBinding(dashKey);
        ClientRegistry.registerKeyBinding(shieldBashKey);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.currentScreen != null) return;

        if (openConfigGuiKey.isPressed()) {
            mc.displayGuiScreen(new GuiArmoredCombatEnhancer());
        }
        if (openSkillsGuiKey.isPressed()) {
            mc.displayGuiScreen(new GuiSkillTree());
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent evt) {
        if (evt.phase != ClientTickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        // reload config
        while (reloadConfigKey.isPressed()) {
            ConfigModule.reload();
            mc.player.sendMessage(new TextComponentString("A.C.E. config reloaded."));
        }

        // dash
        while (dashKey.isPressed()) {
            MovementModule.triggerDash(mc.player);
        }

        // shield‐bash
        while (shieldBashKey.isPressed()) {
            MovementModule.triggerShieldBash(mc.player);
            ArmoredCombatEnhancer.NETWORK.sendToServer(new ShieldBashMessage());
        }

        // double‐tap A/D
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
            long now = System.currentTimeMillis();
            if (now - lastLeftTap < DOUBLE_TAP_THRESHOLD) {
                MovementModule.triggerDodge(mc.player, true);
                lastLeftTap = 0;
            } else lastLeftTap = now;
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
            long now = System.currentTimeMillis();
            if (now - lastRightTap < DOUBLE_TAP_THRESHOLD) {
                MovementModule.triggerDodge(mc.player, false);
                lastRightTap = 0;
            } else lastRightTap = now;
        }
    }
}