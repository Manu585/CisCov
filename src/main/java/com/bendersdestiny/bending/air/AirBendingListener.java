package com.bendersdestiny.bending.air;

import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.SpiritualProjection;
import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.subabilities.SpiritMode;
import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.subabilities.SpiritReturn;
import com.bendersdestiny.bending.air.multiabilities.spiritual.spiritualprojection.subabilities.SpiritualMode;
import com.bendersdestiny.bending.air.passives.TwinkleToes;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AirBendingListener implements Listener {

    // -- ACTIVATIONS --
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

        if (bendingPlayer == null) return;
        if (bendingPlayer.getBoundAbility() == null) return;

        if (bendingPlayer.getBoundAbility().equals(CoreAbility.getAbility(SpiritualProjection.class))) {
            new SpiritualProjection(player);
        }
    }

    // -- ABILITIES --

    // -- SpiritualProjection --

    /**
     * Handle all subability slot switches and instantiate subclasses upon hovering the new slot
     */
    @EventHandler
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        SpiritualProjection spiritualProjection = CoreAbility.getAbility(player, SpiritualProjection.class);
        if (spiritualProjection == null || !spiritualProjection.isMultiAbilityBound()) {
            return;
        }

        // Cancel if ability is supposed to end
        if (spiritualProjection.abilityEnded()) {
            event.setCancelled(true);
        }

        int slot = event.getNewSlot();
        switch (slot) {
            case 0:
                if (CoreAbility.getAbility(player, SpiritMode.class) == null) {
                    new SpiritMode(player);
                }
                break;
            case 2:
                if (CoreAbility.getAbility(player, SpiritReturn.class) == null) {
                    new SpiritReturn(player);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Set Player into {@link SpiritualMode} when left-clicking whilest hovering correct slot
     */
    @EventHandler
    public void onLeftClickWithSpiritualMode(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) { // Prevent duplicate call
            return;
        }

        Player player = event.getPlayer();
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

        if (bendingPlayer == null) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        SpiritualProjection spiritualProjection = CoreAbility.getAbility(player, SpiritualProjection.class);
        if (spiritualProjection == null || !spiritualProjection.isMultiAbilityBound()) return;

        // Set into SpiritualMode if the correct slot hovered and left-clicked
        if (player.getInventory().getHeldItemSlot() == 1) {
            new SpiritualMode(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        SpiritualProjection spiritualProjection = CoreAbility.getAbility(player, SpiritualProjection.class);

        if (spiritualProjection != null) {
            spiritualProjection.remove();
        }
    }

    // -- PASSIVES --

    // TWINKLE TOES

    /**
     * Make Sculk sensors not receive a sound signal if the player has element air and can bend TwinkleToes
     */
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
