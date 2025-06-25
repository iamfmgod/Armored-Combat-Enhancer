package com.iamfmgod.armoredcombatenhancer.client;

import com.iamfmgod.armoredcombatenhancer.client.gui.GuiArmoredCombatEnhancer;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.movement.MovementModule;
import com.iamfmgod.armoredcombatenhancer.modules.network.NetworkModule;
import com.iamfmgod.armoredcombatenhancer.modules.network.ShieldBashMessage;
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
    public static KeyBinding shieldBashKey;

    private static long lastLeftTap  = 0;
    private static long lastRightTap = 0;
    private static final long DOUBLE_TAP_THRESHOLD = 250;

    public static void init() {
        openGuiKey      = new KeyBinding("key.ace.gui",
                Keyboard.KEY_G,
                "key.categories.ace");
        reloadConfigKey = new KeyBinding("key.ace.reload",
                Keyboard.KEY_R,
                "key.categories.ace");
        dashKey         = new KeyBinding("key.ace.dash",
                Keyboard.KEY_F,
                "key.categories.ace");
        shieldBashKey   = new KeyBinding("key.ace.shieldbash",
                Keyboard.KEY_V,
                "key.categories.ace");

        ClientRegistry.registerKeyBinding(openGuiKey);
        ClientRegistry.registerKeyBinding(reloadConfigKey);
        ClientRegistry.registerKeyBinding(dashKey);
        ClientRegistry.registerKeyBinding(shieldBashKey);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        // Open the A.C.E. GUI
        if (openGuiKey.isPressed()) {
            mc.displayGuiScreen(new GuiArmoredCombatEnhancer());
        }

        // Reload JSON config
        if (reloadConfigKey.isPressed()) {
            ConfigModule.reload();  // <-- use reload() here
            mc.player.sendMessage(new TextComponentString("A.C.E. config reloaded."));
        }

        // Forward Dash
        if (dashKey.isPressed()) {
            MovementModule.triggerDash(mc.player);
        }

        // Shield‐bash key
        if (shieldBashKey.isPressed()) {
            // client‐side dash + particles + sound
            MovementModule.triggerShieldBash(mc.player);
            // server‐side authoritative knockback
            NetworkModule.NETWORK.sendToServer(new ShieldBashMessage());
        }

        // Double‐tap dodge left
        int keyCode = Keyboard.getEventKey();
        boolean down = Keyboard.getEventKeyState();
        if (down && keyCode == mc.gameSettings.keyBindLeft.getKeyCode()) {
            long now = System.currentTimeMillis();
            if (now - lastLeftTap < DOUBLE_TAP_THRESHOLD) {
                MovementModule.triggerDodge(mc.player, true);
                lastLeftTap = 0;
            } else {
                lastLeftTap = now;
            }
        }

        // Double‐tap dodge right
        if (down && keyCode == mc.gameSettings.keyBindRight.getKeyCode()) {
            long now = System.currentTimeMillis();
            if (now - lastRightTap < DOUBLE_TAP_THRESHOLD) {
                MovementModule.triggerDodge(mc.player, false);
                lastRightTap = 0;
            } else {
                lastRightTap = now;
            }
        }
    }
}