package de.jeff_media.replant.acf.commands.lib.timings;

import de.jeff_media.replant.acf.commands.lib.timings.MCTiming;
import de.jeff_media.replant.acf.commands.lib.timings.TimingType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.plugin.Plugin;

public class TimingManager {
    private static TimingType timingProvider;
    private static final Object LOCK;
    private final Plugin plugin;
    private final Map<String, MCTiming> timingCache = new HashMap<String, MCTiming>(0);

    private TimingManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static TimingManager of(Plugin plugin) {
        return new TimingManager(plugin);
    }

    public MCTiming ofStart(String string) {
        return this.ofStart(string, null);
    }

    public MCTiming ofStart(String string, MCTiming mCTiming) {
        return this.of(string, mCTiming).startTiming();
    }

    public MCTiming of(String string) {
        return this.of(string, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MCTiming of(String string, MCTiming mCTiming) {
        Object object;
        Object object2;
        Object object3;
        if (timingProvider == null) {
            object3 = LOCK;
            synchronized (object3) {
                if (timingProvider == null) {
                    try {
                        object2 = Class.forName("co.aikar.timings.Timing");
                        object = ((Class)object2).getMethod("startTiming", new Class[0]);
                        timingProvider = ((Method)object).getReturnType() != object2 ? TimingType.MINECRAFT_18 : TimingType.MINECRAFT;
                    }
                    catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
                        try {
                            Class.forName("org.spigotmc.CustomTimingsHandler");
                            timingProvider = TimingType.SPIGOT;
                        }
                        catch (ClassNotFoundException classNotFoundException) {
                            timingProvider = TimingType.EMPTY;
                        }
                    }
                }
            }
        }
        if (timingProvider.useCache()) {
            object2 = this.timingCache;
            synchronized (object2) {
                object = string.toLowerCase();
                object3 = this.timingCache.get(object);
                if (object3 == null) {
                    object3 = timingProvider.newTiming(this.plugin, string, mCTiming);
                    this.timingCache.put((String)object, (MCTiming)object3);
                }
            }
            return object3;
        }
        return timingProvider.newTiming(this.plugin, string, mCTiming);
    }

    static {
        LOCK = new Object();
    }
}

