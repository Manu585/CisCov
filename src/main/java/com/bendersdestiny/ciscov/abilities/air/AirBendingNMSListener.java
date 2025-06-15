package com.bendersdestiny.ciscov.abilities.air;

import com.bendersdestiny.ciscov.abilities.air.passives.TwinkleToes;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntitySoundEffect;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// TODO: Fix class
public class AirBendingNMSListener implements PacketListener {
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_SOUND_EFFECT) return;

        Player player = Bukkit.getPlayer(event.getUser().getUUID());
        if (player == null || !player.isOnline()) return;

        player.sendMessage("Packet: " + event.getPacketType());

        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return;
        if (!bPlayer.canBend(CoreAbility.getAbility(TwinkleToes.class))) return;

        WrapperPlayServerEntitySoundEffect soundPacket = new WrapperPlayServerEntitySoundEffect(event);

        String sound = soundPacket.getSound().getSoundId().toString();

        if (sound.startsWith("minecraft:block.") && sound.endsWith(".step")) {
            bPlayer.getPlayer().sendMessage("Muted sound");
            event.setCancelled(true);
        }
    }
}
