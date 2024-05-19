package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.BukkitCommandIssuer;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.BukkitRootCommand;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.help.GenericCommandHelpTopic;

public class ACFBukkitHelpTopic
extends GenericCommandHelpTopic {
    public ACFBukkitHelpTopic(BukkitCommandManager bukkitCommandManager, BukkitRootCommand bukkitRootCommand) {
        super((Command)bukkitRootCommand);
        final ArrayList<String> arrayList = new ArrayList<String>();
        BukkitCommandIssuer bukkitCommandIssuer = new BukkitCommandIssuer(bukkitCommandManager, (CommandSender)Bukkit.getConsoleSender()){

            @Override
            public void sendMessageInternal(String string) {
                arrayList.add(string);
            }
        };
        bukkitCommandManager.generateCommandHelp((CommandIssuer)bukkitCommandIssuer, bukkitRootCommand).showHelp(bukkitCommandIssuer);
        this.fullText = ACFUtil.join(arrayList, "\n");
    }
}

