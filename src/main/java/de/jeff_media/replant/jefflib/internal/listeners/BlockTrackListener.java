package de.jeff_media.replant.jefflib.internal.listeners;

import de.jeff_media.replant.jefflib.BlockTracker;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;

@Internal
public final class BlockTrackListener
implements Listener {
    private final Plugin plugin = JeffLib.getPlugin();

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent blockPlaceEvent) {
        if (!BlockTracker.isTrackedBlockType(blockPlaceEvent.getBlock().getType())) {
            return;
        }
        BlockTracker.setPlayerPlacedBlock(blockPlaceEvent.getBlock(), true);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent blockBreakEvent) {
        if (BlockTracker.isPlayerPlacedBlock(blockBreakEvent.getBlock())) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> BlockTracker.setPlayerPlacedBlock(blockBreakEvent.getBlock(), false), 1L);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onStructureGrow(StructureGrowEvent structureGrowEvent) {
        for (BlockState blockState : structureGrowEvent.getBlocks()) {
            Block block = blockState.getBlock();
            BlockTracker.setPlayerPlacedBlock(block, false);
        }
    }
}

