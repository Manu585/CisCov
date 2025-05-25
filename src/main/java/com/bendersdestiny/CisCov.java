package com.bendersdestiny;

import com.bendersdestiny.bending.air.AirBendingListener;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CisCov extends JavaPlugin {
    private static CisCov instance;

    @Override
    public void onEnable() {
        instance = this;

        CoreAbility.registerPluginAbilities(instance, "com.bendersdestiny.bending");

        registerListeners(
                new AirBendingListener()
        );

        getLogger().info("CisCov enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CisCov disabled!");
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    public static CisCov getInstance() {
        return instance;
    }
}
