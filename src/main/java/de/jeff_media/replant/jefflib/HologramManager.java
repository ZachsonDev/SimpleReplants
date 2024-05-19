package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.data.Hologram;
import de.jeff_media.replant.jefflib.exceptions.NMSNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Deprecated
public final class HologramManager {
    private static final List<Hologram> HOLOGRAMS = new ArrayList<Hologram>();
    private static final Map<OfflinePlayer, List<Hologram>> SHOWN_HOLOGRAMS = new HashMap<OfflinePlayer, List<Hologram>>();
    private static final Runnable RUNNABLE = () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Hologram hologram : HOLOGRAMS) {
                if (!SHOWN_HOLOGRAMS.containsKey(player)) {
                    SHOWN_HOLOGRAMS.put((OfflinePlayer)player, new ArrayList());
                }
                if (!SHOWN_HOLOGRAMS.get(player).contains(hologram) && (hologram.isVisibleForAnyone() || hologram.getPlayers().contains(player)) && hologram.getLocation().getWorld().equals((Object)player.getWorld()) && hologram.getLocation().distanceSquared(player.getLocation()) <= hologram.getVisibilityRadius() * hologram.getVisibilityRadius()) {
                    SHOWN_HOLOGRAMS.get(player).add(hologram);
                    for (Object object : hologram.getEntities()) {
                        JeffLib.getNMSHandler().showEntityToPlayer(object, player);
                    }
                    continue;
                }
                if (!SHOWN_HOLOGRAMS.get(player).contains(hologram) || hologram.getLocation().getWorld().equals((Object)player.getWorld()) && !(hologram.getLocation().distanceSquared(player.getLocation()) > hologram.getVisibilityRadius() * hologram.getVisibilityRadius())) continue;
                SHOWN_HOLOGRAMS.get(player).remove(hologram);
                for (Object object : hologram.getEntities()) {
                    JeffLib.getNMSHandler().hideEntityFromPlayer(object, player);
                }
            }
        }
    };
    private static boolean IS_SCHEDULED = false;

    public static void unloadAllHolograms() {
        for (Hologram hologram : HOLOGRAMS) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!SHOWN_HOLOGRAMS.containsKey(player) || !SHOWN_HOLOGRAMS.get(player).contains(hologram)) continue;
                SHOWN_HOLOGRAMS.get(player).remove(hologram);
                for (Object object : hologram.getEntities()) {
                    JeffLib.getNMSHandler().hideEntityFromPlayer(object, player);
                }
            }
        }
        HOLOGRAMS.clear();
    }

    public static Hologram loadHologram(ConfigurationSection configurationSection) {
        return Hologram.deserialize(configurationSection.getValues(false));
    }

    public static void init() {
        NMSNotSupportedException.check();
        if (!IS_SCHEDULED) {
            IS_SCHEDULED = true;
            Bukkit.getScheduler().runTaskTimer(JeffLib.getPlugin(), RUNNABLE, 5L, 5L);
        }
    }

    private HologramManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static List<Hologram> getHOLOGRAMS() {
        return HOLOGRAMS;
    }
}

