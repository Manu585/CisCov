package com.bendersdestiny.ciscov.configuration;

import com.bendersdestiny.ciscov.CisCov;
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

        // -- SpiritualProjection --
        config.addDefault("Air.Spiritual.SpiritualProjection.Cooldown", 5000);
        config.addDefault("Air.Spiritual.SpiritualProjection.ChargeTime", 3000);
        config.addDefault("Air.Spiritual.SpiritualProjection.Duration", 60000);

        config.addDefault("Air.Spiritual.SpiritualProjection.SubAbilities.SpiritReturn.BaseReturnSpeed", 0.1);
        config.addDefault("Air.Spiritual.SpiritualProjection.SubAbilities.SpiritReturn.SpeedIncrement", 0.05);
        config.addDefault("Air.Spiritual.SpiritualProjection.SubAbilities.SpiritReturn.PhysicalBodyDistanceThreshold", 1);

        // FIRE

        // -- SteamSurge --
        config.addDefault("Fire.SteamSurge.Cooldown", 5000);
        config.addDefault("Fire.SteamSurge.ChargeTime", 3000);
        config.addDefault("Fire.SteamSurge.Duration", 60000);
        config.addDefault("Fire.SteamSurge.SteamHeight", 6);
        config.addDefault("Fire.SteamSurge.SteamWidth", 6);
        config.addDefault("Fire.SteamSurge.NearestWaterSourceRadius", 6);

        // EARTH

        // -- LavaGeyser --
        config.addDefault("Earth.Lava.LavaGeyser.Cooldown", 5000);
        config.addDefault("Earth.Lava.LavaGeyser.ChargeTime", 3000);
        config.addDefault("Earth.Lava.LavaGeyser.Duration", 5000);
        config.addDefault("Earth.Lava.LavaGeyser.Radius", 4);
        config.addDefault("Earth.Lava.LavaGeyser.Height", 4);
        config.addDefault("Earth.Lava.LavaGeyser.Range", 8);
        config.addDefault("Earth.Lava.LavaGeyser.MaxSources", 5);

        defaultConfig.save();
    }

    public static FileConfiguration getDefaultConfig() {
        return defaultConfig.get();
    }

    public static Config getDefaultConfigInstance() {
        return defaultConfig;
    }
}
