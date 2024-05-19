package de.jeff_media.replant.jefflib.internal.listeners;

import de.jeff_media.replant.jefflib.events.PlayerScrollEvent;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

@Internal
public final class PlayerScrollListener
implements Listener {
    @EventHandler
    public void onScroll(PlayerItemHeldEvent playerItemHeldEvent) {
        Player player = playerItemHeldEvent.getPlayer();
        PlayerScrollEvent.ScrollDirection scrollDirection = playerItemHeldEvent.getPreviousSlot() == 8 && playerItemHeldEvent.getNewSlot() == 0 ? PlayerScrollEvent.ScrollDirection.UP : (playerItemHeldEvent.getPreviousSlot() == 0 && playerItemHeldEvent.getNewSlot() == 8 ? PlayerScrollEvent.ScrollDirection.DOWN : (playerItemHeldEvent.getPreviousSlot() < playerItemHeldEvent.getNewSlot() ? PlayerScrollEvent.ScrollDirection.UP : PlayerScrollEvent.ScrollDirection.DOWN));
        PlayerScrollEvent playerScrollEvent = new PlayerScrollEvent(player, scrollDirection);
        Bukkit.getPluginManager().callEvent((Event)playerScrollEvent);
        if (playerScrollEvent.isCancelled()) {
            playerItemHeldEvent.setCancelled(true);
        }
    }
}

