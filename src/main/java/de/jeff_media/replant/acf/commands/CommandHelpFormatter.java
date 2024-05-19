package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandHelp;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.HelpEntry;
import de.jeff_media.replant.acf.commands.MessageKeys;
import de.jeff_media.replant.acf.commands.MessageType;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class CommandHelpFormatter {
    private final CommandManager manager;

    public CommandHelpFormatter(CommandManager commandManager) {
        this.manager = commandManager;
    }

    public void showAllResults(CommandHelp commandHelp, List<HelpEntry> list) {
        CommandIssuer commandIssuer = commandHelp.getIssuer();
        this.printHelpHeader(commandHelp, commandIssuer);
        for (HelpEntry helpEntry : list) {
            this.printHelpCommand(commandHelp, commandIssuer, helpEntry);
        }
        this.printHelpFooter(commandHelp, commandIssuer);
    }

    public void showSearchResults(CommandHelp commandHelp, List<HelpEntry> list) {
        CommandIssuer commandIssuer = commandHelp.getIssuer();
        this.printSearchHeader(commandHelp, commandIssuer);
        for (HelpEntry helpEntry : list) {
            this.printSearchEntry(commandHelp, commandIssuer, helpEntry);
        }
        this.printSearchFooter(commandHelp, commandIssuer);
    }

    public void showDetailedHelp(CommandHelp commandHelp, HelpEntry helpEntry) {
        CommandIssuer commandIssuer = commandHelp.getIssuer();
        this.printDetailedHelpCommand(commandHelp, commandIssuer, helpEntry);
        for (CommandParameter commandParameter : helpEntry.getParameters()) {
            String string = commandParameter.getDescription();
            if (string == null || string.isEmpty()) continue;
            this.printDetailedParameter(commandHelp, commandIssuer, helpEntry, commandParameter);
        }
    }

    public void printHelpHeader(CommandHelp commandHelp, CommandIssuer commandIssuer) {
        commandIssuer.sendMessage(MessageType.HELP, MessageKeys.HELP_HEADER, this.getHeaderFooterFormatReplacements(commandHelp));
    }

    public void printHelpCommand(CommandHelp commandHelp, CommandIssuer commandIssuer, HelpEntry helpEntry) {
        String string = this.manager.formatMessage(commandIssuer, MessageType.HELP, MessageKeys.HELP_FORMAT, this.getEntryFormatReplacements(commandHelp, helpEntry));
        for (String string2 : ACFPatterns.NEWLINE.split(string)) {
            commandIssuer.sendMessageInternal(ACFUtil.rtrim(string2));
        }
    }

    public void printHelpFooter(CommandHelp commandHelp, CommandIssuer commandIssuer) {
        if (commandHelp.isOnlyPage()) {
            return;
        }
        commandIssuer.sendMessage(MessageType.HELP, MessageKeys.HELP_PAGE_INFORMATION, this.getHeaderFooterFormatReplacements(commandHelp));
    }

    public void printSearchHeader(CommandHelp commandHelp, CommandIssuer commandIssuer) {
        commandIssuer.sendMessage(MessageType.HELP, MessageKeys.HELP_SEARCH_HEADER, this.getHeaderFooterFormatReplacements(commandHelp));
    }

    public void printSearchEntry(CommandHelp commandHelp, CommandIssuer commandIssuer, HelpEntry helpEntry) {
        String string = this.manager.formatMessage(commandIssuer, MessageType.HELP, MessageKeys.HELP_FORMAT, this.getEntryFormatReplacements(commandHelp, helpEntry));
        for (String string2 : ACFPatterns.NEWLINE.split(string)) {
            commandIssuer.sendMessageInternal(ACFUtil.rtrim(string2));
        }
    }

    public void printSearchFooter(CommandHelp commandHelp, CommandIssuer commandIssuer) {
        if (commandHelp.isOnlyPage()) {
            return;
        }
        commandIssuer.sendMessage(MessageType.HELP, MessageKeys.HELP_PAGE_INFORMATION, this.getHeaderFooterFormatReplacements(commandHelp));
    }

    public void printDetailedHelpHeader(CommandHelp commandHelp, CommandIssuer commandIssuer, HelpEntry helpEntry) {
        commandIssuer.sendMessage(MessageType.HELP, MessageKeys.HELP_DETAILED_HEADER, "{command}", helpEntry.getCommand(), "{commandprefix}", commandHelp.getCommandPrefix());
    }

    public void printDetailedHelpCommand(CommandHelp commandHelp, CommandIssuer commandIssuer, HelpEntry helpEntry) {
        String string = this.manager.formatMessage(commandIssuer, MessageType.HELP, MessageKeys.HELP_DETAILED_COMMAND_FORMAT, this.getEntryFormatReplacements(commandHelp, helpEntry));
        for (String string2 : ACFPatterns.NEWLINE.split(string)) {
            commandIssuer.sendMessageInternal(ACFUtil.rtrim(string2));
        }
    }

    public void printDetailedParameter(CommandHelp commandHelp, CommandIssuer commandIssuer, HelpEntry helpEntry, CommandParameter commandParameter) {
        String string = this.manager.formatMessage(commandIssuer, MessageType.HELP, MessageKeys.HELP_DETAILED_PARAMETER_FORMAT, this.getParameterFormatReplacements(commandHelp, commandParameter, helpEntry));
        for (String string2 : ACFPatterns.NEWLINE.split(string)) {
            commandIssuer.sendMessageInternal(ACFUtil.rtrim(string2));
        }
    }

    public void printDetailedHelpFooter(CommandHelp commandHelp, CommandIssuer commandIssuer, HelpEntry helpEntry) {
    }

    public String[] getHeaderFooterFormatReplacements(CommandHelp commandHelp) {
        return new String[]{"{search}", commandHelp.search != null ? String.join((CharSequence)" ", commandHelp.search) : "", "{command}", commandHelp.getCommandName(), "{commandprefix}", commandHelp.getCommandPrefix(), "{rootcommand}", commandHelp.getCommandName(), "{page}", "" + commandHelp.getPage(), "{totalpages}", "" + commandHelp.getTotalPages(), "{results}", "" + commandHelp.getTotalResults()};
    }

    public String[] getEntryFormatReplacements(CommandHelp commandHelp, HelpEntry helpEntry) {
        return new String[]{"{command}", helpEntry.getCommand(), "{commandprefix}", commandHelp.getCommandPrefix(), "{parameters}", helpEntry.getParameterSyntax(commandHelp.getIssuer()), "{separator}", helpEntry.getDescription().isEmpty() ? "" : "-", "{description}", helpEntry.getDescription()};
    }

    @NotNull
    public String[] getParameterFormatReplacements(CommandHelp commandHelp, CommandParameter commandParameter, HelpEntry helpEntry) {
        return new String[]{"{name}", commandParameter.getDisplayName(commandHelp.getIssuer()), "{syntaxorname}", (String)ACFUtil.nullDefault(commandParameter.getSyntax(commandHelp.getIssuer()), commandParameter.getDisplayName(commandHelp.getIssuer())), "{syntax}", (String)ACFUtil.nullDefault(commandParameter.getSyntax(commandHelp.getIssuer()), ""), "{description}", (String)ACFUtil.nullDefault(commandParameter.getDescription(), ""), "{command}", commandHelp.getCommandName(), "{fullcommand}", helpEntry.getCommand(), "{commandprefix}", commandHelp.getCommandPrefix()};
    }
}

