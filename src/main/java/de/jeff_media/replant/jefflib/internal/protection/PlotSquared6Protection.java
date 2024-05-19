package de.jeff_media.replant.jefflib.internal.protection;

import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector3;
import de.jeff_media.replant.jefflib.internal.protection.PluginProtection;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlotSquared6Protection
implements PluginProtection {
    private static com.plotsquared.core.location.Location getWeirdLocation(Location location) {
        return com.plotsquared.core.location.Location.at((String)location.getWorld().getName(), (BlockVector3)BlockVector3.at((int)location.getBlockX(), (int)location.getBlockY(), (int)location.getBlockZ()));
    }

    @Override
    public boolean canBuild(@NotNull Player player, @NotNull Location location) {
        com.plotsquared.core.location.Location location2 = PlotSquared6Protection.getWeirdLocation(location);
        if (!location2.isPlotArea()) {
            return true;
        }
        Plot plot = location2.getPlot();
        if (plot == null) {
            return true;
        }
        UUID uUID = player.getUniqueId();
        return plot.isOwner(uUID) || plot.isAdded(uUID);
    }
}

