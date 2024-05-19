package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.MessageFormatter;
import org.bukkit.ChatColor;

public class BukkitMessageFormatter
extends MessageFormatter<ChatColor> {
    public BukkitMessageFormatter(ChatColor ... chatColorArray) {
        super(chatColorArray);
    }

    @Override
    String format(ChatColor chatColor, String string) {
        return chatColor + string;
    }
}

