package de.jeff_media.replant.acf.commands.lib.timings;

import de.jeff_media.replant.acf.commands.lib.timings.MCTiming;
import org.bukkit.Bukkit;
import org.spigotmc.CustomTimingsHandler;

class SpigotTiming
extends MCTiming {
    private final CustomTimingsHandler timing;

    SpigotTiming(String string) {
        this.timing = new CustomTimingsHandler(string);
    }

    @Override
    public MCTiming startTiming() {
        if (Bukkit.isPrimaryThread()) {
            this.timing.startTiming();
        }
        return this;
    }

    @Override
    public void stopTiming() {
        if (Bukkit.isPrimaryThread()) {
            this.timing.stopTiming();
        }
    }
}

