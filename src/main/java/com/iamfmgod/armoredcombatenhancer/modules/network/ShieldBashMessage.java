// ShieldBashMessage.java
package com.iamfmgod.armoredcombatenhancer.modules.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ShieldBashMessage implements IMessage {
    public ShieldBashMessage() {}

    @Override
    public void toBytes(ByteBuf buf) {
        // No data
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // No data
    }
}