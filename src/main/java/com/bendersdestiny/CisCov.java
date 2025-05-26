package com.bendersdestiny;

import com.bendersdestiny.bending.air.AirBendingListener;
import com.bendersdestiny.bending.fire.FireBendingListener;
import com.bendersdestiny.configuration.ConfigManager;
import com.bendersdestiny.listener.hitbox.FakePlayerDamageListener;
import com.github.retrooper.packetevents.PacketEvents;
import com.projectkorra.projectkorra.ability.CoreAbility;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CisCov extends JavaPlugin {
    private static CisCov instance;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;

        PacketEvents.getAPI().init();

        new ConfigManager();

        CoreAbility.registerPluginAbilities(instance, "com.bendersdestiny.bending");

        registerListeners(
                new AirBendingListener(),
                new FireBendingListener(),
                new FakePlayerDamageListener()
        );
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
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
