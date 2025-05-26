package com.bendersdestiny.bending.air;

import com.bendersdestiny.bending.air.abilities.spiritual.multiabilities.spiritualprojection.SpiritualProjection;
import com.bendersdestiny.bending.air.abilities.spiritual.multiabilities.spiritualprojection.sub.SpiritMode;
import com.bendersdestiny.bending.air.abilities.spiritual.multiabilities.spiritualprojection.sub.SpiritReturn;
import com.bendersdestiny.bending.air.abilities.spiritual.multiabilities.spiritualprojection.sub.SpiritualMode;
import com.bendersdestiny.bending.air.passives.TwinkleToes;
import com.bendersdestiny.util.nms.FakePlayer;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.event.AbilityDamageEntityEvent;
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

    @EventHandler
    public void onAbilityDamage(AbilityDamageEntityEvent event) {
        int hitEntity = event.getEntity().getEntityId();

        FakePlayer fake = FakePlayer.getByEntityId(hitEntity);
        if (fake == null) return;


        fake.damage(event.getDamage());

        event.getAbility().getPlayer().sendMessage("Damage: " + event.getDamage());

        if (fake.getHealth() <= 0) {
            fake.removePlayer();
        }
    }

    // SpiritualProjection
    @EventHandler
    public void onLeftClickWithSpiritualMode(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);

        if (bendingPlayer == null) return;

        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        SpiritualProjection spiritualProjection = CoreAbility.getAbility(player, SpiritualProjection.class);
        if (spiritualProjection == null || !spiritualProjection.isMultiAbilityBound()) return;

        int itemSlot = player.getInventory().getHeldItemSlot();

        if (itemSlot == 1) {
            player.sendMessage("Test");
            new SpiritualMode(player);
        }
    }

    @EventHandler
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        SpiritualProjection spiritualProjection = CoreAbility.getAbility(player, SpiritualProjection.class);

        if (spiritualProjection == null || !spiritualProjection.isMultiAbilityBound()) {
            return;
        }

        if (spiritualProjection.isTranscending()) {
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
