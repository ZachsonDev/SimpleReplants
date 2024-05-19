package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BukkitCommandIssuer;
import de.jeff_media.replant.acf.commands.ConditionContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitConditionContext
extends ConditionContext<BukkitCommandIssuer> {
    BukkitConditionContext(BukkitCommandIssuer bukkitCommandIssuer, String string) {
        super(bukkitCommandIssuer, string);
    }

    public CommandSender getSender() {
        return ((BukkitCommandIssuer)this.getIssuer()).getIssuer();
    }

    public Player getPlayer() {
        return ((BukkitCommandIssuer)this.getIssuer()).getPlayer();
    }
}

