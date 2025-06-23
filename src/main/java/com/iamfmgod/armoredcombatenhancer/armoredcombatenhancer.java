package com.iamfmgod.armoredcombatenhancer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

import com.iamfmgod.armoredcombatenhancer.client.ModKeyBindings;
import com.iamfmgod.armoredcombatenhancer.modules.combat.CombatModule;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.compatibility.CompatibilityModule;
import com.iamfmgod.armoredcombatenhancer.modules.effects.EffectsModule;
import com.iamfmgod.armoredcombatenhancer.modules.effects.StunHandler;
import com.iamfmgod.armoredcombatenhancer.modules.movement.MovementModule;
import com.iamfmgod.armoredcombatenhancer.modules.network.NetworkModule;
import com.iamfmgod.armoredcombatenhancer.modules.ui.UIModule;
import com.iamfmgod.armoredcombatenhancer.modules.utils.UtilsModule;

@Mod(
        modid   = armoredcombatenhancer.MODID,
        name    = armoredcombatenhancer.NAME,
        version = armoredcombatenhancer.VERSION
)
public class armoredcombatenhancer {
    public static final String MODID   = "armoredcombatenhancer";
    public static final String NAME    = "Armored Combat Enhancer";
    public static final String VERSION = "1.0.0";

    private final CombatModule   combatModule   = new CombatModule();
    private final MovementModule movementModule = new MovementModule();
    private final UIModule       uiModule       = new UIModule();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 1) Load JSON config (weapons, armor, effects, weights)
        ConfigModule.loadConfig();
        UtilsModule.debugLog("Configuration loaded.");

        // 2) Initialize compat flags & register any custom effect potions
        CompatibilityModule.preInit(event);
        UtilsModule.debugLog("Compatibility settings initialized.");

        // Example hook: register mod-specific potions for effects
        // if (CompatibilityModule.isBotaniaLoaded) {
        //     EffectsModule.registerCustomEffect("thornsaura", BotaniaPotions.THORN);
        // }

        // 3) Register our stun handler so "Stun" is a true lock-down
        StunHandler.register();
        UtilsModule.debugLog("StunHandler registered for true stuns.");

        // 4) Wire up networking, gameplay, and UI
        NetworkModule.init(MODID);
        UtilsModule.debugLog("Network module initialized.");

        MinecraftForge.EVENT_BUS.register(combatModule);
        MinecraftForge.EVENT_BUS.register(movementModule);
        MinecraftForge.EVENT_BUS.register(uiModule);
        UtilsModule.debugLog("Module event listeners registered.");

        // Client-only setup: keybindings & UI events
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            ModKeyBindings.init();
            MinecraftForge.EVENT_BUS.register(new ModKeyBindings());
            UtilsModule.debugLog("Key bindings initialized and event handler registered.");
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        CompatibilityModule.init(event);
        UtilsModule.debugLog("Initialization complete.");
    }
}