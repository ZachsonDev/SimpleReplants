package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public final class WorldUtils {
    private static final boolean HAS_WORLD_MIN_HEIGHT_METHOD;

    @NotNull
    public static World getDefaultWorld() {
        return Objects.requireNonNull(Bukkit.getWorld((String)WorldUtils.getDefaultWorldName()));
    }

    @NotNull
    public static String getDefaultWorldName() {
        return JeffLib.getNMSHandler().getDefaultWorldName();
    }

    @NMS
    public static void setFullTimeWithoutTimeSkipEvent(@NotNull World world, long l, boolean bl) {
        JeffLib.getNMSHandler().setFullTimeWithoutTimeSkipEvent(world, l, bl);
    }

    public static int getWorldMinHeight(@NotNull World world) {
        return HAS_WORLD_MIN_HEIGHT_METHOD ? world.getMinHeight() : 0;
    }

    static {
        boolean bl;
        try {
            World.class.getMethod("getMinHeight", new Class[0]);
            bl = true;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            bl = false;
        }
        HAS_WORLD_MIN_HEIGHT_METHOD = bl;
    }
}

