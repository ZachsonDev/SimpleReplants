package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BukkitCommandCompletionContext;
import de.jeff_media.replant.acf.commands.BukkitCommandExecutionContext;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import de.jeff_media.replant.acf.commands.CommandCompletions;
import de.jeff_media.replant.acf.commands.CommandContexts;
import de.jeff_media.replant.acf.commands.PaperAsyncTabCompleteHandler;
import de.jeff_media.replant.acf.commands.PaperBrigadierManager;
import de.jeff_media.replant.acf.commands.PaperCommandCompletions;
import de.jeff_media.replant.acf.commands.PaperCommandContexts;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PaperCommandManager
extends BukkitCommandManager {
    private boolean brigadierAvailable;

    public PaperCommandManager(Plugin plugin) {
        super(plugin);
        try {
            Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
            plugin.getServer().getPluginManager().registerEvents((Listener)new PaperAsyncTabCompleteHandler(this), plugin);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            Class.forName("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent");
            this.brigadierAvailable = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    @Override
    public void enableUnstableAPI(String string) {
        super.enableUnstableAPI(string);
        if ("brigadier".equals(string) && this.brigadierAvailable) {
            new PaperBrigadierManager(this.plugin, this);
        }
    }

    @Override
    public synchronized CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new PaperCommandContexts(this);
        }
        return this.contexts;
    }

    @Override
    public synchronized CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new PaperCommandCompletions(this);
        }
        return this.completions;
    }
}

