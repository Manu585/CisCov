package com.bendersdestiny.ciscov.bending.air.multiabilities.spiritual.spiritualprojection;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.bending.air.abilities.spiritual.SpiritualProjection;
import com.bendersdestiny.ciscov.configuration.ConfigManager;
import com.bendersdestiny.ciscov.util.nms.FakePlayer;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpiritReturn extends SpiritualAbility implements AddonAbility {
    private static final double INITIAL_SPEED   = ConfigManager.getDefaultConfig().getDouble("Air.Spiritual.SpiritualProjection.SubAbilities.SpiritReturn.BaseReturnSpeed");
    private static final double SPEED_INCREMENT = ConfigManager.getDefaultConfig().getDouble("Air.Spiritual.SpiritualProjection.SubAbilities.SpiritReturn.SpeedIncrement");
    private static final double THRESHOLD       = ConfigManager.getDefaultConfig().getDouble("Air.Spiritual.SpiritualProjection.SubAbilities.SpiritReturn.PhysicalBodyDistanceThreshold");

    private static final int SPEED_INCREMENT_INTERVAL = 5; // Increase speed every 5 ticks

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
        return this.player != null ? this.player.getLocation() : null;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return CisCov.getInstance().getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public String getVersion() {
        return CisCov.getInstance().getPluginMeta().getVersion();
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }
}
