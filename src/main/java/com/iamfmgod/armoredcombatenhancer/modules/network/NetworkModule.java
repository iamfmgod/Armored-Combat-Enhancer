package com.iamfmgod.armoredcombatenhancer.modules.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkModule {

    // Do not initialize eagerly.
    public static SimpleNetworkWrapper NETWORK;

    private static int packetId = 0;

    /**
     * Call this method during your mod's preInit phase.
     * @param modId Your mod ID (e.g., armoredcombatenhancer.MODID).
     */
    public static void init(String modId) {
        // Initialize the channel using your mod ID.
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(modId);

        // Example registrations:
        // NETWORK.registerMessage(PacketExample.Handler.class, PacketExample.class, nextPacketId(), Side.SERVER);
        // NETWORK.registerMessage(PacketExample.Handler.class, PacketExample.class, nextPacketId(), Side.CLIENT);
    }

    public static int nextPacketId() {
        return packetId++;
    }
}