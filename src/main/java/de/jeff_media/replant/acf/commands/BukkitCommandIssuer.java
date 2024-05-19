package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFBukkitUtil;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitCommandIssuer
implements CommandIssuer {
    private final BukkitCommandManager manager;
    private final CommandSender sender;

    BukkitCommandIssuer(BukkitCommandManager bukkitCommandManager, CommandSender commandSender) {
        this.manager = bukkitCommandManager;
        this.sender = commandSender;
    }

    @Override
    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    public CommandSender getIssuer() {
        return this.sender;
    }

    public Player getPlayer() {
        return this.isPlayer() ? (Player)this.sender : null;
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        if (this.isPlayer()) {
            return ((Player)this.sender).getUniqueId();
        }
        return UUID.nameUUIDFromBytes(this.sender.getName().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public CommandManager getManager() {
        return this.manager;
    }

    @Override
    public void sendMessageInternal(String string) {
        this.sender.sendMessage(ACFBukkitUtil.color(string));
    }

    @Override
    public boolean hasPermission(String string) {
        return this.sender.hasPermission(string);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        BukkitCommandIssuer bukkitCommandIssuer = (BukkitCommandIssuer)object;
        return Objects.equals(this.sender, bukkitCommandIssuer.sender);
    }

    public int hashCode() {
        return Objects.hash(this.sender);
    }
}

