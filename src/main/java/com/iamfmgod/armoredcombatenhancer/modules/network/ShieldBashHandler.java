// ShieldBashHandler.java
package com.iamfmgod.armoredcombatenhancer.modules.network;

import com.iamfmgod.armoredcombatenhancer.modules.movement.MovementModule;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ShieldBashHandler implements IMessageHandler<ShieldBashMessage, IMessage> {

    @Override
    public IMessage onMessage(ShieldBashMessage msg, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        // schedule on main thread
        player.getServerWorld().addScheduledTask(() -> {
            System.out.println("[A.C.E.] ShieldBashHandler.onMessage fired for "
                    + player.getName());
            MovementModule.triggerShieldBash(player);
        });
        return null;
    }

}