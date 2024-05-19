package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.Annotations;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.CommandCompletions;
import de.jeff_media.replant.acf.commands.CommandExecutionContext;
import de.jeff_media.replant.acf.commands.CommandHelp;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandOperationContext;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.InvalidCommandArgument;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.commands.MessageKeys;
import de.jeff_media.replant.acf.commands.MessageType;
import de.jeff_media.replant.acf.commands.ShowCommandHelp;
import de.jeff_media.replant.acf.commands.annotation.CommandAlias;
import de.jeff_media.replant.acf.commands.annotation.CommandCompletion;
import de.jeff_media.replant.acf.commands.annotation.CommandPermission;
import de.jeff_media.replant.acf.commands.annotation.Conditions;
import de.jeff_media.replant.acf.commands.annotation.Description;
import de.jeff_media.replant.acf.commands.annotation.HelpSearchTags;
import de.jeff_media.replant.acf.commands.annotation.Private;
import de.jeff_media.replant.acf.commands.annotation.Syntax;
import de.jeff_media.replant.acf.commands.contexts.ContextResolver;
import de.jeff_media.replant.acf.locales.MessageKeyProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class RegisteredCommand<CEC extends CommandExecutionContext<CEC, ? extends CommandIssuer>> {
    final BaseCommand scope;
    final Method method;
    final CommandParameter<CEC>[] parameters;
    final CommandManager manager;
    final List<String> registeredSubcommands = new ArrayList<String>();
    String command;
    String prefSubCommand;
    String syntaxText;
    String helpText;
    String permission;
    String complete;
    String conditions;
    public String helpSearchTags;
    boolean isPrivate;
    final int requiredResolvers;
    final int consumeInputResolvers;
    final int doesNotConsumeInputResolvers;
    final int optionalResolvers;
    final Set<String> permissions = new HashSet<String>();

    RegisteredCommand(BaseCommand baseCommand, String string, Method method, String string2) {
        this.scope = baseCommand;
        this.manager = this.scope.manager;
        Annotations annotations = this.manager.getAnnotations();
        if (BaseCommand.isSpecialSubcommand(string2)) {
            string2 = "";
            string = string.trim();
        }
        this.command = string + (!annotations.hasAnnotation(method, CommandAlias.class, false) && !string2.isEmpty() ? string2 : "");
        this.method = method;
        this.prefSubCommand = string2;
        this.permission = annotations.getAnnotationValue(method, CommandPermission.class, 9);
        this.complete = annotations.getAnnotationValue(method, CommandCompletion.class, 17);
        this.helpText = annotations.getAnnotationValue(method, Description.class, 17);
        this.conditions = annotations.getAnnotationValue(method, Conditions.class, 9);
        this.helpSearchTags = annotations.getAnnotationValue(method, HelpSearchTags.class, 9);
        this.syntaxText = annotations.getAnnotationValue(method, Syntax.class, 1);
        Parameter[] parameterArray = method.getParameters();
        this.parameters = new CommandParameter[parameterArray.length];
        this.isPrivate = annotations.hasAnnotation(method, Private.class) || annotations.getAnnotationFromClass(baseCommand.getClass(), Private.class) != null;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        CommandParameter commandParameter = null;
        for (int i = 0; i < parameterArray.length; ++i) {
            this.parameters[i] = new CommandParameter(this, parameterArray[i], i, i == parameterArray.length - 1);
            CommandParameter commandParameter2 = this.parameters[i];
            if (commandParameter != null) {
                commandParameter.setNextParam(commandParameter2);
            }
            commandParameter = commandParameter2;
            if (commandParameter2.isCommandIssuer()) continue;
            if (!commandParameter2.requiresInput()) {
                ++n4;
            } else {
                ++n;
            }
            if (commandParameter2.canConsumeInput()) {
                ++n2;
                continue;
            }
            ++n3;
        }
        this.requiredResolvers = n;
        this.consumeInputResolvers = n2;
        this.doesNotConsumeInputResolvers = n3;
        this.optionalResolvers = n4;
        this.computePermissions();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void invoke(CommandIssuer commandIssuer, List<String> list, CommandOperationContext commandOperationContext) {
        if (!this.scope.canExecute(commandIssuer, this)) {
            return;
        }
        this.preCommand();
        try {
            this.manager.getCommandConditions().validateConditions(commandOperationContext);
            Map<String, Object> map = this.resolveContexts(commandIssuer, list);
            if (map == null) {
                return;
            }
            Object object = this.method.invoke((Object)this.scope, map.values().toArray());
            if (object instanceof CompletionStage) {
                CompletionStage completionStage = (CompletionStage)object;
                completionStage.exceptionally(throwable -> {
                    this.handleException(commandIssuer, list, (Throwable)throwable);
                    return null;
                });
            }
        }
        catch (Exception exception) {
            this.handleException(commandIssuer, list, exception);
        }
        finally {
            this.postCommand();
        }
    }

    public void preCommand() {
    }

    public void postCommand() {
    }

    void handleException(CommandIssuer commandIssuer, List<String> list, Throwable throwable) {
        while (throwable instanceof ExecutionException || throwable instanceof CompletionException || throwable instanceof InvocationTargetException) {
            throwable = throwable.getCause();
        }
        if (throwable instanceof ShowCommandHelp) {
            ShowCommandHelp showCommandHelp = (ShowCommandHelp)throwable;
            CommandHelp commandHelp = this.manager.generateCommandHelp();
            if (showCommandHelp.search) {
                commandHelp.setSearch(showCommandHelp.searchArgs == null ? list : showCommandHelp.searchArgs);
            }
            commandHelp.showHelp(commandIssuer);
        } else if (throwable instanceof InvalidCommandArgument) {
            InvalidCommandArgument invalidCommandArgument = (InvalidCommandArgument)throwable;
            if (invalidCommandArgument.key != null) {
                commandIssuer.sendMessage(MessageType.ERROR, invalidCommandArgument.key, invalidCommandArgument.replacements);
            } else if (throwable.getMessage() != null && !throwable.getMessage().isEmpty()) {
                commandIssuer.sendMessage(MessageType.ERROR, MessageKeys.ERROR_PREFIX, "{message}", throwable.getMessage());
            }
            if (invalidCommandArgument.showSyntax) {
                this.scope.showSyntax(commandIssuer, this);
            }
        } else {
            try {
                boolean bl;
                if (!this.manager.handleUncaughtException(this.scope, this, commandIssuer, list, throwable)) {
                    commandIssuer.sendMessage(MessageType.ERROR, MessageKeys.ERROR_PERFORMING_COMMAND, new String[0]);
                }
                boolean bl2 = bl = this.manager.defaultExceptionHandler != null || this.scope.getExceptionHandler() != null;
                if (!bl || this.manager.logUnhandledExceptions) {
                    this.manager.log(LogLevel.ERROR, "Exception in command: " + this.command + " " + ACFUtil.join(list), throwable);
                }
            }
            catch (Exception exception) {
                this.manager.log(LogLevel.ERROR, "Exception in handleException for command: " + this.command + " " + ACFUtil.join(list), throwable);
                this.manager.log(LogLevel.ERROR, "Exception triggered by exception handler:", exception);
            }
        }
    }

    @Nullable
    Map<String, Object> resolveContexts(CommandIssuer commandIssuer, List<String> list) {
        return this.resolveContexts(commandIssuer, list, null);
    }

    @Nullable
    Map<String, Object> resolveContexts(CommandIssuer commandIssuer, List<String> list, String string) {
        list = new ArrayList<String>(list);
        String[] stringArray = list.toArray(new String[list.size()]);
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
        int n = this.requiredResolvers;
        CommandOperationContext commandOperationContext = CommandManager.getCurrentCommandOperationContext();
        for (int i = 0; !(i >= this.parameters.length || string != null && linkedHashMap.containsKey(string)); ++i) {
            Object object;
            boolean bl = i == this.parameters.length - 1;
            boolean bl2 = n == 0;
            CommandParameter<CEC> commandParameter = this.parameters[i];
            String string2 = commandParameter.getName();
            Class<?> clazz = commandParameter.getType();
            ContextResolver<?, CEC> contextResolver = commandParameter.getResolver();
            CommandExecutionContext commandExecutionContext = this.manager.createCommandContext(this, commandParameter, commandIssuer, list, i, linkedHashMap);
            boolean bl3 = commandParameter.requiresInput();
            if (bl3 && n > 0) {
                --n;
            }
            Set<String> set = commandParameter.getRequiredPermissions();
            if (list.isEmpty() && (!bl || clazz != String[].class)) {
                if (bl2 && commandParameter.getDefaultValue() != null) {
                    list.add(commandParameter.getDefaultValue());
                } else {
                    if (bl2 && commandParameter.isOptional()) {
                        object = !commandParameter.isOptionalResolver() || !this.manager.hasPermission(commandIssuer, set) ? null : contextResolver.getContext(commandExecutionContext);
                        if (object == null && commandParameter.getClass().isPrimitive()) {
                            throw new IllegalStateException("Parameter " + commandParameter.getName() + " is primitive and does not support Optional.");
                        }
                        this.manager.getCommandConditions().validateConditions(commandExecutionContext, object);
                        linkedHashMap.put(string2, object);
                        continue;
                    }
                    if (bl3) {
                        this.scope.showSyntax(commandIssuer, this);
                        return null;
                    }
                }
            } else if (!this.manager.hasPermission(commandIssuer, set)) {
                commandIssuer.sendMessage(MessageType.ERROR, MessageKeys.PERMISSION_DENIED_PARAMETER, "{param}", string2);
                throw new InvalidCommandArgument(false);
            }
            if (commandParameter.getValues() != null) {
                object = !list.isEmpty() ? list.get(0) : "";
                HashSet<String> hashSet = new HashSet<String>();
                CommandCompletions<?> commandCompletions = this.manager.getCommandCompletions();
                for (String string3 : commandParameter.getValues()) {
                    List<String> list2;
                    if ("*".equals(string3) || "@completions".equals(string3)) {
                        string3 = commandCompletions.findDefaultCompletion(this, stringArray);
                    }
                    if (!(list2 = commandCompletions.getCompletionValues(this, commandIssuer, string3, stringArray, commandOperationContext.isAsync())).isEmpty()) {
                        hashSet.addAll(list2.stream().filter(Objects::nonNull).map(String::toLowerCase).collect(Collectors.toList()));
                        continue;
                    }
                    hashSet.add(string3.toLowerCase(Locale.ENGLISH));
                }
                if (!hashSet.contains(((String)object).toLowerCase(Locale.ENGLISH))) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.PLEASE_SPECIFY_ONE_OF, "{valid}", ACFUtil.join(hashSet, ", "));
                }
            }
            object = contextResolver.getContext(commandExecutionContext);
            this.manager.getCommandConditions().validateConditions(commandExecutionContext, object);
            linkedHashMap.put(string2, object);
        }
        return linkedHashMap;
    }

    boolean hasPermission(CommandIssuer commandIssuer) {
        return this.manager.hasPermission(commandIssuer, this.getRequiredPermissions());
    }

    @Deprecated
    public String getPermission() {
        if (this.permission == null || this.permission.isEmpty()) {
            return null;
        }
        return ACFPatterns.COMMA.split(this.permission)[0];
    }

    void computePermissions() {
        this.permissions.clear();
        this.permissions.addAll(this.scope.getRequiredPermissions());
        if (this.permission != null && !this.permission.isEmpty()) {
            this.permissions.addAll(Arrays.asList(ACFPatterns.COMMA.split(this.permission)));
        }
    }

    public Set<String> getRequiredPermissions() {
        return this.permissions;
    }

    public boolean requiresPermission(String string) {
        return this.getRequiredPermissions().contains(string);
    }

    public String getPrefSubCommand() {
        return this.prefSubCommand;
    }

    public String getSyntaxText() {
        return this.getSyntaxText(null);
    }

    public String getSyntaxText(CommandIssuer commandIssuer) {
        if (this.syntaxText != null) {
            return this.syntaxText;
        }
        StringBuilder stringBuilder = new StringBuilder(64);
        for (CommandParameter<CEC> commandParameter : this.parameters) {
            String string = commandParameter.getSyntax(commandIssuer);
            if (string == null) continue;
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(string);
        }
        return stringBuilder.toString().trim();
    }

    public String getHelpText() {
        return this.helpText != null ? this.helpText : "";
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public String getCommand() {
        return this.command;
    }

    public void addSubcommand(String string) {
        this.registeredSubcommands.add(string);
    }

    public void addSubcommands(Collection<String> collection) {
        this.registeredSubcommands.addAll(collection);
    }

    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return this.method.getAnnotation(clazz);
    }
}

