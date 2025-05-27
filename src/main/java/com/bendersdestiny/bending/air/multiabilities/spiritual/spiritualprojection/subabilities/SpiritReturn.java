package com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.subabilities;

import com.bendersdestiny.CisCov;
import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.SpiritualProjection;
import com.bendersdestiny.util.nms.FakePlayer;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpiritReturn extends SpiritualAbility implements AddonAbility {
    private static final double INITIAL_SPEED = 0.1;
    private static final double SPEED_INCREMENT = 0.05;
    private static final int    SPEED_INCREMENT_INTERVAL = 5; // Increase speed every 5 ticks
    private static final double THRESHOLD = 1.0;

    private final SpiritualProjection projection;
    private Location origin;

    private double speed = INITIAL_SPEED;
    private int tickCounter = 0;

    public SpiritReturn(Player player) {
        super(player);

        projection = CoreAbility.getAbility(player, SpiritualProjection.class);
        if (projection == null || projection.getFakePlayer() == null) {
            return;
        }

        FakePlayer fakePlayer = projection.getFakePlayer();
        this.origin = fakePlayer.getLocation().clone().add(0, 1, 0);
        this.player.setGameMode(GameMode.SPECTATOR);

        start();
    }

    @Override
    public void progress() {
        if (!player.isOnline() || player.isDead()) {
            remove();
            return;
        }

        Location current = player.getLocation();
        Vector toOrigin = origin.toVector().subtract(current.toVector()).normalize();
        player.setVelocity(toOrigin.multiply(speed));

        tickCounter++;
        if (tickCounter % SPEED_INCREMENT_INTERVAL == 0) {
            speed += SPEED_INCREMENT;
        }

        if (current.distance(origin) <= THRESHOLD) {
            remove();
        }
    }


    @Override
    public void remove() {
        super.remove();
        projection.remove();
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "SpiritReturn";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return CisCov.getInstance().getDescription().getAuthors().getFirst();
    }

    @Override
    public String getVersion() {
        return CisCov.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }
}
