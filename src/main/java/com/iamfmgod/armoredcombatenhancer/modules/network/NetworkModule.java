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

        // Register ShieldBash → server side
        NETWORK.registerMessage(
            ShieldBashHandler.class,
            ShieldBashMessage.class,
            nextPacketId(),
            Side.SERVER
        );

        // Register ProgressionSync → client side
        NETWORK.registerMessage(
            com.iamfmgod.armoredcombatenhancer.modules.progression.ProgressionSyncMessage.Handler.class,
            com.iamfmgod.armoredcombatenhancer.modules.progression.ProgressionSyncMessage.class,
            nextPacketId(),
            Side.CLIENT
        );
    }

    /**
     * Returns the next unique packet ID.
     */
    public static int nextPacketId() {
        return packetId++;
    }

    /**
     * Safely send a packet to the server, with null check.
     */
    public static void sendToServerSafe(net.minecraftforge.fml.common.network.simpleimpl.IMessage message) {
        if (NETWORK != null) {
            NETWORK.sendToServer(message);
        } else {
            System.err.println("[A.C.E.] NETWORK is null! Packet not sent.");
        }
    }
}