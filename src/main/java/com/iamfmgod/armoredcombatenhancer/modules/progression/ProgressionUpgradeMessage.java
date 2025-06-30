package com.iamfmgod.armoredcombatenhancer.modules.progression;

import com.iamfmgod.armoredcombatenhancer.ArmoredCombatEnhancer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

/**
 * Sent from client → server when the player clicks an “upgrade” button.
 */
public class ProgressionUpgradeMessage implements IMessage {
    private String ability;

    public ProgressionUpgradeMessage() {}

    public ProgressionUpgradeMessage(String ability) {
        this.ability = ability;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, ability);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ability = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<ProgressionUpgradeMessage, IMessage> {
        @Override
        public IMessage onMessage(ProgressionUpgradeMessage msg, MessageContext ctx) {
            // Enqueue on the main server thread
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                IPlayerProgression prog = player.getCapability(
                        PlayerProgressionProvider.CAP, null
                );
                if (prog == null) return;

                // Attempt the selected upgrade
                switch (msg.ability) {
                    case "dash":     prog.upgradeDash();     break;
                    case "bash":     prog.upgradeBash();     break;
                    case "cooldown": prog.upgradeCooldown(); break;
                }

                // Always re‐sync state back to the client
                ArmoredCombatEnhancer.NETWORK.sendTo(
                        new ProgressionSyncMessage(prog),
                        player
                );
            });
            return null;
        }
    }
}