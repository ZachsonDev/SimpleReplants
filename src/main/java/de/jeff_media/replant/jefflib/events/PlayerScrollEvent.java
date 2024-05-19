package de.jeff_media.replant.jefflib.events;

import com.allatori.annotations.DoNotRename;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class PlayerScrollEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player who;
    private final ScrollDirection direction;
    private boolean cancelled;

    public PlayerScrollEvent(@NotNull Player player, ScrollDirection scrollDirection) {
        this.who = player;
        this.direction = scrollDirection;
    }

    @DoNotRename
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @DoNotRename
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }

    public Player getWho() {
        return this.who;
    }

    public ScrollDirection getDirection() {
        return this.direction;
    }

    public static enum ScrollDirection {
        UP,
        DOWN;

    }
}

