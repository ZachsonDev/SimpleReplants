package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.BlockUtils;
import java.util.Comparator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class Comparators {

    public static class BlockByDistanceComparator
    implements Comparator<Block> {
        private final Location origin;

        public BlockByDistanceComparator(@NotNull Location location) {
            this.origin = location;
        }

        public BlockByDistanceComparator(@NotNull Block block) {
            this.origin = BlockUtils.getCenter(block);
        }

        @Override
        public int compare(Block block, Block block2) {
            return Double.compare(BlockUtils.getCenter(block).distanceSquared(this.origin), BlockUtils.getCenter(block2).distanceSquared(this.origin));
        }
    }

    public static class EntityByDistanceComparator
    implements Comparator<Entity> {
        private final Location origin;

        public EntityByDistanceComparator(@NotNull Entity entity) {
            this.origin = entity.getLocation();
        }

        public EntityByDistanceComparator(@NotNull Location location) {
            this.origin = location;
        }

        @Override
        public int compare(Entity entity, Entity entity2) {
            return Double.compare(entity.getLocation().distanceSquared(this.origin), entity2.getLocation().distanceSquared(this.origin));
        }
    }

    public static class LocationByDistanceComparator
    implements Comparator<Location> {
        private final Location origin;

        public LocationByDistanceComparator(Location location) {
            this.origin = location;
        }

        @Override
        public int compare(Location location, Location location2) {
            return Double.compare(location.distanceSquared(this.origin), location2.distanceSquared(this.origin));
        }
    }
}

