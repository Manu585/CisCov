package com.bendersdestiny.util.nms;

import com.bendersdestiny.CisCov;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBar {
    public static void sendActionBar(Player player, String message) {
        Component component = Component.text(message);
        WrapperPlayServerActionBar bar = new WrapperPlayServerActionBar(component);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, bar);
    }

    public static void sendActionBar(Player player, String message, int durationTicks) {
        sendActionBar(player, message);
        new BukkitRunnable() {
            @Override public void run() {
                sendActionBar(player, "");
            }
        }.runTaskLater(CisCov.getInstance(), durationTicks);
    }
}
