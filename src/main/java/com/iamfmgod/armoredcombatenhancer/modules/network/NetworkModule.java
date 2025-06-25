package com.iamfmgod.armoredcombatenhancer.modules.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

// Import your packet and handler from the correct package
import com.iamfmgod.armoredcombatenhancer.modules.network.ShieldBashHandler;
import com.iamfmgod.armoredcombatenhancer.modules.network.ShieldBashMessage;

public class NetworkModule {

    // The channel for all your mod’s packets
    public static SimpleNetworkWrapper NETWORK;

    // Incremental packet ID
    private static int packetId = 0;

    /**
     * Call this during your preInit (using your MODID) to set up networking.
     */
    public static void init(String modId) {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(modId);

        // Register the ShieldBashMessage → handled server‐side
        NETWORK.registerMessage(
                ShieldBashHandler.class,  // Handler class
                ShieldBashMessage.class,  // Message class
                nextPacketId(),           // Discriminator
                Side.SERVER               // Handled on the server
        );

        // Register other messages here as needed:
        // NETWORK.registerMessage(OtherHandler.class, OtherMessage.class,
        //                         nextPacketId(), Side.CLIENT);
    }

    /**
     * Returns the next unique packet ID.
     */
    public static int nextPacketId() {
        return packetId++;
    }
}