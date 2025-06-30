package com.iamfmgod.armoredcombatenhancer;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.iamfmgod.armoredcombatenhancer.client.ModKeyBindings;
import com.iamfmgod.armoredcombatenhancer.commands.ACECommand;
import com.iamfmgod.armoredcombatenhancer.modules.combat.CombatModule;
import com.iamfmgod.armoredcombatenhancer.modules.compatibility.CompatibilityModule;
import com.iamfmgod.armoredcombatenhancer.modules.config.ConfigModule;
import com.iamfmgod.armoredcombatenhancer.modules.effects.StunHandler;
import com.iamfmgod.armoredcombatenhancer.modules.movement.MovementModule;
import com.iamfmgod.armoredcombatenhancer.modules.network.NetworkModule;
import com.iamfmgod.armoredcombatenhancer.modules.progression.*;
import com.iamfmgod.armoredcombatenhancer.modules.ui.UIModule;
import com.iamfmgod.armoredcombatenhancer.modules.utils.UtilsModule;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid   = ArmoredCombatEnhancer.MODID,
        name    = ArmoredCombatEnhancer.NAME,
        version = ArmoredCombatEnhancer.VERSION
)
public class ArmoredCombatEnhancer {
    public static final String MODID   = "armoredcombatenhancer";
    public static final String NAME    = "Armored Combat Enhancer";
    public static final String VERSION = "1.0.3";

    private final CombatModule   combatModule   = new CombatModule();
    private final MovementModule movementModule = new MovementModule();

    public static SimpleNetworkWrapper NETWORK;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 1) Load config
        ConfigModule.reload();
        UtilsModule.debugLog("Configuration loaded.");

        // 2) Compatibility hooks
        CompatibilityModule.preInit(event);
        UtilsModule.debugLog("Compatibility initialized.");

        // 3) Event-driven stun system
        StunHandler.register();
        UtilsModule.debugLog("Stun handler registered.");

        // 4) Networking
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        int id = 0;
        if (event.getSide().isClient()) {
            NETWORK.registerMessage(
                ProgressionSyncMessage.Handler.class,
                ProgressionSyncMessage.class,
                id++, Side.CLIENT
            );
        }
        NETWORK.registerMessage(
                ProgressionUpgradeMessage.Handler.class,
                ProgressionUpgradeMessage.class,
                id++, Side.SERVER
        );
        UtilsModule.debugLog("Network initialized.");

        // 5) Global event listeners
        MinecraftForge.EVENT_BUS.register(combatModule);
        MinecraftForge.EVENT_BUS.register(movementModule);
        MinecraftForge.EVENT_BUS.register(PlayerProgressionProvider.class);
        MinecraftForge.EVENT_BUS.register(new ProgressionEvents());
        UtilsModule.debugLog("Core modules registered.");

        // 6) Capability registration
        CapabilityManager.INSTANCE.register(
                IPlayerProgression.class,
                new PlayerProgressionStorage(),
                PlayerProgression::new
        );
        UtilsModule.debugLog("Progression capability registered.");

        // 7) Client-only
        if (event.getSide() == Side.CLIENT) {
            ModKeyBindings.init();
            MinecraftForge.EVENT_BUS.register(new ModKeyBindings());
            MinecraftForge.EVENT_BUS.register(new UIModule());
            UtilsModule.debugLog("Client UI & keybindings initialized.");
        }

        // 8) Mod instance event registration (for @SubscribeEvent methods)
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        CompatibilityModule.init(event);
        UtilsModule.debugLog("Initialization complete.");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new ACECommand());
        UtilsModule.debugLog("/ace command registered.");
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent ev) {
        if (ev.player instanceof EntityPlayerMP) {
            EntityPlayerMP mp = (EntityPlayerMP) ev.player;
            IPlayerProgression prog = mp.getCapability(PlayerProgressionProvider.CAP, null);
            if (prog != null) {
                NETWORK.sendTo(new ProgressionSyncMessage(prog), mp);
                System.out.println("[ACE] Sent progression sync to " + mp.getName());
            }
        }
    }
}