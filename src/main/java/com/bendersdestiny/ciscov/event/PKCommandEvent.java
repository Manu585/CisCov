package com.bendersdestiny.ciscov.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PKCommandEvent extends Event {
    public enum CommandType {
        VERSION,
        WHO,
        CISCOV;

        public static CommandType getType(String string) {
            for (CommandType element : CommandType.values()) {
                if (element.toString().equalsIgnoreCase(string)) {
                    return element;
                }
            }
            return null;
        }
    }

    public static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final CommandType type;
    private final String[] args;

    public PKCommandEvent(Player sender, String[] args, CommandType type) {
        this.sender = sender;
        this.type = type;
        this.args = args;
    }

    public Player getSender() {
        return sender;
    }

    public CommandType getType() {
        return type;
    }

    public String[] getArgs() {
        return args;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
