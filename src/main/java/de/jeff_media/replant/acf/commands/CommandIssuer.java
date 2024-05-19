package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.MessageKeys;
import de.jeff_media.replant.acf.commands.MessageType;
import de.jeff_media.replant.acf.locales.MessageKey;
import de.jeff_media.replant.acf.locales.MessageKeyProvider;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface CommandIssuer {
    public <T> T getIssuer();

    public CommandManager getManager();

    public boolean isPlayer();

    default public void sendMessage(String message) {
        this.getManager().sendMessage(this, MessageType.INFO, (MessageKeyProvider)MessageKeys.INFO_MESSAGE, "{message}", message);
    }

    @NotNull
    public UUID getUniqueId();

    public boolean hasPermission(String var1);

    default public void sendError(MessageKeyProvider key, String ... replacements) {
        this.sendMessage(MessageType.ERROR, key.getMessageKey(), replacements);
    }

    default public void sendSyntax(MessageKeyProvider key, String ... replacements) {
        this.sendMessage(MessageType.SYNTAX, key.getMessageKey(), replacements);
    }

    default public void sendInfo(MessageKeyProvider key, String ... replacements) {
        this.sendMessage(MessageType.INFO, key.getMessageKey(), replacements);
    }

    default public void sendError(MessageKey key, String ... replacements) {
        this.sendMessage(MessageType.ERROR, key, replacements);
    }

    default public void sendSyntax(MessageKey key, String ... replacements) {
        this.sendMessage(MessageType.SYNTAX, key, replacements);
    }

    default public void sendInfo(MessageKey key, String ... replacements) {
        this.sendMessage(MessageType.INFO, key, replacements);
    }

    default public void sendMessage(MessageType type, MessageKeyProvider key, String ... replacements) {
        this.sendMessage(type, key.getMessageKey(), replacements);
    }

    default public void sendMessage(MessageType type, MessageKey key, String ... replacements) {
        this.getManager().sendMessage(this, type, (MessageKeyProvider)key, replacements);
    }

    @Deprecated
    public void sendMessageInternal(String var1);
}

