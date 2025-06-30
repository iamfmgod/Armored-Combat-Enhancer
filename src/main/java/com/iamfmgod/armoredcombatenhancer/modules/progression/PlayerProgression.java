package com.iamfmgod.armoredcombatenhancer.modules.progression;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Holds a player’s progression points & tiers,
 * serialises itself to NBT for the capability system.
 */
public class PlayerProgression implements IPlayerProgression, INBTSerializable<NBTTagCompound> {
    private int points       = 0;
    private int dashTier     = 0;
    private int bashTier     = 0;
    private int cooldownTier = 0;
    private static final int MAX_TIER = 3;

    // getters
    @Override public int getPoints()          { return points; }
    @Override public int getDashTier()        { return dashTier; }
    @Override public int getBashTier()        { return bashTier; }
    @Override public int getCooldownTier()    { return cooldownTier; }

    // setters (newly implemented)
    @Override public void setPoints(int pts)  { this.points = Math.max(0, pts); }
    @Override public void setDashTier(int t)  { this.dashTier = Math.max(0, Math.min(t, MAX_TIER)); }
    @Override public void setBashTier(int t)  { this.bashTier = Math.max(0, Math.min(t, MAX_TIER)); }
    @Override public void setCooldownTier(int t) { this.cooldownTier = Math.max(0, Math.min(t, MAX_TIER)); }

    // upgrades
    @Override
    public boolean upgradeDash() {
        if (points <= 0 || dashTier >= MAX_TIER) return false;
        dashTier++; points--; return true;
    }
    @Override
    public boolean upgradeBash() {
        if (points <= 0 || bashTier >= MAX_TIER) return false;
        bashTier++; points--; return true;
    }
    @Override
    public boolean upgradeCooldown() {
        if (points <= 0 || cooldownTier >= MAX_TIER) return false;
        cooldownTier++; points--; return true;
    }

    // point operations
    @Override public void addPoints(int amt) { setPoints(this.points + amt); }

    // NBT serialization
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Points",       points);
        nbt.setInteger("DashTier",     dashTier);
        nbt.setInteger("BashTier",     bashTier);
        nbt.setInteger("CooldownTier", cooldownTier);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.points       = nbt.getInteger("Points");
        this.dashTier     = nbt.getInteger("DashTier");
        this.bashTier     = nbt.getInteger("BashTier");
        this.cooldownTier = nbt.getInteger("CooldownTier");
    }
}