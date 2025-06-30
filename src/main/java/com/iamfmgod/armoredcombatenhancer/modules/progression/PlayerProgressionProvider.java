package com.iamfmgod.armoredcombatenhancer.modules.progression;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerProgressionProvider implements ICapabilitySerializable<NBTTagCompound> {
    public static final ResourceLocation ID =
            new ResourceLocation("armoredcombatenhancer", "progression");

    @CapabilityInject(IPlayerProgression.class)
    public static Capability<IPlayerProgression> CAP = null;

    private IPlayerProgression inst;

    public PlayerProgressionProvider() {
        if (CAP != null) {
            this.inst = CAP.getDefaultInstance();
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CAP;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CAP ? (T) inst : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return inst != null ? (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null)
                : new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (inst != null) {
            CAP.getStorage().readNBT(CAP, inst, null, nbt);
        }
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> ev) {
        if (ev.getObject() instanceof EntityPlayer) {
            ev.addCapability(ID, new PlayerProgressionProvider());
        }
    }
}