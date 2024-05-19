package de.jeff_media.replant.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.jeff_media.replant.hooks.PluginHandler;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardHandler
extends PluginHandler {
    private static final WorldGuardPlatform platform;

    @Override
    public boolean canBuild(Player player, Block block) {
        if (platform == null) {
            return true;
        }
        RegionQuery regionQuery = platform.getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt((Location)block.getLocation());
        World world = BukkitAdapter.adapt((org.bukkit.World)block.getWorld());
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (!platform.getSessionManager().hasBypass(localPlayer, world)) {
            return regionQuery.testState(location, localPlayer, new StateFlag[]{Flags.BUILD});
        }
        return true;
    }

    static {
        WorldGuardPlatform worldGuardPlatform;
        try {
            worldGuardPlatform = WorldGuard.getInstance().getPlatform();
        }
        catch (Exception exception) {
            worldGuardPlatform = null;
        }
        platform = worldGuardPlatform;
    }
}

