package de.jeff_media.replant.acf.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.Annotations;
import de.jeff_media.replant.acf.commands.CommandHelp;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandOperationContext;
import de.jeff_media.replant.acf.commands.CommandRouter;
import de.jeff_media.replant.acf.commands.ExceptionHandler;
import de.jeff_media.replant.acf.commands.ForwardingCommand;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.commands.MessageKeys;
import de.jeff_media.replant.acf.commands.MessageType;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import de.jeff_media.replant.acf.commands.UnstableAPI;
import de.jeff_media.replant.acf.commands.annotation.CatchAll;
import de.jeff_media.replant.acf.commands.annotation.CatchUnknown;
import de.jeff_media.replant.acf.commands.annotation.CommandAlias;
import de.jeff_media.replant.acf.commands.annotation.CommandPermission;
import de.jeff_media.replant.acf.commands.annotation.Conditions;
import de.jeff_media.replant.acf.commands.annotation.Default;
import de.jeff_media.replant.acf.commands.annotation.Description;
import de.jeff_media.replant.acf.commands.annotation.HelpCommand;
import de.jeff_media.replant.acf.commands.annotation.PreCommand;
import de.jeff_media.replant.acf.commands.annotation.Subcommand;
import de.jeff_media.replant.acf.commands.annotation.UnknownHandler;
import de.jeff_media.replant.acf.commands.apachecommonslang.ApacheCommonsLangUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public abstract class BaseCommand {
    static final String CATCHUNKNOWN = "__catchunknown";
    static final String DEFAULT = "__default";
    final SetMultimap<String, RegisteredCommand> subCommands = HashMultimap.create();
    final Set<BaseCommand> subScopes = new HashSet<BaseCommand>();
    final Map<Class<?>, String> contextFlags = new HashMap();
    @Nullable
    private Method preCommandHandler;
    private String execLabel;
    private String execSubcommand;
    private String[] origArgs;
    CommandManager<?, ?, ?, ?, ?, ?> manager = null;
    BaseCommand parentCommand;
    Map<String, RootCommand> registeredCommands = new HashMap<String, RootCommand>();
    @Nullable
    String description;
    @Nullable
    String commandName;
    @Nullable
    String permission;
    @Nullable
    String conditions;
    boolean hasHelpCommand;
    private ExceptionHandler exceptionHandler = null;
    private final ThreadLocal<CommandOperationContext> lastCommandOperationContext = new ThreadLocal();
    @Nullable
    private String parentSubcommand;
    private final Set<String> permissions = new HashSet<String>();

    public BaseCommand() {
    }

    @Deprecated
    public BaseCommand(@Nullable String string) {
        this.commandName = string;
    }

    public CommandOperationContext getLastCommandOperationContext() {
        return this.lastCommandOperationContext.get();
    }

    public String getExecCommandLabel() {
        return this.execLabel;
    }

    public String getExecSubcommand() {
        return this.execSubcommand;
    }

    public String[] getOrigArgs() {
        return this.origArgs;
    }

    void onRegister(CommandManager commandManager) {
        this.onRegister(commandManager, this.commandName);
    }

    private void onRegister(CommandManager commandManager, String string) {
        commandManager.injectDependencies(this);
        this.manager = commandManager;
        Annotations annotations = commandManager.getAnnotations();
        Class<?> clazz = this.getClass();
        String[] stringArray = annotations.getAnnotationValues(clazz, CommandAlias.class, 11);
        if (string == null && stringArray != null) {
            string = stringArray[0];
        }
        this.commandName = string != null ? string : clazz.getSimpleName().toLowerCase(Locale.ENGLISH);
        this.permission = annotations.getAnnotationValue(clazz, CommandPermission.class, 1);
        this.description = annotations.getAnnotationValue(clazz, Description.class, 9);
        this.parentSubcommand = this.getParentSubcommand(clazz);
        this.conditions = annotations.getAnnotationValue(clazz, Conditions.class, 9);
        this.computePermissions();
        this.registerSubcommands();
        this.registerSubclasses(string);
        if (stringArray != null) {
            HashSet hashSet = new HashSet();
            Collections.addAll(hashSet, stringArray);
            hashSet.remove(string);
            for (String string2 : hashSet) {
                this.register(string2, this);
            }
        }
        if (string != null) {
            this.register(string, this);
        }
    }

    private void registerSubclasses(String string) {
        for (Class<?> clazz : this.getClass().getDeclaredClasses()) {
            if (!BaseCommand.class.isAssignableFrom(clazz)) continue;
            try {
                Constructor<?>[] constructorArray;
                BaseCommand baseCommand = null;
                for (Constructor<?> constructor : constructorArray = clazz.getDeclaredConstructors()) {
                    constructor.setAccessible(true);
                    Parameter[] parameterArray = constructor.getParameters();
                    if (parameterArray.length == 1) {
                        baseCommand = (BaseCommand)constructor.newInstance(this);
                        continue;
                    }
                    this.manager.log(LogLevel.INFO, "Found unusable constructor: " + constructor.getName() + "(" + Stream.of(parameterArray).map(parameter -> parameter.getType().getSimpleName() + " " + parameter.getName()).collect(Collectors.joining("<c2>,</c2> ")) + ")");
                }
                if (baseCommand != null) {
                    baseCommand.parentCommand = this;
                    this.subScopes.add(baseCommand);
                    super.onRegister(this.manager, string);
                    this.subCommands.putAll(baseCommand.subCommands);
                    this.registeredCommands.putAll(baseCommand.registeredCommands);
                    continue;
                }
                this.manager.log(LogLevel.ERROR, "Could not find a subcommand ctor for " + clazz.getName());
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException reflectiveOperationException) {
                this.manager.log(LogLevel.ERROR, "Error registering subclass", reflectiveOperationException);
            }
        }
    }

    private void registerSubcommands() {
        Annotations annotations = this.manager.getAnnotations();
        boolean bl = false;
        boolean bl2 = this.parentSubcommand == null || this.parentSubcommand.isEmpty();
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Collections.addAll(linkedHashSet, this.getClass().getDeclaredMethods());
        Collections.addAll(linkedHashSet, this.getClass().getMethods());
        for (Method method : linkedHashSet) {
            boolean bl3;
            method.setAccessible(true);
            String string = null;
            String string2 = this.getSubcommandValue(method);
            String string3 = annotations.getAnnotationValue(method, HelpCommand.class, 0);
            String string4 = annotations.getAnnotationValue(method, CommandAlias.class, 0);
            if (annotations.hasAnnotation(method, Default.class)) {
                if (!bl2) {
                    string2 = this.parentSubcommand;
                } else {
                    this.registerSubcommand(method, DEFAULT);
                }
            }
            if (string2 != null) {
                string = string2;
            } else if (string4 != null) {
                string = string4;
            } else if (string3 != null) {
                string = string3;
                this.hasHelpCommand = true;
            }
            boolean bl4 = annotations.hasAnnotation(method, PreCommand.class);
            boolean bl5 = bl3 = annotations.hasAnnotation(method, CatchUnknown.class) || annotations.hasAnnotation(method, CatchAll.class) || annotations.hasAnnotation(method, UnknownHandler.class);
            if (bl3 || !bl && string3 != null) {
                if (!bl) {
                    if (bl3) {
                        this.subCommands.get((Object)CATCHUNKNOWN).clear();
                        bl = true;
                    }
                    this.registerSubcommand(method, CATCHUNKNOWN);
                } else {
                    ACFUtil.sneaky(new IllegalStateException("Multiple @CatchUnknown/@HelpCommand commands, duplicate on " + method.getDeclaringClass().getName() + "#" + method.getName()));
                }
            } else if (bl4) {
                if (this.preCommandHandler == null) {
                    this.preCommandHandler = method;
                } else {
                    ACFUtil.sneaky(new IllegalStateException("Multiple @PreCommand commands, duplicate on " + method.getDeclaringClass().getName() + "#" + method.getName()));
                }
            }
            if (!Objects.equals(method.getDeclaringClass(), this.getClass()) || string == null) continue;
            this.registerSubcommand(method, string);
        }
    }

    private void computePermissions() {
        this.permissions.clear();
        if (this.permission != null && !this.permission.isEmpty()) {
            this.permissions.addAll(Arrays.asList(ACFPatterns.COMMA.split(this.permission)));
        }
        if (this.parentCommand != null) {
            this.permissions.addAll(this.parentCommand.getRequiredPermissions());
        }
        this.subCommands.values().forEach(RegisteredCommand::computePermissions);
        this.subScopes.forEach(BaseCommand::computePermissions);
    }

    private String getSubcommandValue(Method method) {
        String string = this.manager.getAnnotations().getAnnotationValue(method, Subcommand.class, 0);
        if (string == null) {
            return null;
        }
        Class<?> clazz = method.getDeclaringClass();
        String string2 = this.getParentSubcommand(clazz);
        return string2 == null || string2.isEmpty() ? string : string2 + " " + string;
    }

    private String getParentSubcommand(Class<?> clazz) {
        ArrayList<String> arrayList = new ArrayList<String>();
        while (clazz != null) {
            String string = this.manager.getAnnotations().getAnnotationValue(clazz, Subcommand.class, 0);
            if (string != null) {
                arrayList.add(string);
            }
            clazz = clazz.getEnclosingClass();
        }
        Collections.reverse(arrayList);
        return ACFUtil.join(arrayList, " ");
    }

    private void register(String string, BaseCommand baseCommand) {
        String string2 = string.toLowerCase(Locale.ENGLISH);
        RootCommand rootCommand = this.manager.obtainRootCommand(string2);
        rootCommand.addChild(baseCommand);
        this.registeredCommands.put(string2, rootCommand);
    }

    private void registerSubcommand(Method method, String string) {
        String[] stringArray;
        string = this.manager.getCommandReplacements().replace(string.toLowerCase(Locale.ENGLISH));
        Object[] objectArray = ACFPatterns.SPACE.split(string);
        Set<String> set = BaseCommand.getSubCommandPossibilityList((String[])objectArray);
        for (int i = 0; i < objectArray.length; ++i) {
            stringArray = ACFPatterns.PIPE.split((CharSequence)objectArray[i]);
            if (stringArray.length == 0 || stringArray[0].isEmpty()) {
                throw new IllegalArgumentException("Invalid @Subcommand configuration for " + method.getName() + " - parts can not start with | or be empty");
            }
            objectArray[i] = stringArray[0];
        }
        String string2 = ApacheCommonsLangUtil.join(objectArray, " ");
        stringArray = this.manager.getAnnotations().getAnnotationValues((AnnotatedElement)method, CommandAlias.class, 3);
        String string3 = stringArray != null ? stringArray[0] : this.commandName + " ";
        RegisteredCommand registeredCommand = this.manager.createRegisteredCommand(this, string3, method, string2);
        for (String string4 : set) {
            this.subCommands.put((Object)string4, (Object)registeredCommand);
        }
        registeredCommand.addSubcommands(set);
        if (stringArray != null) {
            for (String string5 : stringArray) {
                this.register(string5, new ForwardingCommand(this, registeredCommand, (String[])objectArray));
            }
        }
    }

    private static Set<String> getSubCommandPossibilityList(String[] stringArray) {
        HashSet<String> hashSet;
        int n = 0;
        HashSet<String> hashSet2 = null;
        while (true) {
            hashSet = new HashSet<String>();
            if (n < stringArray.length) {
                for (String string : ACFPatterns.PIPE.split(stringArray[n])) {
                    if (hashSet2 != null) {
                        hashSet.addAll(hashSet2.stream().map(string2 -> string2 + " " + string).collect(Collectors.toList()));
                        continue;
                    }
                    hashSet.add(string);
                }
            }
            if (n + 1 >= stringArray.length) break;
            hashSet2 = hashSet;
            ++n;
        }
        return hashSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void execute(CommandIssuer commandIssuer, CommandRouter.CommandRouteResult commandRouteResult) {
        try {
            CommandOperationContext commandOperationContext = this.preCommandOperation(commandIssuer, commandRouteResult.commandLabel, commandRouteResult.args, false);
            this.execSubcommand = commandRouteResult.subcommand;
            this.executeCommand(commandOperationContext, commandIssuer, commandRouteResult.args, commandRouteResult.cmd);
        }
        finally {
            this.postCommandOperation();
        }
    }

    private void postCommandOperation() {
        CommandManager.commandOperationContext.get().pop();
        this.lastCommandOperationContext.set(null);
        this.execSubcommand = null;
        this.execLabel = null;
        this.origArgs = new String[0];
    }

    private CommandOperationContext preCommandOperation(CommandIssuer commandIssuer, String string, String[] stringArray, boolean bl) {
        Stack<CommandOperationContext> stack = CommandManager.commandOperationContext.get();
        CommandOperationContext<?> commandOperationContext = this.manager.createCommandOperationContext(this, commandIssuer, string, stringArray, bl);
        stack.push(commandOperationContext);
        this.lastCommandOperationContext.set(commandOperationContext);
        this.execSubcommand = null;
        this.execLabel = string;
        this.origArgs = stringArray;
        return commandOperationContext;
    }

    public CommandIssuer getCurrentCommandIssuer() {
        return CommandManager.getCurrentCommandIssuer();
    }

    public CommandManager getCurrentCommandManager() {
        return CommandManager.getCurrentCommandManager();
    }

    private void executeCommand(CommandOperationContext commandOperationContext, CommandIssuer commandIssuer, String[] stringArray, RegisteredCommand registeredCommand) {
        if (registeredCommand.hasPermission(commandIssuer)) {
            commandOperationContext.setRegisteredCommand(registeredCommand);
            if (this.checkPrecommand(commandOperationContext, registeredCommand, commandIssuer, stringArray)) {
                return;
            }
            List<String> list = Arrays.asList(stringArray);
            registeredCommand.invoke(commandIssuer, list, commandOperationContext);
        } else {
            commandIssuer.sendMessage(MessageType.ERROR, MessageKeys.PERMISSION_DENIED, new String[0]);
        }
    }

    @Deprecated
    public boolean canExecute(CommandIssuer commandIssuer, RegisteredCommand<?> registeredCommand) {
        return true;
    }

    public List<String> tabComplete(CommandIssuer commandIssuer, String string, String[] stringArray) {
        return this.tabComplete(commandIssuer, string, stringArray, false);
    }

    public List<String> tabComplete(CommandIssuer commandIssuer, String string, String[] stringArray, boolean bl) {
        return this.tabComplete(commandIssuer, this.manager.getRootCommand(string.toLowerCase(Locale.ENGLISH)), stringArray, bl);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    List<String> tabComplete(CommandIssuer commandIssuer, RootCommand rootCommand, String[] stringArray, boolean bl) {
        if (stringArray.length == 0) {
            stringArray = new String[]{""};
        }
        String string = rootCommand.getCommandName();
        try {
            CommandRouter commandRouter = this.manager.getRouter();
            this.preCommandOperation(commandIssuer, string, stringArray, bl);
            CommandRouter.RouteSearch routeSearch = commandRouter.routeCommand(rootCommand, string, stringArray, true);
            ArrayList<String> arrayList = new ArrayList<String>();
            if (routeSearch != null) {
                for (RegisteredCommand registeredCommand : routeSearch.commands) {
                    arrayList.addAll(this.completeCommand(commandIssuer, registeredCommand, routeSearch.args, string, bl));
                }
            }
            List<String> list = BaseCommand.filterTabComplete(stringArray[stringArray.length - 1], arrayList);
            return list;
        }
        finally {
            this.postCommandOperation();
        }
    }

    List<String> getCommandsForCompletion(CommandIssuer commandIssuer, String[] stringArray) {
        HashSet<String> hashSet = new HashSet<String>();
        int n = Math.max(0, stringArray.length - 1);
        String string = ApacheCommonsLangUtil.join((Object[])stringArray, " ").toLowerCase(Locale.ENGLISH);
        for (Map.Entry entry : this.subCommands.entries()) {
            RegisteredCommand registeredCommand;
            String string2 = (String)entry.getKey();
            if (!string2.startsWith(string) || BaseCommand.isSpecialSubcommand(string2) || !(registeredCommand = (RegisteredCommand)entry.getValue()).hasPermission(commandIssuer) || registeredCommand.isPrivate) continue;
            String[] stringArray2 = ACFPatterns.SPACE.split(registeredCommand.prefSubCommand);
            hashSet.add(stringArray2[n]);
        }
        return new ArrayList<String>(hashSet);
    }

    static boolean isSpecialSubcommand(String string) {
        return CATCHUNKNOWN.equals(string) || DEFAULT.equals(string);
    }

    private List<String> completeCommand(CommandIssuer commandIssuer, RegisteredCommand registeredCommand, String[] stringArray, String string, boolean bl) {
        if (!registeredCommand.hasPermission(commandIssuer) || stringArray.length == 0 || registeredCommand.parameters.length == 0) {
            return Collections.emptyList();
        }
        if (!registeredCommand.parameters[registeredCommand.parameters.length - 1].consumesRest && stringArray.length > registeredCommand.consumeInputResolvers) {
            return Collections.emptyList();
        }
        List<String> list = this.manager.getCommandCompletions().of(registeredCommand, commandIssuer, stringArray, bl);
        return BaseCommand.filterTabComplete(stringArray[stringArray.length - 1], list);
    }

    private static List<String> filterTabComplete(String string, List<String> list) {
        return list.stream().distinct().filter(string2 -> string2 != null && (string.isEmpty() || ApacheCommonsLangUtil.startsWithIgnoreCase(string2, string))).collect(Collectors.toList());
    }

    private boolean checkPrecommand(CommandOperationContext commandOperationContext, RegisteredCommand registeredCommand, CommandIssuer commandIssuer, String[] stringArray) {
        Method method = this.preCommandHandler;
        if (method != null) {
            try {
                Class<?>[] classArray = method.getParameterTypes();
                Object[] objectArray = new Object[method.getParameterCount()];
                for (int i = 0; i < objectArray.length; ++i) {
                    Class<?> clazz = classArray[i];
                    Object t = commandIssuer.getIssuer();
                    objectArray[i] = this.manager.isCommandIssuer(clazz) && clazz.isAssignableFrom(t.getClass()) ? t : (CommandIssuer.class.isAssignableFrom(clazz) ? commandIssuer : (RegisteredCommand.class.isAssignableFrom(clazz) ? registeredCommand : (String[].class.isAssignableFrom(clazz) ? stringArray : null)));
                }
                return (Boolean)method.invoke((Object)this, objectArray);
            }
            catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
                this.manager.log(LogLevel.ERROR, "Exception encountered while command pre-processing", reflectiveOperationException);
            }
        }
        return false;
    }

    @Deprecated
    @UnstableAPI
    public CommandHelp getCommandHelp() {
        return this.manager.generateCommandHelp();
    }

    @Deprecated
    @UnstableAPI
    public void showCommandHelp() {
        this.getCommandHelp().showHelp();
    }

    public void help(Object object, String[] stringArray) {
        this.help((CommandIssuer)this.manager.getCommandIssuer(object), stringArray);
    }

    public void help(CommandIssuer commandIssuer, String[] stringArray) {
        commandIssuer.sendMessage(MessageType.ERROR, MessageKeys.UNKNOWN_COMMAND, new String[0]);
    }

    public void doHelp(Object object, String ... stringArray) {
        this.doHelp((CommandIssuer)this.manager.getCommandIssuer(object), stringArray);
    }

    public void doHelp(CommandIssuer commandIssuer, String ... stringArray) {
        this.help(commandIssuer, stringArray);
    }

    public void showSyntax(CommandIssuer commandIssuer, RegisteredCommand<?> registeredCommand) {
        commandIssuer.sendMessage(MessageType.SYNTAX, MessageKeys.INVALID_SYNTAX, "{command}", this.manager.getCommandPrefix(commandIssuer) + registeredCommand.command, "{syntax}", registeredCommand.getSyntaxText(commandIssuer));
    }

    public boolean hasPermission(Object object) {
        return this.hasPermission((CommandIssuer)this.manager.getCommandIssuer(object));
    }

    public boolean hasPermission(CommandIssuer commandIssuer) {
        return this.manager.hasPermission(commandIssuer, this.getRequiredPermissions());
    }

    public Set<String> getRequiredPermissions() {
        return this.permissions;
    }

    public boolean requiresPermission(String string) {
        return this.getRequiredPermissions().contains(string);
    }

    public String getName() {
        return this.commandName;
    }

    public ExceptionHandler getExceptionHandler() {
        return this.exceptionHandler;
    }

    public BaseCommand setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public RegisteredCommand getDefaultRegisteredCommand() {
        return (RegisteredCommand)ACFUtil.getFirstElement(this.subCommands.get((Object)DEFAULT));
    }

    public String setContextFlags(Class<?> clazz, String string) {
        return this.contextFlags.put(clazz, string);
    }

    public String getContextFlags(Class<?> clazz) {
        return this.contextFlags.get(clazz);
    }

    public List<RegisteredCommand> getRegisteredCommands() {
        ArrayList<RegisteredCommand> arrayList = new ArrayList<RegisteredCommand>();
        arrayList.addAll(this.subCommands.values());
        return arrayList;
    }

    protected SetMultimap<String, RegisteredCommand> getSubCommands() {
        return this.subCommands;
    }
}

