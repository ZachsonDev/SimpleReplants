package de.jeff_media.replant.jefflib.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PaperPlayerJumpEventListener
implements Listener {
    @EventHandler
    public void onJump(PlayerJumpEvent playerJumpEvent) {
        PlayerJumpEvent playerJumpEvent2 = new PlayerJumpEvent(playerJumpEvent.getPlayer(), playerJumpEvent.getFrom(), playerJumpEvent.getTo());
        Bukkit.getPluginManager().callEvent((Event)playerJumpEvent2);
        if (playerJumpEvent2.isCancelled()) {
            playerJumpEvent.setCancelled(true);
        }
    }
}

