package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BukkitCommandIssuer;
import de.jeff_media.replant.acf.commands.CommandExecutionContext;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCommandExecutionContext
extends CommandExecutionContext<BukkitCommandExecutionContext, BukkitCommandIssuer> {
    BukkitCommandExecutionContext(RegisteredCommand registeredCommand, CommandParameter commandParameter, BukkitCommandIssuer bukkitCommandIssuer, List<String> list, int n, Map<String, Object> map) {
        super(registeredCommand, commandParameter, bukkitCommandIssuer, list, n, map);
    }

    public CommandSender getSender() {
        return ((BukkitCommandIssuer)this.issuer).getIssuer();
    }

    public Player getPlayer() {
        return ((BukkitCommandIssuer)this.issuer).getPlayer();
    }
}

