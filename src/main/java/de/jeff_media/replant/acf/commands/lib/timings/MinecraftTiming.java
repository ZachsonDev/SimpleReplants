package de.jeff_media.replant.acf.commands.lib.timings;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import de.jeff_media.replant.acf.commands.lib.timings.MCTiming;
import org.bukkit.plugin.Plugin;

class MinecraftTiming
extends MCTiming {
    private final Timing timing;

    MinecraftTiming(Plugin plugin, String string, MCTiming mCTiming) {
        this.timing = Timings.of((Plugin)plugin, (String)string, mCTiming instanceof MinecraftTiming ? ((MinecraftTiming)mCTiming).timing : null);
    }

    @Override
    public MCTiming startTiming() {
        this.timing.startTimingIfSync();
        return this;
    }

    @Override
    public void stopTiming() {
        this.timing.stopTimingIfSync();
    }
}

