package de.jeff_media.replant.commands;

import com.allatori.annotations.StringEncryption;
import de.jeff_media.replant.Main;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.annotation.CommandAlias;
import de.jeff_media.replant.acf.commands.annotation.CommandPermission;
import de.jeff_media.replant.acf.commands.annotation.Default;
import de.jeff_media.replant.acf.commands.annotation.HelpCommand;
import de.jeff_media.replant.acf.commands.annotation.Subcommand;
import de.jeff_media.replant.config.Messages;
import de.jeff_media.replant.utils.CommandUtils;
import java.util.HashMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias(value="replant")
@StringEncryption(value="disable")
public class MainCommand
extends BaseCommand {
    private static final Main main = Main.getInstance();

    @Default
    @Subcommand(value="crops")
    @CommandPermission(value="replant.use")
    public static void onCommand(Player player, String[] stringArray) {
        main.getPlayerManager().toggleCrops(player);
    }

    @HelpCommand
    @Subcommand(value="help")
    public static void onHelp(CommandSender commandSender, String[] stringArray) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("crops", "Toggles auto-replanting crops");
        hashMap.put("reload", "Reloads config file");
        hashMap.put("help", "Shows this help message");
        CommandUtils.printUsage(commandSender, "/replant", hashMap);
    }

    @Subcommand(value="reload")
    @CommandPermission(value="replant.reload")
    public static void onReload(CommandSender commandSender, String[] stringArray) {
        main.reload();
        Messages.sendMessage(commandSender, Messages.CONFIG_RELOADED);
    }
}

