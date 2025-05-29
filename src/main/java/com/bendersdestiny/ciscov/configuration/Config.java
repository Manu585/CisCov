package com.bendersdestiny.ciscov.configuration;

import com.bendersdestiny.ciscov.CisCov;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Config {
    private final File file;
    private final YamlConfiguration config;

    public Config(CisCov plugin, String fileName) {
        File data = plugin.getDataFolder();
        if (!data.exists() && !data.mkdirs()) {
            plugin.getLogger().warning("Could not create data folder!");
        }

        // Point at the file
        this.file = new File(data, fileName);

        // If missing, touch it
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    plugin.getLogger().info("Created new file: " + fileName);
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create file: " + fileName, e);
            }
        }

        // Load
        this.config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load " + fileName, ex);
        }
    }

    /** Expose the config so you can add defaults. */
    public FileConfiguration get() {
        return config;
    }

    public void reload() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException ignored) {}
    }

    /** Copy any defaults you've added, then write back out. */
    public void save() {
        config.options().copyDefaults(true);
        try {
            config.save(file);
        } catch (IOException ignored) {}
    }
}
