package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BukkitCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ACFBukkitScheduler {
    private int localeTask;

    public void registerSchedulerDependencies(BukkitCommandManager bukkitCommandManager) {
        bukkitCommandManager.registerDependency(BukkitScheduler.class, Bukkit.getScheduler());
    }

    public void createDelayedTask(Plugin plugin, Runnable runnable, long l) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, l);
    }

    public void createLocaleTask(Plugin plugin, Runnable runnable, long l, long l2) {
        this.localeTask = Bukkit.getScheduler().runTaskTimer(plugin, runnable, l, l2).getTaskId();
    }

    public void cancelLocaleTask() {
        Bukkit.getScheduler().cancelTask(this.localeTask);
    }
}

