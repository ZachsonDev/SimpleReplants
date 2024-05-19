package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

class ACFBukkitListener
implements Listener {
    private BukkitCommandManager manager;
    private final Plugin plugin;

    public ACFBukkitListener(BukkitCommandManager bukkitCommandManager, Plugin plugin) {
        this.manager = bukkitCommandManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent pluginDisableEvent) {
        if (!this.plugin.getName().equalsIgnoreCase(pluginDisableEvent.getPlugin().getName())) {
            return;
        }
        this.manager.unregisterCommands();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (this.manager.autoDetectFromClient) {
            this.manager.readPlayerLocale(player);
            this.manager.getScheduler().createDelayedTask(this.plugin, () -> this.manager.readPlayerLocale(player), 20L);
        } else {
            this.manager.setIssuerLocale(player, this.manager.getLocales().getDefaultLocale());
            this.manager.notifyLocaleChange(this.manager.getCommandIssuer(player), null, this.manager.getLocales().getDefaultLocale());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        UUID uUID = playerQuitEvent.getPlayer().getUniqueId();
        this.manager.issuersLocale.remove(uUID);
        this.manager.issuersLocaleString.remove(uUID);
    }
}

