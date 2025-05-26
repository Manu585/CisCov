package com.bendersdestiny.bending.air.abilities.spiritual.multiabilities.spiritualprojection;

import com.bendersdestiny.CisCov;
import com.bendersdestiny.bending.air.abilities.spiritual.multiabilities.spiritualprojection.sub.SpiritMode;
import com.bendersdestiny.bending.air.abilities.spiritual.multiabilities.spiritualprojection.sub.SpiritReturn;
import com.bendersdestiny.configuration.ConfigManager;
import com.bendersdestiny.util.nms.FakePlayer;
import com.bendersdestiny.util.nms.GlowManager;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.MultiAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class SpiritualProjection extends SpiritualAbility implements AddonAbility, MultiAbility {
    private final long chargeTime;
    private final long duration;

    private long chargeStartTime;
    private long startTime;
    private boolean charged = false;
    private boolean multiAbilityBound = false;
    private boolean transcending = false;

    private FakePlayer fakePlayer;

    public SpiritualProjection(Player player) {
        super(player);

        this.chargeTime = (long) ConfigManager.getDefaultConfig().getDouble("Air.Spiritual.SpiritualProjection.ChargeTime");
        this.duration = (long) ConfigManager.getDefaultConfig().getDouble("Air.Spiritual.SpiritualProjection.Duration");

        if (!this.bPlayer.canBend(this) || !this.bPlayer.canBendIgnoreBinds(this) || this.bPlayer.isOnCooldown(this)) {
            return;
        }

        this.chargeStartTime = System.currentTimeMillis();
        start();
    }

    @Override
    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            remove();
            return;
        }

        if (this.charged && System.currentTimeMillis() - this.startTime > this.duration) {
            this.transcending = true;
            this.player.getInventory().setHeldItemSlot(2);

            new SpiritReturn(this.player);
            return;
        }

        if (!this.charged) {
            if (player.isSneaking()) {
                startCharging();
            } else {
                super.remove();
            }
            return;
        }

        if (!this.multiAbilityBound && !this.player.isSneaking()) {
            MultiAbilityManager.bindMultiAbility(this.player, getName());
            createBodyAndLaunchPlayer();
            this.multiAbilityBound = true;
        }
    }

    private void createBodyAndLaunchPlayer() {
        this.fakePlayer = new FakePlayer(this.player);
        this.fakePlayer.spawnFakePlayer(new Location(getLocation().getWorld(), getLocation().getX(), getLocation().getY() - 1, getLocation().getZ(), getLocation().getYaw(), 40));
        Location eyeLoc = this.player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().add(new Vector(0, 4, 0));

        double speed = 0.2;
        Vector launch = direction.multiply(speed);

        this.player.setVelocity(launch);

        // Delay to make setFlying work better
        new BukkitRunnable() {
            @Override
            public void run() {
                new SpiritMode(player);
            }
        }.runTaskLaterAsynchronously(CisCov.getInstance(), 1L);
    }

    private void startCharging() {
        long elapsed = System.currentTimeMillis() - this.chargeStartTime;

        if (elapsed >= this.chargeTime) {
            this.player.playSound(this.player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2.0f, 2.0f);
            this.startTime = System.currentTimeMillis();
            this.charged = true;
        }
    }

    @Override
    public void remove() {
        super.remove();

        GlowManager.removeGlowing(this.player);
        this.player.setInvulnerable(false);
        this.player.setAllowFlight(false);
        this.player.setFlying(false);
        this.player.setInvisible(false);

        if (this.multiAbilityBound) {
            MultiAbilityManager.unbindMultiAbility(this.player);
        }

        this.fakePlayer.removePlayer();
        this.player.setGameMode(GameMode.SURVIVAL);
        this.bPlayer.addCooldown(this);
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 5000;
    }

    @Override
    public String getName() {
        return "SpiritualProjection";
    }

    @Override
    public Location getLocation() {
        return this.player != null ? this.player.getLocation() : null;
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
    public ArrayList<MultiAbilityManager.MultiAbilityInfoSub> getMultiAbilities() {
        ArrayList<MultiAbilityManager.MultiAbilityInfoSub> subAbilities = new ArrayList<>();

        subAbilities.add(new MultiAbilityManager.MultiAbilityInfoSub("SpiritMode",    Element.SPIRITUAL));
        subAbilities.add(new MultiAbilityManager.MultiAbilityInfoSub("SpiritualMode", Element.SPIRITUAL));
        subAbilities.add(new MultiAbilityManager.MultiAbilityInfoSub("SpiritReturn",  Element.SPIRITUAL));

        return subAbilities;
    }

    public boolean isMultiAbilityBound() {
        return this.multiAbilityBound;
    }

    public boolean isTranscending() {
        return transcending;
    }

    public FakePlayer getFakePlayer() {
        return fakePlayer;
    }
}
