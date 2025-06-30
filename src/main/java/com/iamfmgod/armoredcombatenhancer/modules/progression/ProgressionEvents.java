package com.iamfmgod.armoredcombatenhancer.modules.progression;

import com.iamfmgod.armoredcombatenhancer.ArmoredCombatEnhancer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.monster.IMob;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class ProgressionEvents {
    private static final HashMap<UUID, Integer> killTracker = new HashMap<>();

    @SubscribeEvent
    public void onMobKill(LivingDeathEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayerMP)) return;
        if (!(event.getEntity() instanceof IMob)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
        UUID uuid = player.getUniqueID();

        int kills = killTracker.getOrDefault(uuid, 0) + 1;
        killTracker.put(uuid, kills);

        if (kills >= 10) {
            killTracker.put(uuid, 0); // reset counter
            IPlayerProgression prog = player.getCapability(PlayerProgressionProvider.CAP, null);
            if (prog != null) {
                prog.addPoints(1);
                ArmoredCombatEnhancer.NETWORK.sendTo(new ProgressionSyncMessage(prog), player);
                System.out.println("[ACE] " + player.getName() + " earned +1 point for 10 kills");
            }
        }
    }
}