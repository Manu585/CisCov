package com.bendersdestiny.ciscov.bending.fire.abilities;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.configuration.ConfigManager;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// ToDo: Dogshit ability
public class SteamSurge extends FireAbility implements AddonAbility {
    private enum State {
        CHARGING,
        READY,
        GENERATING,  // Fog buildup
        SUSTAINING,  // full fog holding
        DISSIPATING, // fog disappears in steps
        FINISHED
    }

    @Attribute(Attribute.HEIGHT) private int steamHeight;
    @Attribute(Attribute.WIDTH) private int steamWidth;
    @Attribute(Attribute.CHARGE_DURATION) private long chargeDuration;
    @Attribute(Attribute.DURATION) private long duration;
    @Attribute(Attribute.RADIUS) private int nearestWaterSourceRadius;

    private State state = State.CHARGING;
    private long stateStartTime;
    private long lastStepTime;

    private List<Location> genQueue;
    private List<Location> filledPositions;
    private int particlesPerTick;
    private final Random random = new Random();

    public SteamSurge(org.bukkit.entity.Player player) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        // SET FIELDS
        steamHeight = ConfigManager.getDefaultConfig().getInt("Fire.SteamSurge.SteamHeight", 6);
        steamWidth  = ConfigManager.getDefaultConfig().getInt("Fire.SteamSurge.SteamWidth", 6);

        chargeDuration = (long) ConfigManager.getDefaultConfig().getDouble("Fire.SteamSurge.ChargeTime", 4000);
        duration       = (long) ConfigManager.getDefaultConfig().getDouble("Fire.SteamSurge.Duration", 10000);

        nearestWaterSourceRadius = ConfigManager.getDefaultConfig().getInt("Fire.SteamSurge.NearestWaterSourceRadius", 6);

        stateStartTime = System.currentTimeMillis();
        lastStepTime   = stateStartTime;

        start();
    }

    @Override
    public void progress() {
        if (!bPlayer.canBendIgnoreBinds(this)) {
            remove();
            return;
        }

        long now = System.currentTimeMillis();

        switch (state) {
            case CHARGING:
                handleCharging(now);
                break;

            case READY:
                handleReady();
                break;

            case GENERATING:
                handleGenerating(now);
                break;

            case SUSTAINING:
                handleSustaining(now);
                break;

            case DISSIPATING:
                handleDissipating(now);
                break;

            case FINISHED:
                remove();
                break;
        }
    }

    private void handleCharging(long now) {
        if (player.isSneaking()) {
            if (now - stateStartTime >= chargeDuration) {
                state = State.READY;
                player.sendMessage("Steam Surge Charged!");
            }
        } else {
            stateStartTime = now;
        }
    }

    private void handleReady() {
        if (!player.isSneaking()) {
            if (waterNear()) {
                genQueue = new ArrayList<>();
                Location base = getLocation();

                for (int x = -steamWidth; x <= steamWidth; x++) {
                    for (int y = 0; y <= steamHeight; y++) {
                        for (int z = -steamWidth; z <= steamWidth; z++) {
                            genQueue.add(base.clone().add(x, y + 0.5, z));
                        }
                    }
                }

                Collections.shuffle(genQueue);
                int total = genQueue.size();
                particlesPerTick = Math.max(1, total / steamHeight);

                filledPositions = new ArrayList<>();
                lastStepTime = System.currentTimeMillis();
                state = State.GENERATING;
                player.sendMessage("Steam Surge Started!");
            } else {
                remove();
            }
        }
    }

    private void handleGenerating(long now) {
        final long interval = 400;
        if (now - lastStepTime < interval) return;

        for (Location loc : filledPositions) {
            spawnCloudParticles(loc);
        }

        int count = Math.min(particlesPerTick, genQueue.size());
        for (int i = 0; i < count; i++) {
            Location loc = genQueue.removeFirst();
            spawnCloudParticles(loc);
            filledPositions.add(loc);
        }

        lastStepTime = now;

        // Finished building fog?
        if (genQueue.isEmpty()) {
            stateStartTime = now;
            state = State.SUSTAINING;
            player.sendMessage("Steam fully formed!");
        }
    }

    private void handleSustaining(long now) {
        final long interval = 400;
        if (now - lastStepTime < interval) return;

        // Refresh full fog each tick
        for (Location loc : filledPositions) {
            spawnCloudParticles(loc);
        }

        if (now - stateStartTime >= duration) {
            lastStepTime = now;
            state = State.DISSIPATING;
        }
    }

    private void handleDissipating(long now) {
        final long interval = 400;
        if (now - lastStepTime < interval) return;

        for (int i = 0; i < particlesPerTick && !filledPositions.isEmpty(); i++) {
            int idx = random.nextInt(filledPositions.size());
            Location loc = filledPositions.remove(idx);
            assert loc.getWorld() != null;
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 1, .1, .1, .1, .1);
        }
        lastStepTime = now;

        if (filledPositions.isEmpty()) {
            state = State.FINISHED;
        }
    }

    private boolean waterNear() {
        for (Block b : GeneralMethods.getBlocksAroundPoint(getLocation(), nearestWaterSourceRadius)) {
            if (b.getType() == Material.WATER || b.getType() == Material.WATER_CAULDRON) {
                return true;
            }
        }
        return false;
    }

    private void spawnCloudParticles(Location loc) {
        assert loc.getWorld() != null;
        loc.getWorld().spawnParticle(Particle.CLOUD, loc, 1, .1, .1, .1, .1);
    }

    @Override
    public void remove() {
        super.remove();
        bPlayer.addCooldown(this);
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return (long) ConfigManager.getDefaultConfig().getDouble("Fire.SteamSurge.Cooldown");
    }

    @Override
    public String getName() {
        return "SteamSurge";
    }

    @Override
    public Location getLocation() {
        return player != null ? player.getLocation() : null;
    }

    @Override
    public void load() {}

    @Override
    public void stop() {}

    @Override
    public String getAuthor() {
        return CisCov.getInstance().getDescription().getAuthors().getFirst();
    }

    @Override
    public String getVersion() {
        return CisCov.getInstance().getDescription().getVersion();
    }

    @Override
    public String getInstructions() {
        return "Hold Sneak near water, release sneak after indicator";
    }

    @Override
    public String getDescription() {
        return "* CisCov Addon *\nRelease a wave of steam to hide from other players";
    }
}
