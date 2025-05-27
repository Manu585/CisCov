package com.bendersdestiny.bending.air;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntitySoundEffect;

public class AirBendingNMSListener implements PacketListener {
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();

        if (event.getPacketType() != PacketType.Play.Server.ENTITY_SOUND_EFFECT) return;

        WrapperPlayServerEntitySoundEffect stepSound = new WrapperPlayServerEntitySoundEffect(0, SoundCategory.AMBIENT, user.getEntityId(), 2, 2);

        event.setCancelled(true);
    }
}
