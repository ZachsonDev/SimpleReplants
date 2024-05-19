package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFBukkitScheduler;
import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ACFPaperScheduler
extends ACFBukkitScheduler {
    private final AsyncScheduler scheduler;
    private ScheduledTask localeTask;

    public ACFPaperScheduler(@NotNull AsyncScheduler asyncScheduler) {
        this.scheduler = asyncScheduler;
    }

    @Override
    public void registerSchedulerDependencies(BukkitCommandManager bukkitCommandManager) {
        bukkitCommandManager.registerDependency(AsyncScheduler.class, this.scheduler);
    }

    @Override
    public void createDelayedTask(Plugin plugin, Runnable runnable, long l) {
        this.scheduler.runDelayed(plugin, scheduledTask -> runnable.run(), l / 20L, TimeUnit.SECONDS);
    }

    @Override
    public void createLocaleTask(Plugin plugin, Runnable runnable, long l, long l2) {
        this.localeTask = this.scheduler.runAtFixedRate(plugin, scheduledTask -> runnable.run(), l / 20L, l2 / 20L, TimeUnit.SECONDS);
    }

    @Override
    public void cancelLocaleTask() {
        this.localeTask.cancel();
    }
}

