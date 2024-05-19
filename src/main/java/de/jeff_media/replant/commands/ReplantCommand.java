package de.jeff_media.replant.commands;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.config.Messages;
import de.jeff_media.replant.config.Permissions;
import de.jeff_media.replant.utils.CommandUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

@Deprecated
public class ReplantCommand
implements CommandExecutor,
TabCompleter {
    private static final Main main = Main.getInstance();

    public ReplantCommand() {
        main.getCommand("replant").setExecutor((CommandExecutor)this);
        main.getCommand("replant").setTabCompleter((TabCompleter)this);
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (!Permissions.isAllowed((Permissible)commandSender, "replant.use")) {
            commandSender.sendMessage(command.getPermissionMessage());
            return true;
        }
        if (stringArray.length == 0) {
            if (commandSender instanceof Player) {
                stringArray = new String[]{"crops"};
            } else {
                this.usage(commandSender, null);
                return true;
            }
        }
        if (stringArray.length == 1) {
            switch (stringArray[0].toLowerCase(Locale.ROOT)) {
                case "reload": {
                    if (!commandSender.hasPermission("replant.reload")) {
                        commandSender.sendMessage(Messages.NO_PERMISSION);
                        return true;
                    }
                    main.reload();
                    Messages.sendMessage(commandSender, Messages.CONFIG_RELOADED);
                    return true;
                }
                case "help": {
                    this.usage(commandSender, null);
                    return true;
                }
            }
        }
        if (!(commandSender instanceof Player)) {
            Messages.sendMessage(commandSender, Messages.COMMAND_PLAYERS_ONLY);
            return true;
        }
        String string2 = (Player)commandSender;
        if (stringArray.length == 1) {
            switch (stringArray[0].toLowerCase(Locale.ROOT)) {
                case "crops": {
                    main.getPlayerManager().toggleCrops((Player)string2);
                    return true;
                }
            }
        }
        this.usage(commandSender, null);
        return true;
    }

    private void usage(CommandSender commandSender, String string) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (string == null) {
            hashMap.put("crops", "Toggles auto-replanting crops");
            hashMap.put("reload", "Reloads config file");
            hashMap.put("help", "Shows this help message");
            CommandUtils.printUsage(commandSender, "/replant", hashMap);
        }
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] stringArray) {
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList("crops", "help"));
        if (commandSender.hasPermission("replant.reload")) {
            arrayList.add("reload");
        }
        if (stringArray.length < 2) {
            return arrayList;
        }
        return null;
    }
}

