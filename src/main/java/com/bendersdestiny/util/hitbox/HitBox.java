package com.bendersdestiny.util.hitbox;

import com.bendersdestiny.CisCov;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HitBox {
    private static final Map<Integer, HitBox> HITBOXES_BY_ENTITY_ID = new ConcurrentHashMap<>();

    private int entityId;
    private Location armorStandLocation;
    private ArmorStand armorStand;

    public void createHitbox(Location location) {
        if (location.getWorld() == null) throw new NullPointerException("World cannot be null! Location: " + location);

        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.armorStandLocation = location.clone();
        this.entityId = this.armorStand.getEntityId();

        this.armorStand.setHealth(20);
        this.armorStand.setCollidable(true);
        this.armorStand.setRemoveWhenFarAway(false);
        this.armorStand.setPersistent(false);
        this.armorStand.setCanPickupItems(false);
        this.armorStand.setInvulnerable(true);
        this.armorStand.setVisible(false);
        this.armorStand.setGravity(false);
        this.armorStand.setMarker(false);
        this.armorStand.setBasePlate(false);
        this.armorStand.setArms(false);
        this.armorStand.setSmall(false);
        this.armorStand.setMetadata("FakePlayerHitbox", new FixedMetadataValue(CisCov.getInstance(), true));

        registerHitBox(this.entityId, this);
    }

    public void removeHitbox() {
        this.armorStand.remove();

        unregisterHitBox(this.entityId);
    }

    public static HitBox getHitBox(int id) {
        return HITBOXES_BY_ENTITY_ID.get(id);
    }

    public static void registerHitBox(int id, HitBox hitBox) {
        HITBOXES_BY_ENTITY_ID.put(id, hitBox);
    }

    public static void unregisterHitBox(int id) {
        HITBOXES_BY_ENTITY_ID.remove(id);
    }

    public static Map<Integer, HitBox> getHITBOXES() {
        return HITBOXES_BY_ENTITY_ID;
    }

    public int getEntityId() {
        return entityId;
    }

    public Location getArmorStandLocation() {
        return armorStandLocation;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }
}
