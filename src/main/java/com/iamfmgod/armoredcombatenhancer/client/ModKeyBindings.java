package com.iamfmgod.armoredcombatenhancer.client;

import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.movement.MovementModule;
import com.iamfmgod.armoredcombatenhancer.client.gui.GuiArmoredCombatEnhancer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ModKeyBindings {

    public static KeyBinding openGuiKey;
    public static KeyBinding reloadConfigKey;
    public static KeyBinding dashKey;

    // Variables for double-tap detection for dodge on left/right.
    private static long lastLeftPressTime = 0;
    private static long lastRightPressTime = 0;
    private static final long DOUBLE_TAP_THRESHOLD = 250; // milliseconds

    public static void init() {
        openGuiKey = new KeyBinding("key.armoredCombatEnhancer.gui", Keyboard.KEY_G, "key.categories.armoredcombatenhancer");
        reloadConfigKey = new KeyBinding("key.armoredCombatEnhancer.reload", Keyboard.KEY_R, "key.categories.armoredcombatenhancer");
        dashKey = new KeyBinding("key.armoredCombatEnhancer.dash", Keyboard.KEY_F, "key.categories.armoredcombatenhancer");

        ClientRegistry.registerKeyBinding(openGuiKey);
        ClientRegistry.registerKeyBinding(reloadConfigKey);
        ClientRegistry.registerKeyBinding(dashKey);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (openGuiKey.isPressed()) {
            mc.displayGuiScreen(new GuiArmoredCombatEnhancer());
        }

        if (reloadConfigKey.isPressed()) {
            ConfigModule.loadConfig();
            mc.player.sendMessage(new TextComponentString("A.C.E. config reloaded."));
        }

        if (dashKey.isPressed()) {
            new MovementModule().triggerDash(mc.player);
        }

        // Detect double-tap for dodge on the left key (A).
        if (Keyboard.getEventKey() == Keyboard.KEY_A && Keyboard.getEventKeyState()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastLeftPressTime < DOUBLE_TAP_THRESHOLD) {
                new MovementModule().triggerDodge(mc.player, true);
                lastLeftPressTime = 0;
            } else {
                lastLeftPressTime = currentTime;
            }
        }

        // Detect double-tap for dodge on the right key (D).
        if (Keyboard.getEventKey() == Keyboard.KEY_D && Keyboard.getEventKeyState()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRightPressTime < DOUBLE_TAP_THRESHOLD) {
                new MovementModule().triggerDodge(mc.player, false);
                lastRightPressTime = 0;
            } else {
                lastRightPressTime = currentTime;
            }
        }
    }
}