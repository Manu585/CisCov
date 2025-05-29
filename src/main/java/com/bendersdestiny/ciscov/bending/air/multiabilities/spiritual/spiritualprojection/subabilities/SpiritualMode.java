package com.bendersdestiny.ciscov.bending.air.multiabilities.spiritual.spiritualprojection.subabilities;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.util.nms.GlowManager;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpiritualMode extends SpiritualAbility implements AddonAbility {
    public SpiritualMode(Player player) {
        super(player);

        player.setGameMode(GameMode.SPECTATOR);
        GlowManager.removeGlowing(player);
    }

    @Override
    public void progress() {

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
        return "SpiritualMode";
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
