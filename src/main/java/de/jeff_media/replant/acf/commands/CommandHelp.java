package de.jeff_media.replant.acf.commands;

import com.google.common.collect.SetMultimap;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandHelpFormatter;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.HelpEntry;
import de.jeff_media.replant.acf.commands.MessageKeys;
import de.jeff_media.replant.acf.commands.MessageType;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import de.jeff_media.replant.acf.commands.UnstableAPI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandHelp {
    private final CommandManager manager;
    private final CommandIssuer issuer;
    private final List<HelpEntry> helpEntries = new ArrayList<HelpEntry>();
    private final String commandName;
    final String commandPrefix;
    private int page = 1;
    private int perPage;
    List<String> search;
    private Set<HelpEntry> selectedEntry = new HashSet<HelpEntry>();
    private int totalResults;
    private int totalPages;
    private boolean lastPage;

    public CommandHelp(CommandManager commandManager, RootCommand rootCommand, CommandIssuer commandIssuer) {
        RegisteredCommand registeredCommand;
        this.manager = commandManager;
        this.issuer = commandIssuer;
        this.perPage = commandManager.defaultHelpPerPage;
        this.commandPrefix = commandManager.getCommandPrefix(commandIssuer);
        this.commandName = rootCommand.getCommandName();
        SetMultimap<String, RegisteredCommand> setMultimap = rootCommand.getSubCommands();
        HashSet<RegisteredCommand> hashSet = new HashSet<RegisteredCommand>();
        if (!rootCommand.getDefCommand().hasHelpCommand && (registeredCommand = rootCommand.getDefaultRegisteredCommand()) != null) {
            this.helpEntries.add(new HelpEntry(this, registeredCommand));
            hashSet.add(registeredCommand);
        }
        setMultimap.entries().forEach(entry -> {
            String string = (String)entry.getKey();
            if (string.equals("__default") || string.equals("__catchunknown")) {
                return;
            }
            RegisteredCommand registeredCommand = (RegisteredCommand)entry.getValue();
            if (!registeredCommand.isPrivate && registeredCommand.hasPermission(commandIssuer) && !hashSet.contains(registeredCommand)) {
                this.helpEntries.add(new HelpEntry(this, registeredCommand));
                hashSet.add(registeredCommand);
            }
        });
    }

    @UnstableAPI
    protected void updateSearchScore(HelpEntry helpEntry) {
        if (this.search == null || this.search.isEmpty()) {
            helpEntry.setSearchScore(1);
            return;
        }
        RegisteredCommand registeredCommand = helpEntry.getRegisteredCommand();
        int n = 0;
        for (String string : this.search) {
            Pattern pattern = Pattern.compile(".*" + Pattern.quote(string) + ".*", 2);
            for (String string2 : registeredCommand.registeredSubcommands) {
                Pattern pattern2 = Pattern.compile(".*" + Pattern.quote(string2) + ".*", 2);
                if (pattern.matcher(string2).matches()) {
                    n += 3;
                    continue;
                }
                if (!pattern2.matcher(string).matches()) continue;
                ++n;
            }
            if (pattern.matcher(helpEntry.getDescription()).matches()) {
                n += 2;
            }
            if (pattern.matcher(helpEntry.getParameterSyntax(this.issuer)).matches()) {
                ++n;
            }
            if (helpEntry.getSearchTags() == null || !pattern.matcher(helpEntry.getSearchTags()).matches()) continue;
            n += 2;
        }
        helpEntry.setSearchScore(n);
    }

    public CommandManager getManager() {
        return this.manager;
    }

    public boolean testExactMatch(String string) {
        this.selectedEntry.clear();
        for (HelpEntry helpEntry : this.helpEntries) {
            if (!helpEntry.getCommand().endsWith(" " + string)) continue;
            this.selectedEntry.add(helpEntry);
        }
        return !this.selectedEntry.isEmpty();
    }

    public void showHelp() {
        this.showHelp(this.issuer);
    }

    public void showHelp(CommandIssuer commandIssuer) {
        CommandHelpFormatter commandHelpFormatter = this.manager.getHelpFormatter();
        if (!this.selectedEntry.isEmpty()) {
            HelpEntry helpEntry2 = ACFUtil.getFirstElement(this.selectedEntry);
            commandHelpFormatter.printDetailedHelpHeader(this, commandIssuer, helpEntry2);
            for (HelpEntry helpEntry3 : this.selectedEntry) {
                commandHelpFormatter.showDetailedHelp(this, helpEntry3);
            }
            commandHelpFormatter.printDetailedHelpFooter(this, commandIssuer, helpEntry2);
            return;
        }
        List<Object> list = this.getHelpEntries().stream().filter(HelpEntry::shouldShow).collect(Collectors.toList());
        Iterator<Object> iterator = list.stream().sorted(Comparator.comparingInt(helpEntry -> helpEntry.getSearchScore() * -1)).iterator();
        if (!iterator.hasNext()) {
            commandIssuer.sendMessage(MessageType.ERROR, MessageKeys.NO_COMMAND_MATCHED_SEARCH, "{search}", ACFUtil.join(this.search, " "));
            list = this.getHelpEntries();
            iterator = list.iterator();
        }
        this.totalResults = list.size();
        int n = (this.page - 1) * this.perPage;
        int n2 = n + this.perPage;
        this.totalPages = (int)Math.ceil((float)this.totalResults / (float)this.perPage);
        int n3 = 0;
        if (n >= this.totalResults) {
            commandIssuer.sendMessage(MessageType.HELP, MessageKeys.HELP_NO_RESULTS, new String[0]);
            return;
        }
        ArrayList<HelpEntry> arrayList = new ArrayList<HelpEntry>();
        while (iterator.hasNext()) {
            HelpEntry helpEntry4 = (HelpEntry)iterator.next();
            if (n3 >= n2) break;
            if (n3++ < n) continue;
            arrayList.add(helpEntry4);
        }
        boolean bl = this.lastPage = n2 >= this.totalResults;
        if (this.search == null) {
            commandHelpFormatter.showAllResults(this, arrayList);
        } else {
            commandHelpFormatter.showSearchResults(this, arrayList);
        }
    }

    public List<HelpEntry> getHelpEntries() {
        return this.helpEntries;
    }

    public void setPerPage(int n) {
        this.perPage = n;
    }

    public void setPage(int n) {
        this.page = n;
    }

    public void setPage(int n, int n2) {
        this.setPage(n);
        this.setPerPage(n2);
    }

    public void setSearch(List<String> list) {
        this.search = list;
        this.getHelpEntries().forEach(this::updateSearchScore);
    }

    public CommandIssuer getIssuer() {
        return this.issuer;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public int getPage() {
        return this.page;
    }

    public int getPerPage() {
        return this.perPage;
    }

    public List<String> getSearch() {
        return this.search;
    }

    public Set<HelpEntry> getSelectedEntry() {
        return this.selectedEntry;
    }

    public int getTotalResults() {
        return this.totalResults;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public boolean isOnlyPage() {
        return this.page == 1 && this.lastPage;
    }

    public boolean isLastPage() {
        return this.lastPage;
    }
}

