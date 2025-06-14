package com.bendersdestiny.ciscov.bending.air.passives;

import com.bendersdestiny.ciscov.CisCov;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TwinkleToes extends AirAbility implements AddonAbility, PassiveAbility {
    public TwinkleToes(Player player) {
        super(player);
        this.start();
    }

    @Override
    public void progress() {}

    @Override
    public void remove() {}

    @Override
    public boolean isSneakAbility() { return false; }

    @Override
    public boolean isHarmlessAbility() { return true; }

    @Override
    public boolean isHiddenAbility() { return false; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public long getCooldown() { return 0; }

    @Override
    public String getName() {
        return "TwinkleToes";
    }

    @Override
    public String getDescription() {
        return "* CisCov Addon *\nAir benders have developed the ability to cushion their steps in order to prevent sculk sensors from being activated";
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
        return CisCov.getInstance().getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public String getVersion() {
        return CisCov.getInstance().getPluginMeta().getVersion();
    }

    @Override
    public boolean isInstantiable() {
        return false;
    }

    @Override
    public boolean isProgressable() {
        return false;
    }
}
