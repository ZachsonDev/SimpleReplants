package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.internal.protection.LandsProtection;
import de.jeff_media.replant.jefflib.internal.protection.PlotSquared6Protection;
import de.jeff_media.replant.jefflib.internal.protection.PluginProtection;
import de.jeff_media.replant.jefflib.internal.protection.WorldGuardProtection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class ProtectionUtils {
    private static final List<PluginProtection> PLUGIN_PROTECTIONS = new ArrayList<PluginProtection>();
    private static boolean initialized = false;

    static void loadPluginProtections() {
        PLUGIN_PROTECTIONS.clear();
        ProtectionUtils.register("WorldGuard", WorldGuardProtection::new);
        ProtectionUtils.register("Lands", LandsProtection::new);
        ProtectionUtils.register("PlotSquared", PlotSquared6Protection::new);
        initialized = true;
    }

    private static void register(String string, Supplier<PluginProtection> supplier) {
        block4: {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(string);
            if (plugin != null) {
                try {
                    PLUGIN_PROTECTIONS.add(supplier.get());
                    if (!initialized) {
                        JeffLib.getLogger().info("Hooked into " + plugin.getName() + " " + plugin.getDescription().getVersion());
                    }
                }
                catch (Exception exception) {
                    if (initialized) break block4;
                    JeffLib.getLogger().warning("Could not hook into " + plugin.getName() + " " + plugin.getDescription().getVersion());
                }
            }
        }
    }

    public static boolean canBuild(@NotNull Player player, @NotNull Location location) {
        for (PluginProtection pluginProtection : PLUGIN_PROTECTIONS) {
            if (pluginProtection.canBuild(player, location)) continue;
            return false;
        }
        return true;
    }

    public static boolean canBreak(@NotNull Player player, @NotNull Location location) {
        for (PluginProtection pluginProtection : PLUGIN_PROTECTIONS) {
            if (pluginProtection.canBreak(player, location)) continue;
            return false;
        }
        return true;
    }

    private ProtectionUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

