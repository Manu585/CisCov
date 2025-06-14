package com.bendersdestiny.ciscov.util.nms;

import com.bendersdestiny.ciscov.bending.air.abilities.spiritual.SpiritualProjection;
import com.bendersdestiny.ciscov.util.hitbox.HitBox;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.MojangAPIUtil;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.projectkorra.projectkorra.ability.CoreAbility;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakePlayer {
    private static final Map<Integer, FakePlayer> FAKE_PLAYER_BY_ENTITY_ID = new ConcurrentHashMap<>();
    private static final Map<Integer, FakePlayer> FAKE_PLAYER_BY_HITBOX_ID = new ConcurrentHashMap<>();
    private static final Map   <UUID, FakePlayer> FAKE_PLAYER_BY_UUID      = new ConcurrentHashMap<>();

    private final int entityId;
    private final Player player;
    private final UUID uuid;
    private final FakeArmorStand fakeArmorStand;
    private final HitBox hitBox;

    private Location location;
    private double health;

    public FakePlayer(Player player) {
        this.entityId = SpigotReflectionUtil.generateEntityId();
        this.player = player;
        this.uuid = UUID.randomUUID();
        this.fakeArmorStand = new FakeArmorStand();
        this.hitBox = new HitBox();

        this.health = player.getHealth();
    }

    public void spawnFakePlayer(Location location) {
        this.location = location;

        // If FakePlayer already exists with owner UUID, skip
        if (FAKE_PLAYER_BY_UUID.containsKey(this.player.getUniqueId())) {
            return;
        }

        // Create Hitbox for FakePlayer
        this.hitBox.createHitbox(location);

        // PUT ALL INSTANCES IN CORRESPONDING MAPS
        FAKE_PLAYER_BY_UUID     .put(this.player.getUniqueId(), this);
        FAKE_PLAYER_BY_ENTITY_ID.put(this.entityId, this);
        FAKE_PLAYER_BY_HITBOX_ID.put(this.hitBox.getEntityId(), this);

        // MAKE FAKE PLAYER VISIBLE TO SERVER
        List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfoList = new ArrayList<>();
        playerInfoList.add(new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(new UserProfile(this.uuid, this.player.getName(), MojangAPIUtil.requestPlayerTextureProperties(this.player.getUniqueId()))));
        WrapperPlayServerPlayerInfoUpdate infoUpdatePacket = new WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, playerInfoList);

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, infoUpdatePacket);
        }

        // SPAWN FAKE PLAYER WITH PACKETS
        WrapperPlayServerSpawnEntity spawnEntityPacket = new WrapperPlayServerSpawnEntity(
                this.entityId,
                this.uuid,
                EntityTypes.PLAYER,
                SpigotConversionUtil.fromBukkitLocation(location),
                location.getYaw(),
                0,
                new Vector3d(0, 0, 0));

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, spawnEntityPacket);
        }

        // MAKE FAKE PLAYER LOOK SITTING
        this.fakeArmorStand.spawnFakeArmorStand(location);
        WrapperPlayServerSetPassengers setPassengersPacket = new WrapperPlayServerSetPassengers(this.fakeArmorStand.getEntityId(), new int[] { this.entityId });

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, setPassengersPacket);
        }

        // MAKE FAKE PLAYERS SKIN PARTS VISIBLE
        List<EntityData<?>> skinMeta = new ArrayList<>();
        skinMeta.add(new EntityData<>(17, EntityDataTypes.BYTE, (byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40)));
        WrapperPlayServerEntityMetadata metaPacket = new WrapperPlayServerEntityMetadata(this.entityId, skinMeta);

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, metaPacket);
        }
    }

    public void removePlayer() {
        // REMOVE ALL INSTANCES FROM CORRESPONDING MAPS
        FAKE_PLAYER_BY_ENTITY_ID.remove(this.entityId);
        FAKE_PLAYER_BY_UUID     .remove(this.player.getUniqueId());
        FAKE_PLAYER_BY_HITBOX_ID.remove(this.hitBox.getEntityId());

        // DESTROY FAKE PLAYER AND HITBOX, AND ARMOR STAND WITH PACKETS
        WrapperPlayServerDestroyEntities destroyFakePlayer = new WrapperPlayServerDestroyEntities(this.entityId);
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, destroyFakePlayer);
        }

        fakeArmorStand.removeFakeArmorStand();
        hitBox.removeHitbox();
    }

    /**
     * Damage FakePlayer and if damage "kills" it, remove spiritual projection and kill real player
     *
     * @param amount Damage amount
     */
    public void damage(double amount) {
        this.health = Math.max(0, this.health - amount);

        if (this.health <= amount) {
            this.health = 0;

            SpiritualProjection projection = CoreAbility.getAbility(player, SpiritualProjection.class);
            if (projection != null) {
                projection.remove();
                player.setHealth(0);
                return;
            }

            removePlayer();
        }
    }

    public static FakePlayer getByEntityId(int id) {
        return FAKE_PLAYER_BY_ENTITY_ID.get(id);
    }

    public static FakePlayer getByHitboxId(int id) {
        return FAKE_PLAYER_BY_HITBOX_ID.get(id);
    }

    public static FakePlayer getByUUID(UUID uuid) {
        return FAKE_PLAYER_BY_UUID.get(uuid);
    }

    public static boolean hasFake(Player owner) {
        return FAKE_PLAYER_BY_UUID.containsKey(owner.getUniqueId());
    }

    public int getEntityId() {
        return entityId;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public FakeArmorStand getFakeArmorStand() {
        return fakeArmorStand;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public Location getLocation() {
        return location;
    }

    public double getHealth() {
        return health;
    }

    private static class FakeArmorStand {
        private static final Map<Integer, FakeArmorStand> FAKE_ARMOR_STANDS_BY_ENTITY_ID = new ConcurrentHashMap<>();

        private final int entityId;
        private final UUID uuid;

        public FakeArmorStand() {
            this.entityId = SpigotReflectionUtil.generateEntityId();
            this.uuid = UUID.randomUUID();
        }

        public void spawnFakeArmorStand(Location location) {
            FAKE_ARMOR_STANDS_BY_ENTITY_ID.put(this.entityId, this);

            // SPAWN ARMOR STAND WITH PACKETS

            WrapperPlayServerSpawnEntity fakeArmorStand = new WrapperPlayServerSpawnEntity(
                    this.entityId,
                    this.uuid,
                    EntityTypes.ARMOR_STAND,
                    SpigotConversionUtil.fromBukkitLocation(location),
                    location.getYaw(),
                    0,
                    new Vector3d(0, 0, 0));

            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, fakeArmorStand);
            }

            // MANIPULATE ARMOR STAND METADATA WITH PACKETS

            List<EntityData<?>> entityDataList = new ArrayList<>();

            EntityData<?> small = new EntityData<>(15, EntityDataTypes.BYTE, (byte) 1); // small
            EntityData<?> invisible = new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x20); // invisible

            entityDataList.add(small);
            entityDataList.add(invisible);

            WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(this.entityId, entityDataList);

            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, metadataPacket);
            }
        }

        public void removeFakeArmorStand() {
            // REMOVE ARMOR STAND AND REMOVE FROM MAPS
            FAKE_ARMOR_STANDS_BY_ENTITY_ID.remove(this.entityId);
            WrapperPlayServerDestroyEntities destroyFakeArmorStand = new WrapperPlayServerDestroyEntities(entityId);

            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(onlinePlayers, destroyFakeArmorStand);
            }
        }

        public static FakeArmorStand getByEntityId(int id) {
            return FAKE_ARMOR_STANDS_BY_ENTITY_ID.get(id);
        }

        public int getEntityId() {
            return entityId;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}
