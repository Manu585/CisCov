package com.bendersdestiny.ciscov.util.nms;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Helps to Apply a glowing effect via packets to a given Player with any Team color
 */
public class GlowManager {
    public static void setGlowing(Player player, NamedTextColor color) {
        String teamName = "glow_" + player.getUniqueId().toString().substring(0, 8);

        WrapperPlayServerTeams.ScoreBoardTeamInfo info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.empty(),
                null,
                null,
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.NEVER,
                color,
                WrapperPlayServerTeams.OptionData.NONE);

        WrapperPlayServerTeams create = new WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.CREATE,
                info,
                Collections.singletonList(player.getName())
        );

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, create);
        }

        player.setGlowing(true);
    }

    public static void removeGlowing(Player player) {
        String teamName = "glow_" + player.getUniqueId().toString().substring(0, 8);

        ArrayList<String> players = new ArrayList<>();
        players.add(player.getName());

        WrapperPlayServerTeams removePlayer = new WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.REMOVE,
                (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
                players
        );

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, removePlayer);
        }

        player.setGlowing(false);
    }
}
