package de.jeff_media.replant.updatechecker;

import de.jeff_media.replant.updatechecker.Messages;
import de.jeff_media.replant.updatechecker.UpdateCheckEvent;
import de.jeff_media.replant.updatechecker.UpdateChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

class InternalUpdateCheckListener
implements Listener {
    private final UpdateChecker instance = UpdateChecker.getInstance();

    InternalUpdateCheckListener() {
    }

    @EventHandler
    public void notifyOnJoin(PlayerJoinEvent playerJoinEvent) {
        if (!this.instance.isCheckedAtLeastOnce()) {
            return;
        }
        Player player = playerJoinEvent.getPlayer();
        if (player.isOp() && this.instance.isNotifyOpsOnJoin() || this.instance.getNotifyPermission() != null && player.hasPermission(this.instance.getNotifyPermission())) {
            Messages.printCheckResultToPlayer(player, false);
        }
    }

    @EventHandler
    public void onUpdateCheck(UpdateCheckEvent updateCheckEvent) {
        if (!this.instance.isCheckedAtLeastOnce()) {
            return;
        }
        if (!this.instance.isNotifyRequesters()) {
            return;
        }
        if (updateCheckEvent.getRequesters() == null) {
            return;
        }
        for (CommandSender commandSender : updateCheckEvent.getRequesters()) {
            if (commandSender instanceof Player) {
                Messages.printCheckResultToPlayer((Player)commandSender, true);
                continue;
            }
            Messages.printCheckResultToConsole(updateCheckEvent);
        }
    }
}

