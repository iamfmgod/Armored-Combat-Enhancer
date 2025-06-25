package com.iamfmgod.armoredcombatenhancer;

/**
 * Central place for user toggles and default mod behavior.
 * These values can be adjusted in-game or respected by subsystems at runtime.
 */
public class ModSettings {

    // GUI toggle: Enables stagger effect on crit hits
    public static boolean isStaggerEnabled    = true;

    // Multiplier applied to all armor weight effects (e.g. 0.8 = 20% lighter)
    public static float   armorWeightMultiplier = 1.0F;

    // Base fallback critical hit chance (if no config match)
    public static float   baseCritChance       = 0.1F;

    // Base fallback critical hit damage multiplier
    public static float   baseCritMultiplier   = 1.5F;

    // Duration of the stagger effect applied on crit (in ticks)
    public static int     staggerDurationTicks = 10;

    // Generic cooldown for dash/dodge movement abilities (in ticks)
    // (maintained for backward compatibility – shield bash has its own cooldown below)
    public static int     movementCooldown     = 40;

    // ────────────────────────────────────────────────────
    // Shield‐Bash movement settings (defaults can be overridden via JSON)
    // ────────────────────────────────────────────────────

    /** How much damage the shield bash deals (2 hearts = 4.0f) */
    public static float   shieldBashDamage     = 4.0F;

    /** The force applied by the shield bash (Knockback V = 5.0f) */
    public static float   shieldBashKnockback  = 5.0F;

    /** How far (in blocks) your bash will detect targets */
    public static double  shieldBashRange      = 4.0D;

    /** How fast the client dash is after you bash */
    public static double  shieldDashSpeed      = 1.0D;

    /** Cooldown (in ticks) before you can bash again */
    public static int     shieldBashCooldown   = 40;

    // Future: enable GUI scaling, debug prints, client-only preferences, etc.
}