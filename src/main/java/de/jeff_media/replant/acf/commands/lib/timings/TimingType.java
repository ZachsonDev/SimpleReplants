package de.jeff_media.replant.acf.commands.lib.timings;

import de.jeff_media.replant.acf.commands.lib.timings.EmptyTiming;
import de.jeff_media.replant.acf.commands.lib.timings.MCTiming;
import de.jeff_media.replant.acf.commands.lib.timings.Minecraft18Timing;
import de.jeff_media.replant.acf.commands.lib.timings.MinecraftTiming;
import de.jeff_media.replant.acf.commands.lib.timings.SpigotTiming;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.plugin.Plugin;

enum TimingType {
    SPIGOT(true){

        @Override
        MCTiming newTiming(Plugin plugin, String string, MCTiming mCTiming) {
            return new SpigotTiming(string);
        }
    }
    ,
    MINECRAFT{

        @Override
        MCTiming newTiming(Plugin plugin, String string, MCTiming mCTiming) {
            return new MinecraftTiming(plugin, string, mCTiming);
        }
    }
    ,
    MINECRAFT_18{

        @Override
        MCTiming newTiming(Plugin plugin, String string, MCTiming mCTiming) {
            try {
                return new Minecraft18Timing(plugin, string, mCTiming);
            }
            catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
                return new EmptyTiming();
            }
        }
    }
    ,
    EMPTY;

    private final boolean useCache;

    public boolean useCache() {
        return this.useCache;
    }

    private TimingType() {
        this(false);
    }

    private TimingType(boolean bl) {
        this.useCache = bl;
    }

    MCTiming newTiming(Plugin plugin, String string, MCTiming mCTiming) {
        return new EmptyTiming();
    }
}

