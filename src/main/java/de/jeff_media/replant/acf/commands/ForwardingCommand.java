package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandOperationContext;
import de.jeff_media.replant.acf.commands.CommandRouter;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ForwardingCommand
extends BaseCommand {
    private final BaseCommand command;
    private final String[] baseArgs;
    private final RegisteredCommand regCommand;

    ForwardingCommand(BaseCommand baseCommand, RegisteredCommand registeredCommand, String[] stringArray) {
        this.regCommand = registeredCommand;
        this.commandName = baseCommand.commandName;
        this.command = baseCommand;
        this.baseArgs = stringArray;
        this.manager = baseCommand.manager;
        this.subCommands.put((Object)"__default", (Object)registeredCommand);
    }

    @Override
    public List<RegisteredCommand> getRegisteredCommands() {
        return Collections.singletonList(this.regCommand);
    }

    @Override
    public CommandOperationContext getLastCommandOperationContext() {
        return this.command.getLastCommandOperationContext();
    }

    @Override
    public Set<String> getRequiredPermissions() {
        return this.command.getRequiredPermissions();
    }

    @Override
    public boolean hasPermission(Object object) {
        return this.command.hasPermission(object);
    }

    @Override
    public boolean requiresPermission(String string) {
        return this.command.requiresPermission(string);
    }

    @Override
    public boolean hasPermission(CommandIssuer commandIssuer) {
        return this.command.hasPermission(commandIssuer);
    }

    @Override
    public List<String> tabComplete(CommandIssuer commandIssuer, RootCommand rootCommand, String[] stringArray, boolean bl) {
        return this.command.tabComplete(commandIssuer, rootCommand, stringArray, bl);
    }

    @Override
    public void execute(CommandIssuer commandIssuer, CommandRouter.CommandRouteResult commandRouteResult) {
        commandRouteResult = new CommandRouter.CommandRouteResult(this.regCommand, commandRouteResult.args, ACFUtil.join(this.baseArgs), commandRouteResult.commandLabel);
        this.command.execute(commandIssuer, commandRouteResult);
    }

    BaseCommand getCommand() {
        return this.command;
    }
}

