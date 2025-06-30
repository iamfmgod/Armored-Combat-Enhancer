# Armored Combat Enhancer

Armored Combat Enhancer is a Minecraft 1.12.2 mod designed to enrich combat mechanics by introducing dynamic armor weight, critical hit effects, stagger mechanics, and custom debuffs. It encourages a more strategic approach to armored combat—every piece of gear and weapon type carries clear in-game consequences.

## Features

### Armor Weight System
- **Dynamic Armor Weight:** Each armor piece is assigned a weight based on its material.
- **Impact on Combat:**
  - Reduces knockback when hit.
  - Adjusts movement speed using slot-based penalties.
  - Applies debuffs when overall armor weight exceeds configured thresholds.

### Critical Hit Effects
- **Stagger Mechanics:** Apply a stagger effect to mobs and players on critical hits.
- **Weapon-Specific Debuffs:** Assign debuffs such as bleeding, knockback boost, armor reduction, or stun.
- **Dynamic Visual & Audio Feedback:** Custom particle effects and sound cues enhance the feeling of impact.

### Heavy Armor Mechanics
- **Weight Thresholds:** When total armor weight exceeds a given value:
  - Minor slowness or additional debuffs are applied.
  - Extended effects such as reduced regeneration, decreased attack speed, and prolonged stagger recovery are triggered.

### Dynamic Extensibility
- **JSON Configuration:** New weapons and armor types can be registered dynamically via the `armored_combat_config.json` configuration file.
- **Mod Compatibility:** The mod integrates seamlessly with popular mods (Draconic Evolution, Botania, ProjectE, Twilight Forest) to ensure balance and compatibility.

### Effect Duration & Stacking
- **Smart Effect Handling:** Active effects have their durations refreshed or intensities increased—up to a configurable cap—if an identical effect is reapplied.

## Project Structure

```
Armored-Combat-Enhancer/
├── build.gradle
├── mcreator.gradle
├── README.md
├── config/
│   └── armored_combat_config.json
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── iamfmgod/
│       │           └── armoredcombatenhancer/
│       │               ├── armoredcombatenhancer.java
│       │               ├── ModSettings.java                  ← New class for toggle state
│       │               ├── client/
│       │               │   ├── gui/
│       │               │   │   └── GuiArmoredCombatEnhancer.java  ← Updated with toggle indicator
│       │               │   └── ModKeyBindings.java
│       │               └── modules/
│       │                   ├── config/
│       │                   │   └── ConfigModule.java
│       │                   ├── compatibility/
│       │                   │   └── CompatibilityModule.java
│       │                   ├── combat/
│       │                   │   └── CombatModule.java
│       │                   ├── movement/
│       │                   │   └── MovementModule.java
│       │                   ├── network/
│       │                   │   └── NetworkModule.java
│       │                   ├── ui/
│       │                   │   └── UIModule.java            
│       │                   └── utils/
│       │                       └── UtilsModule.java
│       └── resources/
│           ├── META-INF/
│           │   └── accesstransformer.cfg
│           ├── assets/
│           │   └── armoredcombatenhancer/
│           │       ├── lang/
│           │       ├── sounds/
│           │       └── textures/
│           └── mcmod.info
```

## Installation & Usage

1. **Configuration:**  
   Modify the `armored_combat_config.json` file to customize weapon and armor types, thresholds, and effect stacking values.

2. **Mod Settings:**  
   Use `ModSettings.java` to toggle mod-specific options (the GUI now displays a toggle indicator for these settings).

3. **Key Bindings:**
  - **Open GUI:** Press **G** to launch the Armored Combat Enhancer GUI.
  - **Reload Config:** Press **R** to reload the configuration.
  - **Dash:** Press the dedicated dash key (default **F**) to perform a lateral dash.  
    – The dash direction is determined by the current A/D key state.  
    – If neither key is pressed, a forward dash is used as a fallback.
  - **Dodge:** Quickly double-tap the left (**A**) or right (**D**) movement keys to perform a swift lateral dodge.

4. **Combat Effects:**  
   During combat, the mod handles dynamic adjustments based on armor weight, critical hit effects, and more, leading to a nuanced and strategic combat experience.

## Credits

Developed by *IAmFmGod*. Special thanks to the Minecraft modding community for inspiration and support.

---

Enjoy a more balanced and immersive combat experience with Armored Combat Enhancer!

---

