package de.jeff_media.replant.acf.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public class BukkitRootCommand
extends Command
implements RootCommand,
PluginIdentifiableCommand {
    private final BukkitCommandManager manager;
    private final String name;
    private BaseCommand defCommand;
    private SetMultimap<String, RegisteredCommand> subCommands = HashMultimap.create();
    private List<BaseCommand> children = new ArrayList<BaseCommand>();
    boolean isRegistered = false;

    BukkitRootCommand(BukkitCommandManager bukkitCommandManager, String string) {
        super(string);
        this.manager = bukkitCommandManager;
        this.name = string;
    }

    @Override
    public String getDescription() {
        RegisteredCommand registeredCommand = this.getDefaultRegisteredCommand();
        String string = null;
        if (registeredCommand != null && !registeredCommand.getHelpText().isEmpty()) {
            string = registeredCommand.getHelpText();
        } else if (registeredCommand != null && registeredCommand.scope.description != null) {
            string = registeredCommand.scope.description;
        } else if (this.defCommand.description != null) {
            string = this.defCommand.description;
        }
        if (string != null) {
            return this.manager.getLocales().replaceI18NStrings(string);
        }
        return super.getDescription();
    }

    @Override
    public String getCommandName() {
        return this.name;
    }

    public List<String> tabComplete(CommandSender commandSender, String string, String[] stringArray) {
        if (string.contains(":")) {
            string = ACFPatterns.COLON.split(string, 2)[1];
        }
        return this.getTabCompletions(this.manager.getCommandIssuer(commandSender), string, stringArray);
    }

    public boolean execute(CommandSender commandSender, String string, String[] stringArray) {
        if (string.contains(":")) {
            string = ACFPatterns.COLON.split(string, 2)[1];
        }
        this.execute(this.manager.getCommandIssuer(commandSender), string, stringArray);
        return true;
    }

    public boolean testPermissionSilent(CommandSender commandSender) {
        return this.hasAnyPermission(this.manager.getCommandIssuer(commandSender));
    }

    @Override
    public void addChild(BaseCommand baseCommand) {
        if (this.defCommand == null || !baseCommand.subCommands.get((Object)"__default").isEmpty()) {
            this.defCommand = baseCommand;
        }
        this.addChildShared(this.children, this.subCommands, baseCommand);
        this.setPermission(this.getUniquePermission());
    }

    @Override
    public CommandManager getManager() {
        return this.manager;
    }

    @Override
    public SetMultimap<String, RegisteredCommand> getSubCommands() {
        return this.subCommands;
    }

    @Override
    public List<BaseCommand> getChildren() {
        return this.children;
    }

    @Override
    public BaseCommand getDefCommand() {
        return this.defCommand;
    }

    public Plugin getPlugin() {
        return this.manager.getPlugin();
    }
}

