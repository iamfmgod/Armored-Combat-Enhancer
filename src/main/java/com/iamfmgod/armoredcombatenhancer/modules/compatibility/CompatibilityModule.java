package com.iamfmgod.armoredcombatenhancer.modules.compatibility;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Auto-detects common mods at runtime and initializes
 * their compatibility layers if present.
 */
public class CompatibilityModule {
    public static boolean isTConstructLoaded;
    public static boolean isBotaniaLoaded;
    public static boolean isThaumcraftLoaded;
    public static boolean isBloodMagicLoaded;
    public static boolean isDraconicEvolutionLoaded;
    // add as you need…

    /**
     * Call during preInit to detect which mods are present
     * and run any per-mod preInit hooks.
     */
    public static void preInit(FMLPreInitializationEvent event) {
        isTConstructLoaded        = Loader.isModLoaded("tconstruct");
        isBotaniaLoaded           = Loader.isModLoaded("botania");
        isThaumcraftLoaded        = Loader.isModLoaded("thaumcraft");
        isBloodMagicLoaded        = Loader.isModLoaded("bloodmagic");
        isDraconicEvolutionLoaded = Loader.isModLoaded("draconicevolution");
        // …and so on for any other mod IDs

        if (isTConstructLoaded) {
            TConstructCompat.preInit(event);
        }
        if (isBotaniaLoaded) {
            BotaniaCompat.preInit(event);
        }
        if (isThaumcraftLoaded) {
            ThaumcraftCompat.preInit(event);
        }
        if (isBloodMagicLoaded) {
            BloodMagicCompat.preInit(event);
        }
        if (isDraconicEvolutionLoaded) {
            DraconicEvolutionCompat.preInit(event);
        }
    }

    /**
     * Call during init to finalize compatibility setup.
     */
    public static void init(FMLInitializationEvent event) {
        if (isTConstructLoaded) {
            TConstructCompat.init(event);
        }
        if (isBotaniaLoaded) {
            BotaniaCompat.init(event);
        }
        if (isThaumcraftLoaded) {
            ThaumcraftCompat.init(event);
        }
        if (isBloodMagicLoaded) {
            BloodMagicCompat.init(event);
        }
        if (isDraconicEvolutionLoaded) {
            DraconicEvolutionCompat.init(event);
        }
    }
}