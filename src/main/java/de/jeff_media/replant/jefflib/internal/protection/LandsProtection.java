package de.jeff_media.replant.jefflib.internal.protection;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.internal.protection.PluginProtection;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class LandsProtection
implements PluginProtection {
    private final LandsIntegration lands = this.getApi();

    private LandsIntegration getApi() {
        return LandsIntegration.of((Plugin)JeffLib.getPlugin());
    }

    @Override
    public boolean canBuild(@NotNull Player player, @NotNull Location location) {
        return this.hasRoleFlag(player, location, Flags.BLOCK_PLACE);
    }

    @Override
    public boolean canBreak(@NotNull Player player, @NotNull Location location) {
        return this.hasRoleFlag(player, location, Flags.BLOCK_BREAK);
    }

    private boolean hasRoleFlag(Player player, Location location, RoleFlag roleFlag) {
        Area area = this.lands.getArea(location);
        if (area == null) {
            return true;
        }
        return area.hasRoleFlag(player.getUniqueId(), roleFlag);
    }
}

