package com.bendersdestiny.ciscov.bending.earth.abilities.lava;

import com.bendersdestiny.ciscov.CisCov;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.util.TempFallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Geyser extends LavaAbility {
    private enum State {
        PREPARING_SOURCE,
        ERUPTING,
        REMOVING
    }

    private final Set<TempBlock> tempBlocks;
    private final Location sourceCenter;
    private final int radius;
    private final int height;
    private final long duration;
    private final long chargeTime;
    private final long prepareStartTime;
    private final LavaGeyser parentAbility;

    private long eruptStartTime;
    private boolean eruptable;
    private boolean removed;
    private boolean fallingBlocksLaunched;
    private State state;

    public Geyser(Player player, Location sourceCenter, int radius, int height, long duration, long chargeTime, LavaGeyser parentAbility) {
        super(player);

        this.sourceCenter = sourceCenter;
        this.radius = radius;
        this.height = height;
        this.duration = duration;
        this.chargeTime = chargeTime;
        this.tempBlocks = new HashSet<>();
        this.parentAbility = parentAbility;

        this.state = State.PREPARING_SOURCE;
        this.prepareStartTime = System.currentTimeMillis();
        this.removed = false;
        this.fallingBlocksLaunched = false;

        start();
    }

    @Override
    public void progress() {
        switch (state) {
            case PREPARING_SOURCE:
                prepareSource();
                break;
            case ERUPTING:
                eruptSource();
                break;
            case REMOVING:
                remove();
                break;
        }
    }

    private void prepareSource() {
        long chargeElapsed = System.currentTimeMillis() - prepareStartTime;
        double progress = Math.min(1.0, (double) chargeElapsed / chargeTime);

        // Get all blocks in radius
        List<Block> circularBlocks = getCircularBlocks(sourceCenter, radius);

        // Sort blocks by distance from center (center first)
        circularBlocks.sort((b1, b2) -> {
            double distance1 = Math.sqrt(b1.getLocation().distanceSquared(sourceCenter));
            double distance2 = Math.sqrt(b2.getLocation().distanceSquared(sourceCenter));
            return Double.compare(distance1, distance2);
        });

        // Process all blocks that should be affected at this progress level
        int totalBlocks = circularBlocks.size();
        int blocksToProcess = (int) (progress * totalBlocks);

        for (int i = 0; i < blocksToProcess && i < totalBlocks; i++) {
            Block block = circularBlocks.get(i);

            // Calculate distance from center for this block
            double distanceFromCenter = Math.sqrt(block.getLocation().distanceSquared(sourceCenter));
            double normalizedDistance = distanceFromCenter / radius; // 0.0 (center) to 1.0 (edge)

            // Determine material based on progress and distance
            Material targetMaterial = determineBlockMaterial(progress, normalizedDistance, block);

            // Create or update TempBlock
            TempBlock existingTempBlock = TempBlock.get(block);

            if (existingTempBlock == null) {
                // First time creating a temp block for this location
                TempBlock newTempBlock = new TempBlock(block, targetMaterial);
                tempBlocks.add(newTempBlock);
            } else {
                // Update existing temp block if material should change
                if (existingTempBlock.getBlock().getType() != targetMaterial) {
                    existingTempBlock.revertBlock();
                    tempBlocks.remove(existingTempBlock);
                    TempBlock updatedTempBlock = new TempBlock(block, targetMaterial);
                    tempBlocks.add(updatedTempBlock);
                }
            }
        }

        // Check if preparation is complete
        if (chargeElapsed >= chargeTime) {
            eruptable = true;
        }
    }

    /**
     * Determines what material a block should be based on progress and distance from center
     */
    private Material determineBlockMaterial(double progress, double normalizedDistance, Block block) {
        Random blockRandom = new Random(block.getLocation().hashCode());

        // Center always starts as stone and becomes magma earlier
        if (normalizedDistance <= 0.2) { // Center 20% radius
            if (progress >= 0.3) {
                return Material.MAGMA_BLOCK;
            } else {
                return Material.STONE;
            }
        }

        // Calculate magma probability based on progress and distance
        // Closer to center = higher chance of magma
        // Higher progress  = higher chance of magma
        double baseProgressFactor = Math.pow(progress, 1.5);
        double distanceFactor = 1.0 - normalizedDistance;
        double magmaProbability = baseProgressFactor * distanceFactor;

        // Add some randomness for organic feel
        double randomOffset = (blockRandom.nextDouble() - 0.5) * 0.3; // random offset
        magmaProbability = Math.max(0.0, Math.min(1.0, magmaProbability + randomOffset));

        // At high progress most blocks should be magma
        if (progress >= 0.8) {
            magmaProbability = Math.max(magmaProbability, 0.7 + (progress - 0.8) * 1.5);
        }

        // At very high progress all blocks should be magma
        if (progress >= 0.95) {
            return Material.MAGMA_BLOCK;
        }

        // Use random chance to determine material
        if (blockRandom.nextDouble() < magmaProbability) {
            return Material.MAGMA_BLOCK;
        } else {
            return Material.STONE;
        }
    }

    public void startEruption() {
        if (eruptable && state == State.PREPARING_SOURCE) {
            state = State.ERUPTING;
            eruptStartTime = System.currentTimeMillis();
        }
    }

    private void eruptSource() {
        long eruptElapsed = System.currentTimeMillis() - eruptStartTime;

        // Play boom sound effect
        if (eruptElapsed < 100) {
            if (sourceCenter.getWorld() == null) return;
            sourceCenter.getWorld().playSound(sourceCenter, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
            sourceCenter.getWorld().playSound(sourceCenter, Sound.BLOCK_LAVA_EXTINGUISH, 1.5f, 0.3f);
        }

        // Launch falling blocks
        if (!fallingBlocksLaunched && eruptElapsed < 100) {
            launchFallingBlocks();
            fallingBlocksLaunched = true;
        }

        // Calculate eruption progress (0.0 to 1.0)
        double eruptProgress = Math.min(1.0, (double) eruptElapsed / 1000.0); // 1 second to reach full height

        // Current height to build lava column
        int currentHeight = (int) (eruptProgress * height);

        // Build lava column from bottom up
        buildLavaColumn(currentHeight);

        // Check if eruption duration is over
        if (eruptElapsed >= duration) {
            state = State.REMOVING;
            // Notify parent ability that this geyser has finished erupting
            if (parentAbility != null) {
                parentAbility.onGeyserErupted();
            }
        }
    }

    private void buildLavaColumn(int targetHeight) {
        for (int y = 1; y <= targetHeight; y++) {
            // Calculate radius for this height level
            int currentRadius = getRadiusForHeight(y, targetHeight);

            // Get circular blocks for this height level
            List<Block> levelBlocks = getCircularBlocksAtHeight(sourceCenter, currentRadius, y);

            for (Block block : levelBlocks) {
                // Create lava temp block
                TempBlock existingTempBlock = TempBlock.get(block);
                if (existingTempBlock == null) {
                    TempBlock lavaTempBlock = new TempBlock(block, Material.LAVA);
                    tempBlocks.add(lavaTempBlock);
                }
            }
        }
    }

    private int getRadiusForHeight(int currentHeight, int maxHeight) {
        // Calculate 85% height threshold
        double taperThreshold = maxHeight * 0.85;

        if (currentHeight <= taperThreshold) {
            // Below 85% height use full radius
            return radius;
        } else {
            // Above 85% height taper the radius
            double taperProgress = (currentHeight - taperThreshold) / (maxHeight - taperThreshold);
            // Reduce radius from full to 1
            return (int) Math.max(1, radius * (1.0 - taperProgress));
        }
    }

    private List<Block> getCircularBlocksAtHeight(Location center, int radius, int height) {
        List<Block> blocks = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Calculate distance from center
                double distance = Math.sqrt(x * x + z * z);

                // Only include blocks within the circular radius
                if (distance <= radius) {
                    Block block = center.clone().add(x, height, z).getBlock();
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    private void launchFallingBlocks() {
        // Get magma blocks from the prepared source
        List<TempBlock> magmaBlocks = tempBlocks.stream()
                .filter(tb -> tb.getBlock().getType() == Material.MAGMA_BLOCK)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // Convert all magma blocks to falling blocks shooting upward
        for (TempBlock magmaBlock : magmaBlocks) {
            Location launchLocation = magmaBlock.getLocation().clone().add(0.5, 1, 0.5);

            // Create upward velocity with a slight random spread
            Random random = new Random();
            double velocityY = 1.2 + (random.nextDouble() * 0.2); // Random upward velocity
            double velocityX = (random.nextDouble() - 0.3) * 0.3; // Random horizontal spread
            double velocityZ = (random.nextDouble() - 0.3) * 0.3; // Random horizontal spread

            Vector velocity = new Vector(velocityX, velocityY, velocityZ);

            // Create a falling block with magma block data
            TempFallingBlock fallingBlock = new TempFallingBlock(
                    launchLocation,
                    Material.MAGMA_BLOCK.createBlockData(),
                    velocity,
                    this
                );

            // Set falling block properties
            fallingBlock.getFallingBlock().setDropItem(false);
            fallingBlock.getFallingBlock().setHurtEntities(false);
            fallingBlock.getFallingBlock().setFallDistance(0);
        }
    }

    @Override
    public void remove() {
        if (!removed) {
            // Don't immediately revert blocks let the cooling animation handle it
            scheduleLavaCooldown();
            removed = true;
        }
        super.remove();
    }

    /**
     * Gets all blocks in a circular pattern around the center location
     */
    private List<Block> getCircularBlocks(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Calculate distance from center
                double distance = Math.sqrt(x * x + z * z);

                // Only include blocks within the circular radius
                if (distance <= radius) {
                    Block block = center.clone().add(x, 0, z).getBlock();

                    // Only include valid earth/lava source blocks
                    if (LavaAbility.isEarthbendable(player, block)) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    private void scheduleLavaCooldown() {
        // Get all lava blocks created during eruption
        List<TempBlock> lavaBlocks = tempBlocks.stream()
                .filter(tb -> tb.getBlock().getType() == Material.LAVA)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // Get all other temp blocks from preparation
        List<TempBlock> otherBlocks = tempBlocks.stream()
                .filter(tb -> tb.getBlock().getType() != Material.LAVA)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // Immediately revert non-lava blocks
        for (TempBlock otherBlock : otherBlocks) {
            otherBlock.revertBlock();
        }

        // If no lava blocks, clear and exit
        if (lavaBlocks.isEmpty()) {
            tempBlocks.clear();
            return;
        }

        // Sort lava blocks by height top to bottom
        lavaBlocks.sort((tb1, tb2) -> Integer.compare(tb2.getLocation().getBlockY(), tb1.getLocation().getBlockY()));

        // Create a set to track cooling temp blocks
        Set<TempBlock> coolingTempBlocks = new HashSet<>();

        // Schedule gradual cooling animation for lava blocks
        for (int i = 0; i < lavaBlocks.size(); i++) {
            TempBlock lavaBlock = lavaBlocks.get(i);
            final int delay = i * 3; // 3 ticks delay between each block

            new BukkitRunnable() {
                @Override
                public void run() {
                    // Check if block still exists and is lava
                    if (lavaBlock.getBlock().getType() == Material.LAVA) {
                        // Create TempBlock for magma stage
                        TempBlock magmaTempBlock = new TempBlock(lavaBlock.getBlock(), Material.MAGMA_BLOCK);
                        coolingTempBlocks.add(magmaTempBlock);

                        // Second stage Magma to Stone after delay
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (magmaTempBlock.getBlock().getType() == Material.MAGMA_BLOCK) {
                                    // Revert magma temp block and create stone temp block
                                    magmaTempBlock.revertBlock();
                                    coolingTempBlocks.remove(magmaTempBlock);

                                    TempBlock stoneTempBlock = new TempBlock(magmaTempBlock.getBlock(), Material.STONE);
                                    coolingTempBlocks.add(stoneTempBlock);

                                    // Final stage Stone to Original block (revert)
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            stoneTempBlock.revertBlock();
                                            coolingTempBlocks.remove(stoneTempBlock);
                                        }
                                    }.runTaskLater(CisCov.getInstance(), 15);
                                }
                            }
                        }.runTaskLater(CisCov.getInstance(), 10);
                    }

                    // Revert original lava temp block
                    lavaBlock.revertBlock();
                }
            }.runTaskLater(CisCov.getInstance(), delay);
        }

        // Clear the tempBlocks set after all animations are scheduled
        new BukkitRunnable() {
            @Override
            public void run() {
                tempBlocks.clear();

                for (TempBlock coolingBlock : coolingTempBlocks) {
                    if (coolingBlock.getBlock().getType() != Material.AIR) {
                        coolingBlock.revertBlock();
                    }
                }
                coolingTempBlocks.clear();
            }
        }.runTaskLater(CisCov.getInstance(), (lavaBlocks.size() * 3L) + 40);
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "Geyser";
    }

    @Override
    public Location getLocation() {
        return sourceCenter;
    }

    @Override
    public boolean isHiddenAbility() {
        return true;
    }

    public Location getSourceCenter() {
        return sourceCenter;
    }

    public int getRadius() {
        return radius;
    }

    public int getHeight() {
        return height;
    }

    public long getDuration() {
        return duration;
    }

    public long getChargeTime() {
        return chargeTime;
    }

    public boolean isEruptable() {
        return eruptable;
    }

    public boolean isRemoved() {
        return removed;
    }

    public Set<TempBlock> getTempBlocks() {
        return tempBlocks;
    }
}
