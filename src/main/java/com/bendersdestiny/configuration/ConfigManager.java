package com.bendersdestiny.configuration;

import com.bendersdestiny.CisCov;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private static final Config defaultConfig = new Config(CisCov.getInstance(), "config.yml");

    public ConfigManager() {
        createDefaults();
    }

    private void createDefaults() {
        FileConfiguration config = defaultConfig.get();

        // AIR

        // SPIRITUAL
        config.addDefault("Air.Spiritual.SpiritualProjection.ChargeTime", 3000);
        config.addDefault("Air.Spiritual.SpiritualProjection.Duration", 60000);


        defaultConfig.save();
    }

    public static FileConfiguration getDefaultConfig() {
        return defaultConfig.get();
    }
}
