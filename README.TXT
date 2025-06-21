Armored Combat Enhancer
Armored Combat Enhancer is a Minecraft 1.12.2 mod designed to enrich combat mechanics 
by introducing dynamic armor weight, critical hit effects, stagger mechanics, and custom 
debuffs. This mod enables a more strategic approach to armored combat, where every
piece of gear and weapon type has clear in-game consequences.


Features
- Armor Weight System:
Each armor piece is assigned a weight based on its material. This weight influences:
- Knockback reduction (when hit)
- Movement speed adjustments via slot-based penalties
- Debuffs when wearing too-heavy armor
- Critical Hit Effects:
On a critical hit, the mod applies:
- A stagger effect to mobs and players
- Weapon-specific debuffs like bleeding, knockback boost, armor reduction, or stun
- Dynamic visual particle effects and sound cues tailored for each weapon type
- Additional Heavy Armor Mechanics:
When the total armor weight exceeds a given threshold:
- Minor slowness or other debuffs are applied
- Extended effects such as reduced regeneration, reduced attack speed, and stagger recovery periods are triggered
- Dynamic Extensibility:
New weapons and armor types can be registered dynamically via a JSON configuration (armored_combat_config.json), allowing for easy mod compatibility and further balance adjustments.
- Effect Duration & Stacking:
A utility system ensures that if an effect is already active, its duration is refreshed or its intensity is increased (up to a configurable cap)



Armored-Combat-Enhancer/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── iamfmgod/
│   │   │           └── armoredcombatenhancer/
│   │   │               ├── ArmoredCombatEnhancer.java   // Main mod file
│   │   │               └── modules/                     // Combat, movement, config modules
│   │   └── resources/
│   │       ├── assets/
│   │       │   └── armoredcombatenhancer/             // Textures, sounds, etc.
│   │       └── mcmod.info                           // Mod metadata file
├── build.gradle                                   // Gradle build script
├── armored_combat_config.json                     // Dynamic mod configuration
└── README.md                                      // This file
