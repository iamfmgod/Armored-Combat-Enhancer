package com.iamfmgod.armoredcombatenhancer.modules.progression;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class ProgressionSyncMessage implements IMessage {
    private int dash, bash, cooldown, points;

    public ProgressionSyncMessage() {}

    public ProgressionSyncMessage(IPlayerProgression prog) {
        this.dash     = prog.getDashTier();
        this.bash     = prog.getBashTier();
        this.cooldown = prog.getCooldownTier();
        this.points   = prog.getPoints();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dash);
        buf.writeInt(bash);
        buf.writeInt(cooldown);
        buf.writeInt(points);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dash     = buf.readInt();
        bash     = buf.readInt();
        cooldown = buf.readInt();
        points   = buf.readInt();
    }

    public static class Handler implements IMessageHandler<ProgressionSyncMessage, IMessage> {
        @Override
        public IMessage onMessage(ProgressionSyncMessage msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player == null) return;

                IPlayerProgression prog = player.getCapability(
                        PlayerProgressionProvider.CAP, null
                );

                if (prog != null) {
                    System.out.println("[ACE] Received ProgressionSyncMessage from server");
                    prog.setPoints(msg.points);
                    prog.setDashTier(msg.dash);
                    prog.setBashTier(msg.bash);
                    prog.setCooldownTier(msg.cooldown);
                } else {
                    System.err.println("[ACE] Client capability was null during sync.");
                }
            });
            return null;
        }
    }
}