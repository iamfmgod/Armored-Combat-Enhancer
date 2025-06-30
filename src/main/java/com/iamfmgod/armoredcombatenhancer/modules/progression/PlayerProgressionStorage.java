package com.iamfmgod.armoredcombatenhancer.modules.progression;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerProgressionStorage implements IStorage<IPlayerProgression> {
    @Override
    public NBTBase writeNBT(Capability<IPlayerProgression> cap,
                            IPlayerProgression inst,
                            EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("DashTier",     inst.getDashTier());
        tag.setInteger("BashTier",     inst.getBashTier());
        tag.setInteger("CooldownTier", inst.getCooldownTier());
        tag.setInteger("Points",       inst.getPoints());
        return tag;
    }

    @Override
    public void readNBT(Capability<IPlayerProgression> cap,
                        IPlayerProgression inst,
                        EnumFacing side,
                        NBTBase nbt) {
        if (!(nbt instanceof NBTTagCompound)) return;
        NBTTagCompound tag = (NBTTagCompound) nbt;
        inst.setDashTier(    tag.getInteger("DashTier"));
        inst.setBashTier(    tag.getInteger("BashTier"));
        inst.setCooldownTier(tag.getInteger("CooldownTier"));
        inst.setPoints(      tag.getInteger("Points"));
    }
}