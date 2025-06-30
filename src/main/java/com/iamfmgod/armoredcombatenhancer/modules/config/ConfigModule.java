package com.iamfmgod.armoredcombatenhancer.modules.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigModule {
    private static final Logger LOGGER          = LogManager.getLogger("ArmoredCombatEnhancer");
    private static final String CONFIG_PATH     = "config/armored_combat_config.json";
    private static final String DEFAULT_RESOURCE= CONFIG_PATH;

    public static Map<String, JsonObject> weaponConfigs        = new HashMap<>();
    public static Map<String, JsonObject> armorConfigs         = new HashMap<>();
    public static Map<String, JsonObject> compatibilityConfigs = new HashMap<>();
    public static Map<String, Float>      armorWeightOverrides = new HashMap<>();

    // Movement settings (can be overridden by JSON)
    public static float   shieldBashDamage    = 4.0F;
    public static float   shieldBashKnockback = 5.0F;
    public static double  shieldBashRange     = 4.0D;
    public static double  shieldDashSpeed     = 1.0D;
    public static int     shieldBashCooldown  = 40;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Call at startup (preInit) and on reload key.
     */
    public static void reload() {
        LOGGER.info("Reloading A.C.E configuration...");
        loadConfig();
    }

    private static void loadConfig() {
        weaponConfigs.clear();
        armorConfigs.clear();
        compatibilityConfigs.clear();
        armorWeightOverrides.clear();

        File file = new File(CONFIG_PATH);
        try {
            // Copy default if missing
            if (!file.exists()) {
                InputStream in = ConfigModule.class
                        .getClassLoader()
                        .getResourceAsStream(DEFAULT_RESOURCE);
                if (in == null) {
                    LOGGER.error("Default config not found in JAR!");
                } else {
                    Files.copy(in, file.toPath());
                    LOGGER.info("Default config created at {}", file.getPath());
                }
            }

            // Read and parse
            try (Reader reader = new InputStreamReader(
                    Files.newInputStream(file.toPath()),
                    StandardCharsets.UTF_8)) {

                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                if (root == null || !root.isJsonObject()) {
                    LOGGER.error("Config root is not a JSON object.");
                    return;
                }

                // weapons
                if (root.has("weapons")) {
                    for (JsonElement el : root.getAsJsonArray("weapons")) {
                        JsonObject obj = el.getAsJsonObject();
                        if (obj.has("type")) {
                            weaponConfigs.put(
                                    obj.get("type").getAsString().toLowerCase(),
                                    obj
                            );
                        }
                    }
                }

                // armors
                if (root.has("armors")) {
                    for (JsonElement el : root.getAsJsonArray("armors")) {
                        JsonObject obj = el.getAsJsonObject();
                        if (obj.has("type")) {
                            armorConfigs.put(
                                    obj.get("type").getAsString().toLowerCase(),
                                    obj
                            );
                        }
                    }
                }

                // compatibility
                if (root.has("compatibility")) {
                    JsonObject compat = root.getAsJsonObject("compatibility");
                    for (Map.Entry<String, JsonElement> entry : compat.entrySet()) {
                        compatibilityConfigs.put(
                                entry.getKey().toLowerCase(),
                                entry.getValue().getAsJsonObject()
                        );
                    }
                }

                // armorWeights overrides
                if (root.has("armorWeights")) {
                    JsonObject aw = root.getAsJsonObject("armorWeights");
                    for (Map.Entry<String, JsonElement> e : aw.entrySet()) {
                        armorWeightOverrides.put(
                                e.getKey().toLowerCase(),
                                e.getValue().getAsFloat()
                        );
                    }
                    LOGGER.info("Loaded {} armor-weight overrides", armorWeightOverrides.size());
                }

                // movement block
                if (root.has("movement")) {
                    JsonObject m = root.getAsJsonObject("movement");
                    shieldBashDamage    = get(m, "shieldBashDamage",    shieldBashDamage);
                    shieldBashKnockback = get(m, "shieldBashKnockback", shieldBashKnockback);
                    shieldBashRange     = get(m, "shieldBashRange",     shieldBashRange);
                    shieldDashSpeed     = get(m, "shieldDashSpeed",     shieldDashSpeed);
                    shieldBashCooldown  = get(m, "shieldBashCooldown",  shieldBashCooldown);
                }

                LOGGER.info("Config loaded: Weapons={}, Armors={}, Compat={}, Overrides={}",
                        weaponConfigs.size(),
                        armorConfigs.size(),
                        compatibilityConfigs.size(),
                        armorWeightOverrides.size()
                );
            }

        } catch (Exception e) {
            LOGGER.error("Exception loading config: ", e);
        }
    }

    // Helpers for retrieving typed settings with defaults
    private static float  get(JsonObject o, String key, float  def)  { return o.has(key)? o.get(key).getAsFloat()  : def; }
    private static double get(JsonObject o, String key, double def)  { return o.has(key)? o.get(key).getAsDouble() : def; }
    private static int    get(JsonObject o, String key, int    def)  { return o.has(key)? o.get(key).getAsInt()    : def; }

    /**
     * Returns an exact or substring‐matched weapon config, or null.
     */
    public static JsonObject getWeaponConfig(String type) {
        String key = type.toLowerCase();
        if (weaponConfigs.containsKey(key)) return weaponConfigs.get(key);
        for (Map.Entry<String, JsonObject> e : weaponConfigs.entrySet()) {
            if (key.contains(e.getKey())) {
                LOGGER.debug("Weapon fallback: {} → {}", type, e.getKey());
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * Returns an exact or substring‐matched armor config, or null.
     */
    public static JsonObject getArmorConfig(String type) {
        String key = type.toLowerCase();
        if (armorConfigs.containsKey(key)) return armorConfigs.get(key);
        for (Map.Entry<String, JsonObject> e : armorConfigs.entrySet()) {
            if (key.contains(e.getKey())) {
                LOGGER.debug("Armor fallback: {} → {}", type, e.getKey());
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * Returns the mod‐compatibility config for the given modId, or null.
     */
    public static JsonObject getCompatConfig(String modId) {
        return compatibilityConfigs.get(modId.toLowerCase());
    }

    /**
     * Returns the configured override for this armor material if present,
     * otherwise returns the provided fallback weight.
     *
     * @param materialRegistryName e.g. "minecraft:diamond"
     * @param fallbackWeight       the default weight to use if no override exists
     */
    public static float getArmorWeight(String materialRegistryName, float fallbackWeight) {
        return armorWeightOverrides.getOrDefault(
                materialRegistryName.toLowerCase(),
                fallbackWeight
        );
    }
}