package com.bendersdestiny.ciscov.listener;

import com.bendersdestiny.ciscov.CisCov;
import com.bendersdestiny.ciscov.configuration.ConfigManager;
import com.bendersdestiny.ciscov.event.PKCommandEvent;
import com.bendersdestiny.ciscov.util.CisCovUtil;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;

public class BendingCommandAndReloadListener implements Listener {
    String[] cmdaliases = {"/bending", "/bend", "/b", "/pk", "/projectkorra", "/korra", "/mtla", "/tla"};

    public static String[] developers = {"7d283a87-378d-4384-b748-3480dc7d3814"};

    @EventHandler
    public void onBendingConfigReload(BendingReloadEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getSender() instanceof Player player) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<color:#e8c410>CisCov config reloaded!</color>"));
                }

                ConfigManager.getDefaultConfigInstance().reload();
                ConfigManager.getDefaultConfigInstance().save();
            }
        }.runTaskLater(CisCov.getInstance(), 2);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        String[] args = cmd.split("\\s+");
        if (Arrays.asList(cmdaliases).contains(args[0]) && args.length >= 2) {
            PKCommandEvent new_event = new PKCommandEvent(event.getPlayer(), args, null);
            for (PKCommand command : PKCommand.instances.values()) {
                if (Arrays.asList(command.getAliases()).contains(args[1].toLowerCase())) {
                    new_event = new PKCommandEvent(event.getPlayer(), args, PKCommandEvent.CommandType.getType(command.getName()));
                }
            }
            Bukkit.getServer().getPluginManager().callEvent(new_event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPKCommand(final PKCommandEvent event) {
        new BukkitRunnable() {
            public void run() {
                if (event.getType() != null) {
                    if (event.getType().equals(PKCommandEvent.CommandType.WHO) && event.getSender().hasPermission("bending.command.who")) {
                        if (event.getArgs().length == 3) {
                            if (Bukkit.getPlayer(event.getArgs()[2]) != null) {
                                UUID uuid = Bukkit.getPlayer(event.getArgs()[2]).getUniqueId();
                                if (Arrays.asList(developers).contains(uuid.toString())) {
                                    event.getSender().sendMessage(MiniMessage.miniMessage().deserialize("<color:#e8c410>CisCov Developer</color>"));
                                }
                            }
                        }
                    }

                    if (event.getType().equals(PKCommandEvent.CommandType.VERSION) && event.getSender().hasPermission("bending.command.version")) {
                        CisCovUtil.sendCisCovInfo(event.getSender());
                    }
                }
            }
        }.runTaskLater(CisCov.getInstance(), 2);
    }
}
