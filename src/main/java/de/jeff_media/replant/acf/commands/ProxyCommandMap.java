package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.RootCommand;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

class ProxyCommandMap
extends SimpleCommandMap {
    private BukkitCommandManager manager;
    CommandMap proxied;

    ProxyCommandMap(BukkitCommandManager bukkitCommandManager, CommandMap commandMap) {
        super(Bukkit.getServer());
        this.manager = bukkitCommandManager;
        this.proxied = commandMap;
    }

    public void registerAll(String string, List<Command> list) {
        this.proxied.registerAll(string, list);
    }

    public boolean register(String string, String string2, Command command) {
        if (this.isOurCommand(command)) {
            return super.register(string, string2, command);
        }
        return this.proxied.register(string, string2, command);
    }

    boolean isOurCommand(String string) {
        String[] stringArray = ACFPatterns.SPACE.split(string);
        return stringArray.length != 0 && this.isOurCommand((Command)this.knownCommands.get(stringArray[0].toLowerCase(Locale.ENGLISH)));
    }

    boolean isOurCommand(Command command) {
        return command instanceof RootCommand && ((RootCommand)command).getManager() == this.manager;
    }

    public boolean register(String string, Command command) {
        if (this.isOurCommand(command)) {
            return super.register(string, command);
        }
        return this.proxied.register(string, command);
    }

    public boolean dispatch(CommandSender commandSender, String string) {
        if (this.isOurCommand(string)) {
            return super.dispatch(commandSender, string);
        }
        return this.proxied.dispatch(commandSender, string);
    }

    public void clearCommands() {
        super.clearCommands();
        this.proxied.clearCommands();
    }

    public Command getCommand(String string) {
        if (this.isOurCommand(string)) {
            return super.getCommand(string);
        }
        return this.proxied.getCommand(string);
    }

    public List<String> tabComplete(CommandSender commandSender, String string) {
        if (this.isOurCommand(string)) {
            return super.tabComplete(commandSender, string);
        }
        return this.proxied.tabComplete(commandSender, string);
    }
}

