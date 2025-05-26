package com.bendersdestiny.bending.fire;

import com.bendersdestiny.bending.fire.abilities.InnerFlame;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class FireBendingListener implements Listener {

    // -- ACTIVATIONS --
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

        if (bendingPlayer == null) return;
        if (bendingPlayer.getBoundAbility() == null) return;

        if (bendingPlayer.getBoundAbility().equals(CoreAbility.getAbility(InnerFlame.class))) {
            new InnerFlame(player);
        }
    }

    // -- ABILITIES --


    // -- PASSIVES --
}
