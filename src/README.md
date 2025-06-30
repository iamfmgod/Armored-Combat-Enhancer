Armored Combat Enhancer
Armored Combat Enhancer is a Minecraft 1.12.2 mod that overhauls combat with dynamic armor‐weight penalties, critical‐hit effects, stagger mechanics, skill upgrades, and custom debuffs. Every piece of gear and every point spent shapes how you fight.

Features
Armor Weight System
- Dynamic Weight
  Armor pieces carry configurable weights—heavier sets slow you down, reduce knockback resistance, and can trigger debuffs if you exceed thresholds.
- Slot-Based Penalties
  Boots, leggings, chestplate, and helmet each contribute their own weight penalty.
- Configurable via JSON
  Define new armors, override default weights, or set global thresholds in config/armored_combat_config.json.
  Critical Hit & Stagger
- Custom Crit Effects
  Assign crit chance, crit-damage multiplier, and on-hit effects (bleed, stun, slowness, etc.) per weapon.
- Stagger on Crit
  Knock targets off-balance for a brief recovery window.
- Visual & Audio Feedback
  Particles and sound cues accompany special procs for instant feedback.
  Skill Tree & Progression
- Upgradeable Abilities
  Spend combat-earned points on three tiers each of:
- Dash (increased distance)
- Shield-Bash (damage & knockback)
- Cooldown (shorter cooldowns)
- In-Game Skill Tree GUI
  Press K to open a dedicated skill-upgrade screen.
  Heavy-Armor Mechanics
- Weight Thresholds
  Exceed a total-weight cap to get slowness, reduced regen, slower attacks, and extended stagger.
- Dynamic Extensibility
  New thresholds, penalties, or buff/debuffs on heavy gear can be tuned via JSON.
  Fluid Compatibility & Extensibility
- No Hard Dependencies
  Loads unconditionally—compatibility modules detect and hook into other mods at runtime (TConstruct, Botania, Thaumcraft, Blood Magic, Draconic Evolution, Mekanism, MythicMetals, etc.).
- JSON-Based Overrides
  Add or tweak weapon, armor, effect, and compatibility entries without recompiling.
  Smart Effect Stacking
- Auto-Refresh & Ramp
  Reapplying an active effect refreshes its duration or increments its level up to a cap, avoiding redundant stacks.

Key Bindings
- G → Open A.C.E. Settings GUI
- K → Open Skill Tree GUI
- R → Reload configuration JSON on the fly
- F → Dash (forward by default; hold A/D for lateral)
- Double-Tap A/D → Swift dodge left/right
- V → Shield-bash (damage, knockback, dash)
  All keybindings live in the ACE category in Controls.

Installation
- Install
  Place Armored-Combat-Enhancer.jar in your mods/ directory.
- Run
  Launch Minecraft 1.12.2 with Forge installed.
- Configure
  Edit config/armored_combat_config.json to add or tweak weapons, armors, effects, weights, and compatibility settings.
- Enjoy
  Gear up, fight smart, and watch how combat changes.

Project Structure
Armored-Combat-Enhancer/
├── build.gradle
├── mcreator.gradle
├── README.md
├── config/
│   └── armored_combat_config.json
└── src/
└── main/
├── java/com/iamfmgod/armoredcombatenhancer/
│   ├── ArmoredCombatEnhancer.java
│   ├── ModSettings.java
│   ├── client/
│   │   ├── gui/
│   │   │   ├── GuiArmoredCombatEnhancer.java
│   │   │   └── GuiSkillTree.java
│   │   ├── ClientTicker.java
│   │   └── ModKeyBindings.java
│   ├── modules/
│   │   ├── config/ConfigModule.java
│   │   ├── compatibility/
│   │   │   ├── CompatibilityModule.java
│   │   │   ├── TConstructCompat.java
│   │   │   ├── BotaniaCompat.java
│   │   │   ├── ThaumcraftCompat.java
│   │   │   ├── BloodMagicCompat.java
│   │   │   ├── DraconicEvolutionCompat.java
│   │   │   └── …other stubs…
│   │   ├── combat/CombatModule.java
│   │   ├── movement/MovementModule.java
│   │   ├── network/NetworkModule.java
│   │   ├── progression/
│   │   │   ├── IPlayerProgression.java
│   │   │   ├── PlayerProgression.java
│   │   │   ├── PlayerProgressionStorage.java
│   │   │   ├── PlayerProgressionProvider.java
│   │   │   ├── ProgressionSyncMessage.java
│   │   │   └── ProgressionUpgradeMessage.java
│   │   ├── ui/UIModule.java
│   │   └── utils/UtilsModule.java
└── resources/
├── META-INF/accesstransformer.cfg
├── assets/armoredcombatenhancer/
│   ├── lang/
│   ├── sounds/
│   └── textures/
└── mcmod.info



Credits
- Developer: IAmFmGod
- Thanks to: Forge team, Minecraft modding community, contributors of integrated mods.
  Enjoy a deeper, more strategic combat experience with Armored Combat Enhancer!
