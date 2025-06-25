package com.iamfmgod.armoredcombatenhancer;

import com.iamfmgod.armoredcombatenhancer.client.ModKeyBindings;
import com.iamfmgod.armoredcombatenhancer.modules.combat.CombatModule;
import com.iamfmgod.armoredcombatenhancer.modules.compatibility.CompatibilityModule;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.effects.StunHandler;
import com.iamfmgod.armoredcombatenhancer.modules.movement.MovementModule;
import com.iamfmgod.armoredcombatenhancer.modules.network.NetworkModule;
import com.iamfmgod.armoredcombatenhancer.modules.ui.UIModule;
import com.iamfmgod.armoredcombatenhancer.modules.utils.UtilsModule;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

@Mod(
        modid   = ArmoredCombatEnhancer.MODID,
        name    = ArmoredCombatEnhancer.NAME,
        version = ArmoredCombatEnhancer.VERSION
)
public class ArmoredCombatEnhancer {

    public static final String MODID   = "armoredcombatenhancer";
    public static final String NAME    = "Armored Combat Enhancer";
    public static final String VERSION = "1.0.2";

    // Core modules
    private final CombatModule   combatModule   = new CombatModule();
    private final MovementModule movementModule = new MovementModule();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 1) Load & watch JSON config
        ConfigModule.reload();  // <-- now copies defaults & parses movement/weapons/armors/compat
        UtilsModule.debugLog("Configuration loaded.");

        // 2) Compatibility flags
        CompatibilityModule.preInit(event);
        UtilsModule.debugLog("Compatibility initialized.");

        // 3) Stun system
        StunHandler.register();
        UtilsModule.debugLog("Stun handler registered.");

        // 4) Networking (registers ShieldBashMessage → ShieldBashHandler)
        NetworkModule.init(MODID);
        UtilsModule.debugLog("Network initialized.");

        // 5) Register core event listeners
        MinecraftForge.EVENT_BUS.register(combatModule);
        MinecraftForge.EVENT_BUS.register(movementModule);
        UtilsModule.debugLog("Core modules registered.");

        // 6) Client‐only setup
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            // Key bindings: GUI, reload config, dash, shield bash
            ModKeyBindings.init();
            MinecraftForge.EVENT_BUS.register(new ModKeyBindings());

            // UI overlay/tooltips
            MinecraftForge.EVENT_BUS.register(new UIModule());
            UtilsModule.debugLog("Client UI and keybindings initialized.");
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        CompatibilityModule.init(event);
        UtilsModule.debugLog("Initialization complete.");
    }
}