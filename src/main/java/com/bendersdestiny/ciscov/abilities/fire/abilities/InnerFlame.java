package com.bendersdestiny.ciscov.abilities.fire.abilities;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.util.nms.ActionBar;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

// TODO: Updated whole class / Ability, terrible execution and gameplay aka. Dogshit
public class InnerFlame extends FireAbility implements AddonAbility, PassiveAbility {
    private static final double MAX_TEMPERATURE = 10.0;
    private static final double HEAT_RATE = 0.1;
    private double coreTemperature = 0;

    private static final int TICK_INTERVAL = 3;

    public InnerFlame(Player player) {
        super(player);
        if (canStartAbility()) {
            start();
        }
    }

    @Override
    public void progress() {
        if (canStartAbility()) {

        }

        List<PotionEffect> badEffects = collectNegativeEffects();

        // Still build up heat even if no effects to purge
        if (badEffects.isEmpty()) {
            heatCore();
            showStatus();
            return;
        }

        // Heat up and Show UI
        heatCore();
        showStatus();

        // Purge effects
        purgeEffects(badEffects);

        // Apply Hunger and Nausea
        applyCosts();
    }

    private boolean canStartAbility() {
        return getBendingPlayer().hasElement(Element.FIRE) && getBendingPlayer().canBendIgnoreBinds(this) && hasAnyNegativeEffect();
    }

    private boolean hasAnyNegativeEffect() {
        return player.hasPotionEffect(PotionEffectType.POISON) || player.hasPotionEffect(PotionEffectType.WITHER);
    }

    private List<PotionEffect> collectNegativeEffects() {
        List<PotionEffect> bad = new ArrayList<>();

        PotionEffect poison = player.getPotionEffect(PotionEffectType.POISON);
        PotionEffect wither = player.getPotionEffect(PotionEffectType.WITHER);

        if (poison != null) bad.add(poison);
        if (wither != null) bad.add(wither);

        return bad;
    }

    private void heatCore() {
        coreTemperature = Math.min(MAX_TEMPERATURE, coreTemperature + HEAT_RATE);
    }

    private void showStatus() {
        ActionBar.sendActionBar(player, String.format("§cCore Temp: %.1f / %.1f", coreTemperature, MAX_TEMPERATURE));

        // Sweat particles
        Location head = player.getLocation().add(0, 1.8, 0);
        player.getWorld().spawnParticle(Particle.DRIPPING_WATER, head, 3, 0.2, 0.1, 0.2, 0.02);
    }

    private void purgeEffects(List<PotionEffect> effects) {
        int count = effects.size();

        // clear originals
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.WITHER);

        for (PotionEffect effect : effects) {
            reapplyWithScaledDuration(effect, count);
        }
    }

    private void reapplyWithScaledDuration(PotionEffect effect, int activeCount) {
        int ampLevel = effect.getAmplifier() + 1;
        int originalTicks = effect.getDuration();

        int ticksReduced = (int)Math.ceil(TICK_INTERVAL * (1.0 + (coreTemperature / MAX_TEMPERATURE)) / ampLevel);
        int newDuration = Math.max(0, originalTicks - ticksReduced);

        if (newDuration > 0) {
            player.addPotionEffect(new PotionEffect(effect.getType(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()));
        }
    }

    private void applyCosts() {
        int nauseaAmp  = 1 + (int) (coreTemperature / (MAX_TEMPERATURE / 3));
        int nauseaDur  = (int) (20 * coreTemperature);
        int hungerLoss = (int) Math.ceil(coreTemperature / 2.0);

        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, nauseaDur, nauseaAmp, true, false));
        player.setFoodLevel(Math.max(0, player.getFoodLevel() - hungerLoss));
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
    public boolean isEnabled() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 0;
    }

    @Override
    public String getName() {
        return "InnerFlame";
    }

    @Override
    public String getInstructions() {
        return "Sneak while having InnerFlame selected";
    }

    @Override
    public String getDescription() {
        return "* CisCov Addon *\nRaise your CoreTemperature to fight off bad potion effects in cost of hunger and nausea";
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
    public boolean isInstantiable() {
        return false;
    }

    @Override
    public boolean isProgressable() {
        return true;
    }
}
