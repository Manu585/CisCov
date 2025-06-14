package com.bendersdestiny.ciscov.bending.air.multiabilities.spiritual.spiritualprojection;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.util.nms.GlowManager;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SpiritualAbility;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpiritMode extends SpiritualAbility implements AddonAbility {
    public SpiritMode(Player player) {
        super(player);

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setInvisible(true);
        player.setInvulnerable(true);

        GlowManager.setGlowing(player, NamedTextColor.AQUA);
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
        return false;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "SpiritMode";
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
