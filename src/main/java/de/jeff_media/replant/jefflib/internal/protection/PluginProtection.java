package de.jeff_media.replant.jefflib.internal.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PluginProtection {
    public boolean canBuild(@NotNull Player var1, @NotNull Location var2);

    default public boolean canBreak(@NotNull Player player, @NotNull Location location) {
        return this.canBuild(player, location);
    }
}

