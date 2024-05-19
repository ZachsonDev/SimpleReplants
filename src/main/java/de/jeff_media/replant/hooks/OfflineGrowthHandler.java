package de.jeff_media.replant.hooks;

import java.lang.reflect.Method;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class OfflineGrowthHandler {
    private static Method addPlantMethod;

    public static void register(Block block) {
        if (addPlantMethod != null) {
            try {
                addPlantMethod.invoke(null, block.getLocation());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    static {
        try {
            Class<?> clazz = Class.forName("es.yellowzaki.offlinegrowth.api.OfflineGrowthAPI");
            addPlantMethod = clazz.getMethod("addPlant", Location.class);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

