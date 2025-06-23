package com.iamfmgod.armoredcombatenhancer.modules.config;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigModule {

    private static final String CONFIG_FILE_PATH = "config/armored_combat_config.json";
    private static final String DEFAULT_RESOURCE_PATH = "config/armored_combat_config.json";

    public static Map<String, JsonObject> weaponConfigs = new HashMap<>();
    public static Map<String, JsonObject> armorConfigs = new HashMap<>();

    public static void loadConfig() {
        weaponConfigs.clear();
        armorConfigs.clear();

        File configFile = new File(CONFIG_FILE_PATH);

        try {
            // If the file doesn’t exist, copy the default from JAR resources
            if (!configFile.exists()) {
                InputStream defaultStream = ConfigModule.class.getClassLoader().getResourceAsStream(DEFAULT_RESOURCE_PATH);
                if (defaultStream != null) {
                    Files.copy(defaultStream, configFile.toPath());
                    System.out.println("Default config copied to " + configFile.getPath());
                } else {
                    System.err.println("Default config not found in resources.");
                    return;
                }
            }

            // Parse the now-existing config file
            try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                JsonObject configJson = new Gson().fromJson(reader, JsonObject.class);

                if (configJson == null) {
                    System.err.println("Config file is empty or invalid.");
                    return;
                }

                if (configJson.has("weapons")) {
                    JsonArray weapons = configJson.getAsJsonArray("weapons");
                    for (JsonElement el : weapons) {
                        JsonObject weaponObj = el.getAsJsonObject();
                        if (weaponObj.has("type")) {
                            String type = weaponObj.get("type").getAsString().toLowerCase();
                            weaponConfigs.put(type, weaponObj);
                        }
                    }
                }

                if (configJson.has("armors")) {
                    JsonArray armors = configJson.getAsJsonArray("armors");
                    for (JsonElement el : armors) {
                        JsonObject armorObj = el.getAsJsonObject();
                        if (armorObj.has("type")) {
                            String type = armorObj.get("type").getAsString().toLowerCase();
                            armorConfigs.put(type, armorObj);
                        }
                    }
                }

                System.out.println("Config loaded. Weapons: " + weaponConfigs.size() + ", Armors: " + armorConfigs.size());
            }

        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }

    public static JsonObject getWeaponConfig(String type) {
        return weaponConfigs.get(type.toLowerCase());
    }

    public static JsonObject getArmorConfig(String type) {
        return armorConfigs.get(type.toLowerCase());
    }
}