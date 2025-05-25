package com.bendersdestiny.bending.air;

import com.bendersdestiny.bending.air.passives.TwinkleToes;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AirBendingListener implements Listener {

    // -- ACTIVATIONS --
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

    }

    // -- AIR ABILITIES --


    // -- AIR PASSIVES --

    // TWINKLE TOES
    @EventHandler
    public void onBlockReceiveGameEvent(BlockReceiveGameEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null || !bPlayer.hasElement(Element.AIR) || !bPlayer.canBendIgnoreBinds(CoreAbility.getAbility(TwinkleToes.class))) {
            return;
        }

        event.setCancelled(true);
    }

}
