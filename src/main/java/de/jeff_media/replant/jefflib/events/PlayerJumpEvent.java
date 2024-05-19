package de.jeff_media.replant.jefflib.events;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ServerUtils;
import de.jeff_media.replant.jefflib.events.PaperPlayerJumpEventListener;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PlayerJumpEvent
extends PlayerEvent
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    @NotNull
    private final Location from;
    @NotNull
    private final Location to;
    private boolean cancelled;

    public PlayerJumpEvent(@NotNull Player player, @NotNull Location location, @NotNull Location location2) {
        super(player);
        this.from = location;
        this.to = location2;
    }

    public static Listener registerListener() {
        Object object;
        Plugin plugin = JeffLib.getPlugin();
        if (ServerUtils.isRunningPaper()) {
            object = new PaperPlayerJumpEventListener();
            Bukkit.getPluginManager().registerEvents((Listener)object, plugin);
        } else {
            object = new SpigotListener();
            Bukkit.getPluginManager().registerEvents((Listener)object, plugin);
        }
        return object;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean bl) {
        this.cancelled = bl;
    }

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public Location getFrom() {
        return this.from;
    }

    @NotNull
    public Location getTo() {
        return this.to;
    }

    @Internal
    public static class SpigotListener
    implements Listener {
        @EventHandler
        public void onJump(PlayerStatisticIncrementEvent playerStatisticIncrementEvent) {
            if (playerStatisticIncrementEvent.getStatistic() != Statistic.JUMP) {
                return;
            }
            Player player = playerStatisticIncrementEvent.getPlayer();
            Location location = player.getLocation();
            Location location2 = player.getLocation().add(player.getVelocity().add(new Vector(0.0, 0.42, 0.0)));
            PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(playerStatisticIncrementEvent.getPlayer(), location, location2);
            Bukkit.getPluginManager().callEvent((Event)playerJumpEvent);
            if (playerJumpEvent.isCancelled()) {
                playerStatisticIncrementEvent.setCancelled(true);
                player.teleport(location);
            }
        }
    }
}

