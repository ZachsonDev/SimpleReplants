package de.jeff_media.replant.jefflib.internal.protection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.jeff_media.replant.jefflib.internal.protection.PluginProtection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldGuardProtection
implements PluginProtection {
    private static boolean testStateFlag(Player player, Location location, StateFlag stateFlag) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testBuild(WorldGuardProtection.getWeirdLocation(location), WorldGuardProtection.getWeirdPlayer(player), new StateFlag[]{stateFlag});
    }

    private static com.sk89q.worldedit.util.Location getWeirdLocation(Location location) {
        return BukkitAdapter.adapt((Location)location);
    }

    private static LocalPlayer getWeirdPlayer(Player player) {
        return WorldGuardPlugin.inst().wrapPlayer(player);
    }

    @Override
    public boolean canBuild(@NotNull Player player, @NotNull Location location) {
        return WorldGuardProtection.testStateFlag(player, location, Flags.BLOCK_PLACE);
    }

    @Override
    public boolean canBreak(@NotNull Player player, @NotNull Location location) {
        return WorldGuardProtection.testStateFlag(player, location, Flags.BLOCK_BREAK);
    }
}

