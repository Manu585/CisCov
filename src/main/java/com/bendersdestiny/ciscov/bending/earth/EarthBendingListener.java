package com.bendersdestiny.ciscov.bending.earth;

import com.bendersdestiny.ciscov.bending.earth.abilities.lava.LavaGeyser;
import com.bendersdestiny.ciscov.bending.earth.abilities.sand.SandSpout;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

public class EarthBendingListener implements Listener {

    // -- ACTIVATIONS --
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) { // Prevent duplicate call
            return;
        }

        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (bPlayer.getBoundAbility() == null) return;

        if (bPlayer.getBoundAbility() == CoreAbility.getAbility(LavaGeyser.class)) {
            if (CoreAbility.getAbility(player, LavaGeyser.class) != null) {
                LavaGeyser ability = CoreAbility.getAbility(player, LavaGeyser.class);
                ability.handleLeftClick();
            }
        } else if (bPlayer.getBoundAbility() == CoreAbility.getAbility(SandSpout.class)) {
            if (CoreAbility.getAbility(player, SandSpout.class) == null) {
                new SandSpout(player);
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

        if (bPlayer == null) return;
        if (bPlayer.getBoundAbility() == null) return;

        if (bPlayer.getBoundAbility() == CoreAbility.getAbility(LavaGeyser.class) && CoreAbility.getAbility(player, LavaGeyser.class) == null) {
            new LavaGeyser(player);
        }
    }

    // -- ABILITIES --


    // -- PASSIVES --

}
