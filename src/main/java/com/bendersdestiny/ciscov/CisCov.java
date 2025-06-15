package com.bendersdestiny.ciscov;

import com.bendersdestiny.ciscov.bending.air.AirBendingListener;
import com.bendersdestiny.ciscov.bending.air.AirBendingNMSListener;
import com.bendersdestiny.ciscov.bending.air.multiabilities.spiritual.spiritualprojection.listeners.FakePlayerDamageListener;
import com.bendersdestiny.ciscov.bending.avatar.AvatarBendingListener;
import com.bendersdestiny.ciscov.bending.earth.EarthBendingListener;
import com.bendersdestiny.ciscov.bending.fire.FireBendingListener;
import com.bendersdestiny.ciscov.bending.water.WaterBendingListener;
import com.bendersdestiny.ciscov.configuration.ConfigManager;
import com.bendersdestiny.ciscov.listener.BendingCommandAndReloadListener;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
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

        PacketEvents.getAPI().getEventManager().registerListener(new AirBendingNMSListener(), PacketListenerPriority.NORMAL);
    }

    @Override
    public void onEnable() {
        instance = this;

        PacketEvents.getAPI().init();

        new ConfigManager();

        CoreAbility.registerPluginAbilities(instance, "com.bendersdestiny.ciscov.bending");

        registerListeners(
                new AirBendingListener(),
                new EarthBendingListener(),
                new FireBendingListener(),
                new WaterBendingListener(),
                new AvatarBendingListener(),
                new FakePlayerDamageListener(),
                new BendingCommandAndReloadListener()
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
