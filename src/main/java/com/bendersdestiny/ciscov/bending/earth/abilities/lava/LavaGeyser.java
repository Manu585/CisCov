package com.bendersdestiny.ciscov.bending.earth.abilities.lava;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.configuration.ConfigManager;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LavaGeyser extends LavaAbility implements AddonAbility {
    private enum State {
        SELECTING_SOURCES,
    }

    @Attribute(Attribute.COOLDOWN) private long cooldown;
    @Attribute(Attribute.DURATION) private long eruptDuration;
    @Attribute(Attribute.RADIUS) private int eruptRadius;
    @Attribute(Attribute.HEIGHT) private int eruptHeight;
    @Attribute(Attribute.RANGE) private int sourceRange;

    private int maxSourceSelections;
    private long chargeTime;

    private Set<Block> sourceBlocks;
    private List<Geyser> activeGeysers;
    private Block lastHoveredSource;
    private State state;

    private int totalGeysersCreated;
    private int geysersErupted;

    public LavaGeyser(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) return;

        cooldown = ConfigManager.getDefaultConfig().getLong("Earth.Lava.LavaGeyser.Cooldown");
        maxSourceSelections = ConfigManager.getDefaultConfig().getInt("Earth.Lava.LavaGeyser.MaxSources");

        chargeTime    = (long) ConfigManager.getDefaultConfig().getDouble("Earth.Lava.LavaGeyser.ChargeTime");
        eruptDuration = (long) ConfigManager.getDefaultConfig().getDouble("Earth.Lava.LavaGeyser.Duration");

        eruptRadius   = ConfigManager.getDefaultConfig().getInt("Earth.Lava.LavaGeyser.Radius");
        eruptHeight   = ConfigManager.getDefaultConfig().getInt("Earth.Lava.LavaGeyser.Height");
        sourceRange   = ConfigManager.getDefaultConfig().getInt("Earth.Lava.LavaGeyser.Range");

        sourceBlocks   = new HashSet<>();
        activeGeysers  = new ArrayList<>();

        state = State.SELECTING_SOURCES;

        totalGeysersCreated = 0;
        geysersErupted = 0;

        start();
    }

    @Override
    public void progress() {
        if (!player.isOnline() || player.isDead()) {
            remove();
            return;
        }

        // Clean up finished geysers
        activeGeysers.removeIf(Geyser::isRemoved);

        if (activeGeysers.isEmpty() && geysersErupted == totalGeysersCreated && totalGeysersCreated > 0) {
            remove();
            return;
        }

        if (state == State.SELECTING_SOURCES) {
            handleSourceSelection();
        }
    }

    private void handleSourceSelection() {
        if (!player.isSneaking()) {
            return;
        }

        Block block = BlockSource.getEarthOrLavaSourceBlock(player, sourceRange, ClickType.SHIFT_DOWN);
        if (block == null) return;

        if (!block.equals(lastHoveredSource) && canSelectSource(block)) {
            Geyser geyser = new Geyser(player, block.getLocation(), eruptRadius, eruptHeight, eruptDuration, chargeTime, this);
            activeGeysers.add(geyser);
            sourceBlocks.add(block);
            lastHoveredSource = block;
            totalGeysersCreated++;

            if (sourceBlocks.size() >= maxSourceSelections) {
                player.sendMessage("Max sources selected!");
            }
        }
    }

    private boolean canSelectSource(Block block) {
        if (sourceBlocks.contains(block) ||sourceBlocks.size() >= maxSourceSelections) {
            return false;
        }

        Location blockLocation = block.getLocation();

        // Check for conflicts with existing geysers preparations
        for (Geyser existingGeyser : activeGeysers) {
            Location existingCenter = existingGeyser.getSourceCenter();
            double distance = blockLocation.distance(existingCenter);
            double minDistance = eruptRadius + existingGeyser.getRadius();

            if (distance < minDistance) {
                return false;
            }
        }
        return true;
    }

    public void handleLeftClick() {
        for (Geyser geyser : activeGeysers) {
            if (geyser.isEruptable() && geyser.getState() == Geyser.State.PREPARING_SOURCE) {
                geyser.startEruption();
                break;
            }
        }
    }

    public void onGeyserErupted() {
        geysersErupted++;
    }

    @Override
    public void remove() {
        for (Geyser geyser : activeGeysers) {
            geyser.remove();
        }

        activeGeysers.clear();
        sourceBlocks.clear();
        bPlayer.addCooldown(this);
        super.remove();
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
        return cooldown;
    }

    @Override
    public String getName() {
        return "LavaGeyser";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
    }

    @Override
    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        for (Block sourceBlock : sourceBlocks) {
            locations.add(sourceBlock.getLocation());
        }

        return locations;
    }

    @Override
    public void load() {}

    @Override
    public void stop() {}

    @Override
    public String getAuthor() {
        return CisCov.getInstance().getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public String getVersion() {
        return CisCov.getInstance().getPluginMeta().getVersion();
    }

    @Override
    public String getInstructions() {
        return "Tap sneak, select source blocks with cursor -> Left click to raise sources";
    }

    @Override
    public String getDescription() {
        return "* CisCov Addon *\nRaise fountains of Lava from the ground";
    }
}
