package com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection;

import com.bendersdestiny.CisCov;
import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.subabilities.SpiritMode;
import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.subabilities.SpiritReturn;
import com.bendersdestiny.configuration.ConfigManager;
import com.bendersdestiny.util.nms.FakePlayer;
import com.bendersdestiny.util.nms.GlowManager;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.MultiAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import com.projectkorra.projectkorra.ability.util.MultiAbilityManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class SpiritualProjection extends SpiritualAbility implements AddonAbility, MultiAbility {
    private final long chargeTime;
    private final long duration;

    private long chargeStartTime;
    private long startTime;

    private boolean charged = false;
    private boolean multiAbilityBound = false;
    private boolean abilityEnded = false;

    private FakePlayer fakePlayer;

    public SpiritualProjection(Player player) {
        super(player);

        this.chargeTime = (long) ConfigManager.getDefaultConfig().getDouble("Air.Spiritual.SpiritualProjection.ChargeTime");
        this.duration   = (long) ConfigManager.getDefaultConfig().getDouble("Air.Spiritual.SpiritualProjection.Duration");

        if (!this.bPlayer.canBend(this) || this.bPlayer.isOnCooldown(this)) {
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

        if (!this.bPlayer.canBend(this)) {
            return;
        }

        // Ability end logic
        if (this.charged && System.currentTimeMillis() - this.startTime > this.duration) {
            this.abilityEnded = true;
            this.player.getInventory().setHeldItemSlot(2);

            new SpiritReturn(this.player);
            return;
        }

        // Charge ability
        if (!this.charged) {
            if (this.player.isSneaking() && this.bPlayer.getBoundAbility() == CoreAbility.getAbility(SpiritualProjection.class)) {
                startCharging();
            } else {
                super.remove();
            }
            return;
        }

        // Bind multiability and let AirBendingListener handle the rest
        if (!this.multiAbilityBound && !this.player.isSneaking()) {
            MultiAbilityManager.bindMultiAbility(this.player, getName());
            createBodyAndLaunchPlayer();
            this.multiAbilityBound = true;
        }
    }

    private void createBodyAndLaunchPlayer() {
        // Create FakePlayer
        this.fakePlayer = new FakePlayer(this.player);
        this.fakePlayer.spawnFakePlayer(new Location(getLocation().getWorld(), getLocation().getX(), getLocation().getY() - 1, getLocation().getZ(), getLocation().getYaw(), 40));

        Vector direction = this.player.getEyeLocation().getDirection().add(new Vector(0, 3, 0));
        Vector launch = direction.multiply(0.3);

        // Launch player out of the physical body
        this.player.setVelocity(launch);

        new SpiritMode(player);
    }

    private void startCharging() {
        long elapsed = System.currentTimeMillis() - this.chargeStartTime;

        if (elapsed >= this.chargeTime) {
            this.player.playSound(this.player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_3, 1.0f, 1.0f);
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

    /**
     * Check if the ability is supposed to be over to start SpiritReturn ability and cancel hotbar changes
     */
    public boolean abilityEnded() {
        return abilityEnded;
    }

    public FakePlayer getFakePlayer() {
        return fakePlayer;
    }
}
