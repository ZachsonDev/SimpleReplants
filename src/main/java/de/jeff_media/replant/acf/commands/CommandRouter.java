package de.jeff_media.replant.acf.commands;

import com.google.common.collect.SetMultimap;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import de.jeff_media.replant.acf.commands.apachecommonslang.ApacheCommonsLangUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

class CommandRouter {
    private final CommandManager manager;

    CommandRouter(CommandManager commandManager) {
        this.manager = commandManager;
    }

    CommandRouteResult matchCommand(RouteSearch routeSearch, boolean bl) {
        Set<RegisteredCommand> set = routeSearch.commands;
        String[] stringArray = routeSearch.args;
        if (!set.isEmpty()) {
            if (set.size() == 1) {
                return new CommandRouteResult(ACFUtil.getFirstElement(set), routeSearch);
            }
            Optional<RegisteredCommand> optional = set.stream().filter(registeredCommand -> this.isProbableMatch((RegisteredCommand)registeredCommand, stringArray, bl)).min((registeredCommand, registeredCommand2) -> {
                int n = registeredCommand.consumeInputResolvers;
                int n2 = registeredCommand2.consumeInputResolvers;
                if (n == n2) {
                    return 0;
                }
                return n < n2 ? 1 : -1;
            });
            if (optional.isPresent()) {
                return new CommandRouteResult(optional.get(), routeSearch);
            }
        }
        return null;
    }

    private boolean isProbableMatch(RegisteredCommand registeredCommand, String[] stringArray, boolean bl) {
        int n = registeredCommand.requiredResolvers;
        int n2 = registeredCommand.optionalResolvers;
        return stringArray.length <= n + n2 && (bl || stringArray.length >= n);
    }

    RouteSearch routeCommand(RootCommand rootCommand, String string, String[] stringArray, boolean bl) {
        HashSet<RegisteredCommand> hashSet;
        Object object;
        int n;
        SetMultimap<String, RegisteredCommand> setMultimap = rootCommand.getSubCommands();
        for (int i = n = stringArray.length; i >= 0; --i) {
            object = ApacheCommonsLangUtil.join((Object[])stringArray, " ", 0, i).toLowerCase(Locale.ENGLISH);
            hashSet = setMultimap.get(object);
            if (hashSet.isEmpty()) continue;
            return new RouteSearch(hashSet, Arrays.copyOfRange(stringArray, i, n), string, (String)object, bl);
        }
        Set set = setMultimap.get((Object)"__default");
        object = setMultimap.get((Object)"__catchunknown");
        if (!set.isEmpty()) {
            hashSet = new HashSet<RegisteredCommand>();
            for (RegisteredCommand registeredCommand : set) {
                CommandParameter commandParameter;
                int n2 = registeredCommand.requiredResolvers;
                int n3 = registeredCommand.optionalResolvers;
                CommandParameter commandParameter2 = commandParameter = registeredCommand.parameters.length > 0 ? registeredCommand.parameters[registeredCommand.parameters.length - 1] : null;
                if (n > n2 + n3 && (commandParameter == null || commandParameter.getType() != String[].class && (n < n2 || !commandParameter.consumesRest))) continue;
                hashSet.add(registeredCommand);
            }
            if (!hashSet.isEmpty()) {
                return new RouteSearch(hashSet, stringArray, string, null, bl);
            }
        }
        if (!object.isEmpty()) {
            return new RouteSearch((Set<RegisteredCommand>)object, stringArray, string, null, bl);
        }
        return null;
    }

    static class RouteSearch {
        final String[] args;
        final Set<RegisteredCommand> commands;
        final String commandLabel;
        final String subcommand;

        RouteSearch(Set<RegisteredCommand> set, String[] stringArray, String string, String string2, boolean bl) {
            this.commands = set;
            this.args = stringArray;
            this.commandLabel = string.toLowerCase(Locale.ENGLISH);
            this.subcommand = string2;
        }
    }

    static class CommandRouteResult {
        final RegisteredCommand cmd;
        final String[] args;
        final String commandLabel;
        final String subcommand;

        CommandRouteResult(RegisteredCommand registeredCommand, RouteSearch routeSearch) {
            this(registeredCommand, routeSearch.args, routeSearch.subcommand, routeSearch.commandLabel);
        }

        CommandRouteResult(RegisteredCommand registeredCommand, String[] stringArray, String string, String string2) {
            this.cmd = registeredCommand;
            this.args = stringArray;
            this.commandLabel = string2;
            this.subcommand = string;
        }
    }
}

