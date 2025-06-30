package com.iamfmgod.armoredcombatenhancer.modules.progression;

public interface IPlayerProgression {
    int getDashTier();
    int getBashTier();
    int getCooldownTier();
    int getPoints();

    void addPoints(int pts);
    boolean upgradeDash();
    boolean upgradeBash();
    boolean upgradeCooldown();

    // ← setters needed for Storage.readNBT(...)
    void setDashTier(int tier);
    void setBashTier(int tier);
    void setCooldownTier(int tier);
    void setPoints(int pts);
}