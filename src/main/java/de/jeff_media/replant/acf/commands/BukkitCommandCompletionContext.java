package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BukkitCommandIssuer;
import de.jeff_media.replant.acf.commands.CommandCompletionContext;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCommandCompletionContext
extends CommandCompletionContext<BukkitCommandIssuer> {
    BukkitCommandCompletionContext(RegisteredCommand registeredCommand, BukkitCommandIssuer bukkitCommandIssuer, String string, String string2, String[] stringArray) {
        super(registeredCommand, bukkitCommandIssuer, string, string2, stringArray);
    }

    public CommandSender getSender() {
        return (CommandSender)this.getIssuer().getIssuer();
    }

    public Player getPlayer() {
        return ((BukkitCommandIssuer)this.issuer).getPlayer();
    }
}

