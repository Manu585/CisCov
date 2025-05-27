package com.bendersdestiny.util.nms;

import com.bendersdestiny.CisCov;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerInitializeWorldBorder;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWorldBorderSize;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWorldBorderWarningReach;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: Trash ahh class, doesn't work like I want it to cause' Idk what packets to send - dumm dumm
public class VignetteManager {
    private static final Map<UUID, VignetteManager> ACTIVE_VIGNETTES = new HashMap<>();
    private static final double FULL_DIAMETER = 60_000_000.0;

    private final Player player;
    private final WorldBorder bukkitWorldBorder;
    private final UUID uuid;

    public VignetteManager(Player player) {
        this.player = player;
        this.bukkitWorldBorder = player.getWorld().getWorldBorder();
        this.uuid = player.getUniqueId();
    }

    /**
     * Shows a vignette effect to the given player for a short duration.
     * Call this when the FakePlayer is damaged.
     *
     * @param durationTicks How long the vignette should last (in ticks, 20 ticks = 1 second)
     */
    public void showTemporaryVignette(int durationTicks) {
        if (ACTIVE_VIGNETTES.containsKey(player.getUniqueId())) {
            resetBorder();
        }

        ACTIVE_VIGNETTES.put(player.getUniqueId(), this);

        createAndShowBorder();

        new BukkitRunnable() {
            @Override
            public void run() {
                resetBorder();
            }
        }.runTaskLater(CisCov.getInstance(), durationTicks);
    }

    private void createAndShowBorder() {
        WrapperPlayServerInitializeWorldBorder worldBorderPacket = new WrapperPlayServerInitializeWorldBorder(
                player.getLocation().getX(), player.getLocation().getZ(), 0, 100, 0L, 0, 100, 0
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, worldBorderPacket);
    }

    public void resetBorder() {
        WrapperPlayServerInitializeWorldBorder worldBorderPacket = new WrapperPlayServerInitializeWorldBorder(
                0, 0, 0, FULL_DIAMETER, 0L, 0, 0, 0
        );

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, worldBorderPacket);

        WrapperPlayServerWorldBorderSize resizeOriginalPacket;

        if (bukkitWorldBorder != null) {
            resizeOriginalPacket = new WrapperPlayServerWorldBorderSize(bukkitWorldBorder.getSize());
        } else {
            resizeOriginalPacket = new WrapperPlayServerWorldBorderSize(FULL_DIAMETER);
        }

        WrapperPlayServerWorldBorderWarningReach warningReachPacket;

        if (bukkitWorldBorder != null) {
            warningReachPacket = new WrapperPlayServerWorldBorderWarningReach(bukkitWorldBorder.getWarningDistance());
        } else {
            warningReachPacket = new WrapperPlayServerWorldBorderWarningReach(0);
        }

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, resizeOriginalPacket);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, warningReachPacket);
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static double getFullDiameter() {
        return FULL_DIAMETER;
    }

    public static Map<UUID, VignetteManager> getActiveVignettes() {
        return ACTIVE_VIGNETTES;
    }
}
