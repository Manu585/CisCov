package com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.listeners;

import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.SpiritualProjection;
import com.bendersdestiny.util.nms.FakePlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityDamageEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class FakePlayerDamageListener implements Listener {

    @EventHandler
    public void onHitboxDamageByAbility(AbilityDamageEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand as)) return;
        if (!as.hasMetadata("FakePlayerHitbox")) return;

        FakePlayer fake = FakePlayer.getByHitboxId(as.getEntityId());
        if (fake == null) return;
        fake.damage(event.getDamage());

        SpiritualProjection projection = CoreAbility.getAbility(fake.getPlayer(), SpiritualProjection.class);
        if (projection == null || !projection.isMultiAbilityBound()) return;


        fake.getPlayer().sendMessage(ChatColor.RED + "Your physical body is being attacked! Remaining Health: " + fake.getHealth());
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity dead = event.getEntity();

    }
}
