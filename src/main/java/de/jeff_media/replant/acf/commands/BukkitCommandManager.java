package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFBukkitHelpTopic;
import de.jeff_media.replant.acf.commands.ACFBukkitListener;
import de.jeff_media.replant.acf.commands.ACFBukkitScheduler;
import de.jeff_media.replant.acf.commands.ACFBukkitUtil;
import de.jeff_media.replant.acf.commands.ACFPaperScheduler;
import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.BukkitCommandCompletionContext;
import de.jeff_media.replant.acf.commands.BukkitCommandCompletions;
import de.jeff_media.replant.acf.commands.BukkitCommandContexts;
import de.jeff_media.replant.acf.commands.BukkitCommandExecutionContext;
import de.jeff_media.replant.acf.commands.BukkitCommandIssuer;
import de.jeff_media.replant.acf.commands.BukkitConditionContext;
import de.jeff_media.replant.acf.commands.BukkitLocales;
import de.jeff_media.replant.acf.commands.BukkitMessageFormatter;
import de.jeff_media.replant.acf.commands.BukkitRegisteredCommand;
import de.jeff_media.replant.acf.commands.BukkitRootCommand;
import de.jeff_media.replant.acf.commands.CommandCompletions;
import de.jeff_media.replant.acf.commands.CommandContexts;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.commands.MessageType;
import de.jeff_media.replant.acf.commands.ProxyCommandMap;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import de.jeff_media.replant.acf.commands.apachecommonslang.ApacheCommonsExceptionUtil;
import de.jeff_media.replant.acf.commands.lib.timings.TimingManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

public class BukkitCommandManager
extends CommandManager<CommandSender, BukkitCommandIssuer, ChatColor, BukkitMessageFormatter, BukkitCommandExecutionContext, BukkitConditionContext> {
    protected final Plugin plugin;
    private final CommandMap commandMap;
    @Deprecated
    private final TimingManager timingManager;
    protected ACFBukkitScheduler scheduler;
    private final Logger logger;
    public final Integer mcMinorVersion;
    public final Integer mcPatchVersion;
    protected Map<String, Command> knownCommands = new HashMap<String, Command>();
    protected Map<String, BukkitRootCommand> registeredCommands = new HashMap<String, BukkitRootCommand>();
    protected BukkitCommandContexts contexts;
    protected BukkitCommandCompletions completions;
    protected BukkitLocales locales;
    protected Map<UUID, String> issuersLocaleString = new ConcurrentHashMap<UUID, String>();
    private boolean cantReadLocale = false;
    protected boolean autoDetectFromClient = true;

    public BukkitCommandManager(Plugin plugin) {
        this.plugin = plugin;
        try {
            this.scheduler = new ACFPaperScheduler(Bukkit.getAsyncScheduler());
        }
        catch (NoSuchMethodError noSuchMethodError) {
            this.scheduler = new ACFBukkitScheduler();
        }
        String string = this.plugin.getDescription().getPrefix();
        this.logger = Logger.getLogger(string != null ? string : this.plugin.getName());
        this.timingManager = TimingManager.of(plugin);
        this.commandMap = this.hookCommandMap();
        this.defaultFormatter = new BukkitMessageFormatter(ChatColor.RED, ChatColor.YELLOW, ChatColor.RED);
        this.formatters.put(MessageType.ERROR, this.defaultFormatter);
        this.formatters.put(MessageType.SYNTAX, new BukkitMessageFormatter(ChatColor.YELLOW, ChatColor.GREEN, ChatColor.WHITE));
        this.formatters.put(MessageType.INFO, new BukkitMessageFormatter(ChatColor.BLUE, ChatColor.DARK_GREEN, ChatColor.GREEN));
        this.formatters.put(MessageType.HELP, new BukkitMessageFormatter(ChatColor.AQUA, ChatColor.GREEN, ChatColor.YELLOW));
        Pattern pattern = Pattern.compile("\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?\\)");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());
        if (matcher.find()) {
            this.mcMinorVersion = ACFUtil.parseInt(matcher.toMatchResult().group(2), 0);
            this.mcPatchVersion = ACFUtil.parseInt(matcher.toMatchResult().group(3), 0);
        } else {
            this.mcMinorVersion = -1;
            this.mcPatchVersion = -1;
        }
        Bukkit.getHelpMap().registerHelpTopicFactory(BukkitRootCommand.class, command -> {
            if (this.hasUnstableAPI("help")) {
                return new ACFBukkitHelpTopic(this, (BukkitRootCommand)command);
            }
            return new GenericCommandHelpTopic(command);
        });
        Bukkit.getPluginManager().registerEvents((Listener)new ACFBukkitListener(this, plugin), plugin);
        this.getLocales();
        this.scheduler.createLocaleTask(plugin, () -> {
            if (this.cantReadLocale || !this.autoDetectFromClient) {
                return;
            }
            Bukkit.getOnlinePlayers().forEach(this::readPlayerLocale);
        }, 30L, 30L);
        this.validNamePredicate = ACFBukkitUtil::isValidName;
        this.registerDependency(plugin.getClass(), plugin);
        this.registerDependency(Logger.class, plugin.getLogger());
        this.registerDependency(FileConfiguration.class, plugin.getConfig());
        this.registerDependency(FileConfiguration.class, "config", plugin.getConfig());
        this.registerDependency(Plugin.class, plugin);
        this.registerDependency(JavaPlugin.class, plugin);
        this.registerDependency(PluginManager.class, Bukkit.getPluginManager());
        this.registerDependency(Server.class, Bukkit.getServer());
        this.scheduler.registerSchedulerDependencies(this);
        this.registerDependency(ScoreboardManager.class, Bukkit.getScoreboardManager());
        this.registerDependency(ItemFactory.class, Bukkit.getItemFactory());
        this.registerDependency(PluginDescriptionFile.class, plugin.getDescription());
    }

    @NotNull
    private CommandMap hookCommandMap() {
        Object object = null;
        try {
            Field field;
            Server server = Bukkit.getServer();
            Method method = server.getClass().getDeclaredMethod("getCommandMap", new Class[0]);
            method.setAccessible(true);
            object = (CommandMap)method.invoke((Object)server, new Object[0]);
            if (!SimpleCommandMap.class.isAssignableFrom(object.getClass())) {
                this.log(LogLevel.ERROR, "ERROR: CommandMap has been hijacked! Offending command map is located at: " + object.getClass().getName());
                this.log(LogLevel.ERROR, "We are going to try to hijack it back and resolve this, but you are now in dangerous territory.");
                this.log(LogLevel.ERROR, "We can not guarantee things are going to work.");
                field = server.getClass().getDeclaredField("commandMap");
                object = new ProxyCommandMap(this, (CommandMap)object);
                field.set(server, object);
                this.log(LogLevel.INFO, "Injected Proxy Command Map... good luck...");
            }
            field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            this.knownCommands = (Map)field.get(object);
        }
        catch (Exception exception) {
            this.log(LogLevel.ERROR, "Failed to get Command Map. ACF will not function.");
            ACFUtil.sneaky(exception);
        }
        return object;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean isCommandIssuer(Class<?> clazz) {
        return CommandSender.class.isAssignableFrom(clazz);
    }

    @Override
    public synchronized CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new BukkitCommandContexts(this);
        }
        return this.contexts;
    }

    @Override
    public synchronized CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new BukkitCommandCompletions(this);
        }
        return this.completions;
    }

    @Override
    public BukkitLocales getLocales() {
        if (this.locales == null) {
            this.locales = new BukkitLocales(this);
            this.locales.loadLanguages();
        }
        return this.locales;
    }

    @Override
    public boolean hasRegisteredCommands() {
        return !this.registeredCommands.isEmpty();
    }

    public void registerCommand(BaseCommand baseCommand, boolean bl) {
        String string = this.plugin.getName().toLowerCase(Locale.ENGLISH);
        baseCommand.onRegister(this);
        for (Map.Entry<String, RootCommand> entry : baseCommand.registeredCommands.entrySet()) {
            String string2 = entry.getKey().toLowerCase(Locale.ENGLISH);
            BukkitRootCommand bukkitRootCommand = (BukkitRootCommand)entry.getValue();
            if (!bukkitRootCommand.isRegistered) {
                Command command = this.commandMap.getCommand(string2);
                if (command instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand)command).getPlugin() == this.plugin) {
                    this.knownCommands.remove(string2);
                    command.unregister(this.commandMap);
                } else if (command != null && bl) {
                    this.knownCommands.remove(string2);
                    for (Map.Entry<String, Command> entry2 : this.knownCommands.entrySet()) {
                        String[] stringArray;
                        String string3 = entry2.getKey();
                        Command command2 = entry2.getValue();
                        if (!string3.contains(":") || !command.equals(command2) || (stringArray = ACFPatterns.COLON.split(string3, 2)).length <= 1) continue;
                        command.unregister(this.commandMap);
                        command.setLabel(stringArray[0] + ":" + baseCommand.getName());
                        command.register(this.commandMap);
                    }
                }
                this.commandMap.register(string2, string, (Command)bukkitRootCommand);
            }
            bukkitRootCommand.isRegistered = true;
            this.registeredCommands.put(string2, bukkitRootCommand);
        }
    }

    @Override
    public void registerCommand(BaseCommand baseCommand) {
        this.registerCommand(baseCommand, false);
    }

    public void unregisterCommand(BaseCommand baseCommand) {
        for (RootCommand rootCommand : baseCommand.registeredCommands.values()) {
            BukkitRootCommand bukkitRootCommand = (BukkitRootCommand)rootCommand;
            bukkitRootCommand.getSubCommands().values().removeAll(baseCommand.subCommands.values());
            if (!bukkitRootCommand.isRegistered || !bukkitRootCommand.getSubCommands().isEmpty()) continue;
            this.unregisterCommand(bukkitRootCommand);
            bukkitRootCommand.isRegistered = false;
        }
    }

    @Deprecated
    public void unregisterCommand(BukkitRootCommand bukkitRootCommand) {
        String string = this.plugin.getName().toLowerCase(Locale.ENGLISH);
        bukkitRootCommand.unregister(this.commandMap);
        String string2 = bukkitRootCommand.getName();
        Command command = this.knownCommands.get(string2);
        if (bukkitRootCommand.equals(command)) {
            this.knownCommands.remove(string2);
        }
        this.knownCommands.remove(string + ":" + string2);
        this.registeredCommands.remove(string2);
    }

    public void unregisterCommands() {
        for (String string : new HashSet<String>(this.registeredCommands.keySet())) {
            this.unregisterCommand(this.registeredCommands.get(string));
        }
    }

    private Field getEntityField(Player player) {
        for (Class<?> clazz = player.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (!clazz.getName().endsWith("CraftEntity")) continue;
            Field field = clazz.getDeclaredField("entity");
            field.setAccessible(true);
            return field;
        }
        return null;
    }

    public Locale setPlayerLocale(Player player, Locale locale) {
        return this.setIssuerLocale(player, locale);
    }

    void readPlayerLocale(Player player) {
        if (!player.isOnline() || this.cantReadLocale) {
            return;
        }
        try {
            Object object;
            Locale locale;
            block10: {
                locale = null;
                try {
                    locale = player.locale();
                }
                catch (NoSuchMethodError noSuchMethodError) {
                    block9: {
                        object = null;
                        try {
                            object = player.getLocale();
                        }
                        catch (NoSuchMethodError noSuchMethodError2) {
                            Object object2;
                            Field field = this.getEntityField(player);
                            if (field == null || (object2 = field.get(player)) == null) break block9;
                            Field field2 = object2.getClass().getDeclaredField("locale");
                            field2.setAccessible(true);
                            object = field2.get(object2);
                        }
                    }
                    if (!(object instanceof String) || object.equals(this.issuersLocaleString.get(player.getUniqueId()))) break block10;
                    String[] stringArray = ACFPatterns.UNDERSCORE.split((String)object);
                    Locale locale2 = locale = stringArray.length > 1 ? new Locale(stringArray[0], stringArray[1]) : new Locale(stringArray[0]);
                }
            }
            if (locale != null) {
                UUID uUID = player.getUniqueId();
                object = this.issuersLocale.put(uUID, locale);
                this.issuersLocaleString.put(uUID, locale.toString());
                if (!Objects.equals(locale, object)) {
                    this.notifyLocaleChange(this.getCommandIssuer(player), (Locale)object, locale);
                }
            }
        }
        catch (Exception exception) {
            this.cantReadLocale = true;
            this.scheduler.cancelLocaleTask();
            this.log(LogLevel.INFO, "Can't read players locale, you will be unable to automatically detect players language. Only Bukkit 1.7+ is supported for this.", exception);
        }
    }

    @Deprecated
    public TimingManager getTimings() {
        return this.timingManager;
    }

    public ACFBukkitScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public RootCommand createRootCommand(String string) {
        return new BukkitRootCommand(this, string);
    }

    @Override
    public Collection<RootCommand> getRegisteredRootCommands() {
        return Collections.unmodifiableCollection(this.registeredCommands.values());
    }

    @Override
    public BukkitCommandIssuer getCommandIssuer(Object object) {
        if (!(object instanceof CommandSender)) {
            throw new IllegalArgumentException(object.getClass().getName() + " is not a Command Issuer.");
        }
        return new BukkitCommandIssuer(this, (CommandSender)object);
    }

    @Override
    public BukkitCommandExecutionContext createCommandContext(RegisteredCommand registeredCommand, CommandParameter commandParameter, CommandIssuer commandIssuer, List<String> list, int n, Map<String, Object> map) {
        return new BukkitCommandExecutionContext(registeredCommand, commandParameter, (BukkitCommandIssuer)commandIssuer, list, n, map);
    }

    @Override
    public BukkitCommandCompletionContext createCompletionContext(RegisteredCommand registeredCommand, CommandIssuer commandIssuer, String string, String string2, String[] stringArray) {
        return new BukkitCommandCompletionContext(registeredCommand, (BukkitCommandIssuer)commandIssuer, string, string2, stringArray);
    }

    @Override
    public RegisteredCommand createRegisteredCommand(BaseCommand baseCommand, String string, Method method, String string2) {
        return new BukkitRegisteredCommand(baseCommand, string, method, string2);
    }

    @Override
    public BukkitConditionContext createConditionContext(CommandIssuer commandIssuer, String string) {
        return new BukkitConditionContext((BukkitCommandIssuer)commandIssuer, string);
    }

    @Override
    public void log(LogLevel logLevel, String string, Throwable throwable) {
        Level level = logLevel == LogLevel.INFO ? Level.INFO : Level.SEVERE;
        this.logger.log(level, "[ACF] " + string);
        if (throwable != null) {
            for (String string2 : ACFPatterns.NEWLINE.split(ApacheCommonsExceptionUtil.getFullStackTrace(throwable))) {
                this.logger.log(level, "[ACF] " + string2);
            }
        }
    }

    public boolean usePerIssuerLocale(boolean bl, boolean bl2) {
        boolean bl3 = this.usePerIssuerLocale;
        this.usePerIssuerLocale = bl;
        this.autoDetectFromClient = bl2;
        return bl3;
    }

    @Override
    public String getCommandPrefix(CommandIssuer commandIssuer) {
        return commandIssuer.isPlayer() ? "/" : "";
    }

    @Override
    protected boolean handleUncaughtException(BaseCommand baseCommand, RegisteredCommand registeredCommand, CommandIssuer commandIssuer, List<String> list, Throwable throwable) {
        if (throwable instanceof CommandException && throwable.getCause() != null && throwable.getMessage().startsWith("Unhandled exception")) {
            throwable = throwable.getCause();
        }
        return super.handleUncaughtException(baseCommand, registeredCommand, commandIssuer, list, throwable);
    }
}

