package de.jeff_media.replant.updatechecker;

import de.jeff_media.replant.updatechecker.UpdateCheckEvent;
import de.jeff_media.replant.updatechecker.UpdateCheckResult;
import de.jeff_media.replant.updatechecker.UpdateCheckSuccess;
import de.jeff_media.replant.updatechecker.UpdateChecker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

class Messages {
    Messages() {
    }

    @NotNull
    private static TextComponent createLink(@NotNull String string, @NotNull String string2) {
        ComponentBuilder componentBuilder = new ComponentBuilder("Link: ").bold(true).append(string2).bold(false);
        TextComponent textComponent = new TextComponent(string);
        textComponent.setBold(Boolean.valueOf(true));
        textComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, string2));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
        return textComponent;
    }

    protected static void printCheckResultToConsole(UpdateCheckEvent updateCheckEvent) {
        UpdateChecker updateChecker = UpdateChecker.getInstance();
        Plugin plugin = updateChecker.getPlugin();
        if (updateCheckEvent.getSuccess() == UpdateCheckSuccess.FAIL || updateCheckEvent.getResult() == UpdateCheckResult.UNKNOWN) {
            plugin.getLogger().warning("Could not check for updates.");
            return;
        }
        if (updateCheckEvent.getResult() == UpdateCheckResult.RUNNING_LATEST_VERSION) {
            if (UpdateChecker.getInstance().isSuppressUpToDateMessage()) {
                return;
            }
            plugin.getLogger().info(String.format("You are using the latest version of %s.", plugin.getName()));
            return;
        }
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(String.format("There is a new version of %s available!", plugin.getName()));
        arrayList.add(" ");
        arrayList.add(String.format("Your version:   %s%s", updateChecker.isColoredConsoleOutput() ? ChatColor.RED : "", updateCheckEvent.getUsedVersion()));
        arrayList.add(String.format("Latest version: %s%s", updateChecker.isColoredConsoleOutput() ? ChatColor.GREEN : "", updateCheckEvent.getLatestVersion()));
        List<String> list = updateChecker.getAppropriateDownloadLinks();
        if (list.size() > 0) {
            arrayList.add(" ");
            arrayList.add("Please update to the newest version.");
            arrayList.add(" ");
            if (list.size() == 1) {
                arrayList.add("Download:");
                arrayList.add("  " + list.get(0));
            } else if (list.size() == 2) {
                arrayList.add("Download (Plus):");
                arrayList.add("  " + list.get(0));
                arrayList.add(" ");
                arrayList.add("Download (Free):");
                arrayList.add("  " + list.get(1));
            }
        }
        Messages.printNiceBoxToConsole(plugin.getLogger(), arrayList);
    }

    protected static void printCheckResultToPlayer(Player player, boolean bl) {
        UpdateChecker updateChecker = UpdateChecker.getInstance();
        if (updateChecker.getLastCheckResult() == UpdateCheckResult.NEW_VERSION_AVAILABLE) {
            player.sendMessage(ChatColor.GRAY + "There is a new version of " + ChatColor.GOLD + updateChecker.getPlugin().getName() + ChatColor.GRAY + " available.");
            Messages.sendLinks(player);
            player.sendMessage(ChatColor.DARK_GRAY + "Latest version: " + ChatColor.GREEN + updateChecker.getLatestVersion() + ChatColor.DARK_GRAY + " | Your version: " + ChatColor.RED + updateChecker.getUsedVersion());
            player.sendMessage("");
        } else if (updateChecker.getLastCheckResult() == UpdateCheckResult.UNKNOWN) {
            player.sendMessage(ChatColor.GOLD + updateChecker.getPlugin().getName() + ChatColor.RED + " could not check for updates.");
        } else if (bl) {
            player.sendMessage(ChatColor.GREEN + "You are running the latest version of " + ChatColor.GOLD + updateChecker.getPlugin().getName());
        }
    }

    private static void printNiceBoxToConsole(Logger logger, List<String> list) {
        int n = 0;
        for (String object : list) {
            n = Math.max(object.length(), n);
        }
        if ((n += 2) > 120) {
            n = 120;
        }
        StringBuilder stringBuilder = new StringBuilder(n += 2);
        Stream.generate(() -> "*").limit(n).forEach(stringBuilder::append);
        logger.log(Level.WARNING, stringBuilder.toString());
        for (String string : list) {
            logger.log(Level.WARNING, "* " + string);
        }
        logger.log(Level.WARNING, stringBuilder.toString());
    }

    private static void sendLinks(Player ... textComponent) {
        UpdateChecker updateChecker = UpdateChecker.getInstance();
        ArrayList<TextComponent> arrayList = new ArrayList<TextComponent>();
        List<String> list = updateChecker.getAppropriateDownloadLinks();
        if (list.size() == 2) {
            arrayList.add(Messages.createLink(String.format("Download (%s)", updateChecker.getNamePaidVersion()), list.get(0)));
            arrayList.add(Messages.createLink(String.format("Download (%s)", updateChecker.getNameFreeVersion()), list.get(1)));
        } else if (list.size() == 1) {
            arrayList.add(Messages.createLink("Download", list.get(0)));
        }
        if (updateChecker.getDonationLink() != null) {
            arrayList.add(Messages.createLink("Donate", updateChecker.getDonationLink()));
        }
        if (updateChecker.getChangelogLink() != null) {
            arrayList.add(Messages.createLink("Changelog", updateChecker.getChangelogLink()));
        }
        TextComponent textComponent2 = new TextComponent(" | ");
        textComponent2.setColor(net.md_5.bungee.api.ChatColor.GRAY);
        TextComponent textComponent3 = new TextComponent("");
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            TextComponent textComponent4 = (TextComponent)iterator.next();
            textComponent3.addExtra((BaseComponent)textComponent4);
            if (!iterator.hasNext()) continue;
            textComponent3.addExtra((BaseComponent)textComponent2);
        }
        for (TextComponent textComponent5 : textComponent) {
            textComponent5.spigot().sendMessage((BaseComponent)textComponent3);
        }
    }
}

