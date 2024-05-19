package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.BukkitCommandCompletionContext;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.CommandCompletions;
import de.jeff_media.replant.acf.commands.bukkit.contexts.OnlinePlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.StringUtil;

public class BukkitCommandCompletions
extends CommandCompletions<BukkitCommandCompletionContext> {
    public BukkitCommandCompletions(BukkitCommandManager bukkitCommandManager) {
        super(bukkitCommandManager);
        this.registerAsyncCompletion("mobs", bukkitCommandCompletionContext -> {
            Stream<String> stream = Stream.of(EntityType.values()).map(entityType -> ACFUtil.simplifyString(entityType.getName()));
            return stream.collect(Collectors.toList());
        });
        this.registerAsyncCompletion("chatcolors", bukkitCommandCompletionContext -> {
            String string;
            Stream<ChatColor> stream = Stream.of(ChatColor.values());
            if (bukkitCommandCompletionContext.hasConfig("colorsonly")) {
                stream = stream.filter(chatColor -> chatColor.ordinal() <= 15);
            }
            if ((string = bukkitCommandCompletionContext.getConfig("filter")) != null) {
                Set set = Arrays.stream(ACFPatterns.COLON.split(string)).map(ACFUtil::simplifyString).collect(Collectors.toSet());
                stream = stream.filter(chatColor -> set.contains(ACFUtil.simplifyString(chatColor.name())));
            }
            return stream.map(chatColor -> ACFUtil.simplifyString(chatColor.name())).collect(Collectors.toList());
        });
        this.registerAsyncCompletion("dyecolors", bukkitCommandCompletionContext -> ACFUtil.enumNames(DyeColor.values()));
        this.registerCompletion("worlds", bukkitCommandCompletionContext -> Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toList()));
        this.registerCompletion("players", bukkitCommandCompletionContext -> {
            CommandSender commandSender = bukkitCommandCompletionContext.getSender();
            Validate.notNull((Object)commandSender, (String)"Sender cannot be null");
            Player player = commandSender instanceof Player ? (Player)commandSender : null;
            ArrayList<String> arrayList = new ArrayList<String>();
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                String string = player2.getName();
                if (player != null && !player.canSee(player2) || !StringUtil.startsWithIgnoreCase((String)string, (String)bukkitCommandCompletionContext.getInput())) continue;
                arrayList.add(string);
            }
            arrayList.sort(String.CASE_INSENSITIVE_ORDER);
            return arrayList;
        });
        this.setDefaultCompletion("players", OnlinePlayer.class, de.jeff_media.replant.acf.commands.contexts.OnlinePlayer.class, Player.class);
        this.setDefaultCompletion("worlds", World.class);
    }
}

