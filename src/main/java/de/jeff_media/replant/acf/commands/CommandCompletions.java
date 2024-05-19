package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandCompletionContext;
import de.jeff_media.replant.acf.commands.CommandCompletionTextLookupException;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandOperationContext;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.InvalidCommandArgument;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.apachecommonslang.ApacheCommonsLangUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;

public class CommandCompletions<C extends CommandCompletionContext> {
    private static final String DEFAULT_ENUM_ID = "@__defaultenum__";
    private final CommandManager manager;
    private Map<String, CommandCompletionHandler> completionMap = new HashMap<String, CommandCompletionHandler>();
    private Map<Class, String> defaultCompletions = new HashMap<Class, String>();

    public CommandCompletions(CommandManager commandManager) {
        this.manager = commandManager;
        this.registerStaticCompletion("empty", Collections.emptyList());
        this.registerStaticCompletion("nothing", Collections.emptyList());
        this.registerStaticCompletion("timeunits", Arrays.asList("minutes", "hours", "days", "weeks", "months", "years"));
        this.registerAsyncCompletion("range", commandCompletionContext -> {
            int n;
            int n2;
            String string = commandCompletionContext.getConfig();
            if (string == null) {
                return Collections.emptyList();
            }
            String[] stringArray = ACFPatterns.DASH.split(string);
            if (stringArray.length != 2) {
                n2 = 0;
                n = ACFUtil.parseInt(stringArray[0], 0);
            } else {
                n2 = ACFUtil.parseInt(stringArray[0], 0);
                n = ACFUtil.parseInt(stringArray[1], 0);
            }
            return IntStream.rangeClosed(n2, n).mapToObj(Integer::toString).collect(Collectors.toList());
        });
    }

    public CommandCompletionHandler registerCompletion(String string, CommandCompletionHandler<C> commandCompletionHandler) {
        return this.completionMap.put(CommandCompletions.prepareCompletionId(string), commandCompletionHandler);
    }

    public CommandCompletionHandler unregisterCompletion(String string) {
        if (!this.completionMap.containsKey(string)) {
            throw new IllegalStateException("The supplied key " + string + " does not exist in any completions");
        }
        return this.completionMap.remove(string);
    }

    public CommandCompletionHandler registerAsyncCompletion(String string, AsyncCommandCompletionHandler<C> asyncCommandCompletionHandler) {
        return this.completionMap.put(CommandCompletions.prepareCompletionId(string), asyncCommandCompletionHandler);
    }

    public CommandCompletionHandler registerStaticCompletion(String string, String string2) {
        return this.registerStaticCompletion(string, ACFPatterns.PIPE.split(string2));
    }

    public CommandCompletionHandler registerStaticCompletion(String string, String[] stringArray) {
        return this.registerStaticCompletion(string, Arrays.asList(stringArray));
    }

    public CommandCompletionHandler registerStaticCompletion(String string, Supplier<Collection<String>> supplier) {
        return this.registerStaticCompletion(string, supplier.get());
    }

    public CommandCompletionHandler registerStaticCompletion(String string, Collection<String> collection) {
        return this.registerAsyncCompletion(string, commandCompletionContext -> collection);
    }

    public void setDefaultCompletion(String string, Class ... classArray) {
        CommandCompletionHandler commandCompletionHandler = this.completionMap.get(string = CommandCompletions.prepareCompletionId(string));
        if (commandCompletionHandler == null) {
            throw new IllegalStateException("Completion not registered for " + string);
        }
        for (Class clazz : classArray) {
            this.defaultCompletions.put(clazz, string);
        }
    }

    @NotNull
    private static String prepareCompletionId(String string) {
        return (string.startsWith("@") ? "" : "@") + string.toLowerCase(Locale.ENGLISH);
    }

    @NotNull
    List<String> of(RegisteredCommand registeredCommand, CommandIssuer commandIssuer, String[] stringArray, boolean bl) {
        String string;
        String[] stringArray2 = ACFPatterns.SPACE.split(registeredCommand.complete);
        int n = stringArray.length - 1;
        String string2 = stringArray[n];
        String string3 = string = n < stringArray2.length ? stringArray2[n] : null;
        if (string == null || string.isEmpty() || "*".equals(string)) {
            string = this.findDefaultCompletion(registeredCommand, stringArray);
        }
        if (string == null && stringArray2.length > 0) {
            String string4 = stringArray2[stringArray2.length - 1];
            if (string4.startsWith("repeat@")) {
                string = string4;
            } else if (n >= stringArray2.length && registeredCommand.parameters[registeredCommand.parameters.length - 1].consumesRest) {
                string = string4;
            }
        }
        if (string == null) {
            return Collections.singletonList(string2);
        }
        return this.getCompletionValues(registeredCommand, commandIssuer, string, stringArray, bl);
    }

    String findDefaultCompletion(RegisteredCommand registeredCommand, String[] stringArray) {
        int n = 0;
        for (CommandParameter commandParameter : registeredCommand.parameters) {
            Object object;
            if (!commandParameter.canConsumeInput() || ++n != stringArray.length) continue;
            for (Class<?> clazz = commandParameter.getType(); clazz != null; clazz = clazz.getSuperclass()) {
                object = this.defaultCompletions.get(clazz);
                if (object == null) continue;
                return object;
            }
            if (!commandParameter.getType().isEnum()) break;
            object = CommandManager.getCurrentCommandOperationContext();
            ((CommandOperationContext)object).enumCompletionValues = ACFUtil.enumNames(commandParameter.getType());
            return DEFAULT_ENUM_ID;
        }
        return null;
    }

    List<String> getCompletionValues(RegisteredCommand registeredCommand, CommandIssuer commandIssuer, String string, String[] stringArray, boolean bl) {
        if (DEFAULT_ENUM_ID.equals(string)) {
            CommandOperationContext commandOperationContext = CommandManager.getCurrentCommandOperationContext();
            return commandOperationContext.enumCompletionValues;
        }
        boolean bl2 = string.startsWith("repeat@");
        if (bl2) {
            string = string.substring(6);
        }
        string = this.manager.getCommandReplacements().replace(string);
        ArrayList<String> arrayList = new ArrayList<String>();
        String string3 = stringArray.length > 0 ? stringArray[stringArray.length - 1] : "";
        for (String string4 : ACFPatterns.PIPE.split(string)) {
            String[] stringArray2 = ACFPatterns.COLONEQUALS.split(string4, 2);
            CommandCompletionHandler commandCompletionHandler = this.completionMap.get(stringArray2[0].toLowerCase(Locale.ENGLISH));
            if (commandCompletionHandler != null) {
                if (bl && !(commandCompletionHandler instanceof AsyncCommandCompletionHandler)) {
                    ACFUtil.sneaky(new SyncCompletionRequired());
                    return null;
                }
                String string5 = stringArray2.length == 1 ? null : stringArray2[1];
                CommandCompletionContext commandCompletionContext = this.manager.createCompletionContext(registeredCommand, commandIssuer, string3, string5, stringArray);
                try {
                    Collection collection = commandCompletionHandler.getCompletions(commandCompletionContext);
                    if (!bl2 && collection != null && registeredCommand.parameters[registeredCommand.parameters.length - 1].consumesRest && stringArray.length > ACFPatterns.SPACE.split(registeredCommand.complete).length) {
                        String string6 = String.join((CharSequence)" ", stringArray);
                        collection = collection.stream().map(string2 -> {
                            if (string2 != null && string2.split(" ").length >= stringArray.length && ApacheCommonsLangUtil.startsWithIgnoreCase(string2, string6)) {
                                String[] stringArray2 = string2.split(" ");
                                return String.join((CharSequence)" ", Arrays.copyOfRange(stringArray2, stringArray.length - 1, stringArray2.length));
                            }
                            return string2;
                        }).collect(Collectors.toList());
                    }
                    if (collection != null) {
                        arrayList.addAll(collection);
                        continue;
                    }
                }
                catch (CommandCompletionTextLookupException commandCompletionTextLookupException) {
                }
                catch (Exception exception) {
                    registeredCommand.handleException(commandIssuer, Arrays.asList(stringArray), exception);
                }
                return Collections.singletonList(string3);
            }
            arrayList.add(string4);
        }
        return arrayList;
    }

    public static interface CommandCompletionHandler<C extends CommandCompletionContext> {
        public Collection<String> getCompletions(C var1) throws InvalidCommandArgument;
    }

    public static interface AsyncCommandCompletionHandler<C extends CommandCompletionContext>
    extends CommandCompletionHandler<C> {
    }

    public static class SyncCompletionRequired
    extends RuntimeException {
    }
}

