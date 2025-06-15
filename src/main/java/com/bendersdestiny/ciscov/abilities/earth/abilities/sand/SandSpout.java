package com.bendersdestiny.ciscov.abilities.earth.abilities.sand;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.configuration.ConfigManager;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SandSpout extends SandAbility implements AddonAbility {
    @Attribute(Attribute.COOLDOWN) private long cooldown;
    @Attribute(Attribute.DURATION) private long duration;
    @Attribute(Attribute.HEIGHT)   private int maxHeight;
    @Attribute(Attribute.SPEED)    private int speed;

    private long abilityStartTime;
    private boolean formed;

    public SandSpout(Player player) {
        super(player);

        cooldown  = ConfigManager.getDefaultConfig().getLong("Earth.Sand.SandSpout.Cooldown");
        duration  = ConfigManager.getDefaultConfig().getLong("Earth.Sand.SandSpout.Duration");
        maxHeight = ConfigManager.getDefaultConfig().getInt("Earth.Sand.SandSpout.MaxHeight");
        speed     = ConfigManager.getDefaultConfig().getInt("Earth.Sand.SandSpout.Speed");

        formed = false;

        abilityStartTime = System.currentTimeMillis();

        if (!bPlayer.canBend(this)) {
            return;
        }

        start();
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
        return cooldown;
    }

    @Override
    public String getName() {
        return "SandSpout";
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
        return CisCov.getInstance().getPluginMeta().getAuthors().getFirst();
    }

    @Override
    public String getVersion() {
        return CisCov.getInstance().getPluginMeta().getVersion();
    }
}
