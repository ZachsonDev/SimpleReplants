package de.jeff_media.replant.acf.commands;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.jeff_media.replant.acf.commands.ACFBrigadierManager;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.commands.PaperCommandManager;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import de.jeff_media.replant.acf.commands.UnstableAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Deprecated
@UnstableAPI
public class PaperBrigadierManager
implements Listener {
    private final PaperCommandManager manager;
    private final ACFBrigadierManager<BukkitBrigadierCommandSource> brigadierManager;

    public PaperBrigadierManager(Plugin plugin, PaperCommandManager paperCommandManager) {
        paperCommandManager.verifyUnstableAPI("brigadier");
        paperCommandManager.log(LogLevel.INFO, "Enabled Brigadier Support!");
        this.manager = paperCommandManager;
        this.brigadierManager = new ACFBrigadierManager(paperCommandManager);
        Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
    }

    @EventHandler
    public void onCommandRegister(CommandRegisteredEvent<BukkitBrigadierCommandSource> commandRegisteredEvent) {
        RootCommand rootCommand = this.manager.getRootCommand(commandRegisteredEvent.getCommandLabel());
        if (rootCommand != null) {
            commandRegisteredEvent.setLiteral(this.brigadierManager.register(rootCommand, (LiteralCommandNode<BukkitBrigadierCommandSource>)commandRegisteredEvent.getLiteral(), (SuggestionProvider<BukkitBrigadierCommandSource>)commandRegisteredEvent.getBrigadierCommand(), (Command<BukkitBrigadierCommandSource>)commandRegisteredEvent.getBrigadierCommand(), this::checkPermRoot, this::checkPermSub));
        }
    }

    private boolean checkPermSub(RegisteredCommand registeredCommand, BukkitBrigadierCommandSource bukkitBrigadierCommandSource) {
        return registeredCommand.hasPermission(this.manager.getCommandIssuer(bukkitBrigadierCommandSource.getBukkitSender()));
    }

    private boolean checkPermRoot(RootCommand rootCommand, BukkitBrigadierCommandSource bukkitBrigadierCommandSource) {
        return rootCommand.hasAnyPermission(this.manager.getCommandIssuer(bukkitBrigadierCommandSource.getBukkitSender()));
    }
}

