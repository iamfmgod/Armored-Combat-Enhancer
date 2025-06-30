package com.iamfmgod.armoredcombatenhancer.commands;

import com.iamfmgod.armoredcombatenhancer.ArmoredCombatEnhancer;
import com.iamfmgod.armoredcombatenhancer.modules.progression.IPlayerProgression;
import com.iamfmgod.armoredcombatenhancer.modules.progression.PlayerProgressionProvider;
import com.iamfmgod.armoredcombatenhancer.modules.progression.ProgressionSyncMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ACECommand extends CommandBase {
    @Override
    public String getName() {
        return "ace";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ace upgrade <dash|bash|cooldown>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // only players with a progression capability can use this
        if (!(sender instanceof EntityPlayerMP) || args.length < 2) return;
        EntityPlayerMP player = (EntityPlayerMP) sender;
        IPlayerProgression prog = player.getCapability(PlayerProgressionProvider.CAP, null);
        if (prog == null) return;

        // only "upgrade" subcommand is supported
        if (!"upgrade".equalsIgnoreCase(args[0])) {
            player.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
            return;
        }

        // which ability?
        String ability = args[1].toLowerCase();
        boolean success;
        switch (ability) {
            case "dash":
                success = prog.upgradeDash();
                break;
            case "bash":
                success = prog.upgradeBash();
                break;
            case "cooldown":
                success = prog.upgradeCooldown();
                break;
            default:
                player.sendMessage(new TextComponentString("Unknown ability: " + ability));
                return;
        }

        // feedback & re-sync
        player.sendMessage(new TextComponentString(
                success
                        ? "Upgraded " + ability
                        : "Cannot upgrade " + ability
        ));
        ArmoredCombatEnhancer.NETWORK.sendTo(
                new ProgressionSyncMessage(prog),
                player
        );
    }
}