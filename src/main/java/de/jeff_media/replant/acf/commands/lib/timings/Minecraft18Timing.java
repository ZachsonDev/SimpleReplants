package de.jeff_media.replant.acf.commands.lib.timings;

import de.jeff_media.replant.acf.commands.lib.timings.MCTiming;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

class Minecraft18Timing
extends MCTiming {
    private final Object timing;
    private static Method startTiming;
    private static Method stopTiming;
    private static Method of;

    Minecraft18Timing(Plugin plugin, String string, MCTiming mCTiming) {
        this.timing = of.invoke(null, plugin, string, mCTiming instanceof Minecraft18Timing ? ((Minecraft18Timing)mCTiming).timing : null);
    }

    @Override
    public MCTiming startTiming() {
        try {
            if (startTiming != null) {
                startTiming.invoke(this.timing, new Object[0]);
            }
        }
        catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            // empty catch block
        }
        return this;
    }

    @Override
    public void stopTiming() {
        try {
            if (stopTiming != null) {
                stopTiming.invoke(this.timing, new Object[0]);
            }
        }
        catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            // empty catch block
        }
    }

    static {
        try {
            Class<?> clazz = Class.forName("co.aikar.timings.Timing");
            Class<?> clazz2 = Class.forName("co.aikar.timings.Timings");
            startTiming = clazz.getDeclaredMethod("startTimingIfSync", new Class[0]);
            stopTiming = clazz.getDeclaredMethod("stopTimingIfSync", new Class[0]);
            of = clazz2.getDeclaredMethod("of", Plugin.class, String.class, clazz);
        }
        catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
            Bukkit.getLogger().severe("Timings18 failed to initialize correctly. Stuff's going to be broken.");
        }
    }
}

