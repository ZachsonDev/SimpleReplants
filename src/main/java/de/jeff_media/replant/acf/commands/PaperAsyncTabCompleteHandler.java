package de.jeff_media.replant.acf.commands;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.BukkitCommandIssuer;
import de.jeff_media.replant.acf.commands.CommandCompletions;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.commands.PaperCommandManager;
import de.jeff_media.replant.acf.commands.RootCommand;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

class PaperAsyncTabCompleteHandler
implements Listener {
    private final PaperCommandManager manager;

    PaperAsyncTabCompleteHandler(PaperCommandManager paperCommandManager) {
        this.manager = paperCommandManager;
        paperCommandManager.log(LogLevel.INFO, "Enabled Asynchronous Tab Completion Support!");
    }

    @EventHandler(ignoreCancelled=true)
    public void onAsyncTabComplete(AsyncTabCompleteEvent asyncTabCompleteEvent) {
        block5: {
            String string = asyncTabCompleteEvent.getBuffer();
            if (!asyncTabCompleteEvent.isCommand() && !string.startsWith("/") || string.indexOf(32) == -1) {
                return;
            }
            try {
                List<String> list = this.getCompletions(string, asyncTabCompleteEvent.getCompletions(), asyncTabCompleteEvent.getSender(), true);
                if (list != null) {
                    if (list.size() == 1 && list.get(0).equals("")) {
                        list.set(0, " ");
                    }
                    asyncTabCompleteEvent.setCompletions(list);
                    asyncTabCompleteEvent.setHandled(true);
                }
            }
            catch (Exception exception) {
                if (exception instanceof CommandCompletions.SyncCompletionRequired) break block5;
                throw exception;
            }
        }
    }

    private List<String> getCompletions(String string, List<String> list, CommandSender commandSender, boolean bl) {
        String[] stringArray;
        String[] stringArray2 = ACFPatterns.SPACE.split(string, -1);
        String string2 = PaperAsyncTabCompleteHandler.stripLeadingSlash(stringArray2[0]);
        if (stringArray2.length > 1) {
            stringArray = Arrays.copyOfRange(stringArray2, 1, stringArray2.length);
        } else {
            String[] stringArray3 = new String[1];
            stringArray = stringArray3;
            stringArray3[0] = "";
        }
        stringArray2 = stringArray;
        RootCommand rootCommand = this.manager.getRootCommand(string2);
        if (rootCommand == null) {
            return null;
        }
        BukkitCommandIssuer bukkitCommandIssuer = this.manager.getCommandIssuer(commandSender);
        List<String> list3 = rootCommand.getTabCompletions(bukkitCommandIssuer, string2, stringArray2, false, bl);
        return ACFUtil.preformOnImmutable(list, list2 -> list2.addAll(list3));
    }

    private static String stripLeadingSlash(String string) {
        return string.startsWith("/") ? string.substring(1) : string;
    }
}

