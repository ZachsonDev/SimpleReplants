package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.WorldUtils;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockUtils {
    public static final int[][] BOOKSHELF_OFFSETS = new int[][]{{-2, 0, -2}, {-1, 0, -2}, {0, 0, -2}, {1, 0, -2}, {2, 0, -2}, {-2, 0, -1}, {2, 0, -1}, {-2, 0, 0}, {2, 0, 0}, {-2, 0, 1}, {2, 0, 1}, {-2, 0, 2}, {-1, 0, 2}, {0, 0, 2}, {1, 0, 2}, {2, 0, 2}, {-2, 1, -2}, {-1, 1, -2}, {0, 1, -2}, {1, 1, -2}, {2, 1, -2}, {-2, 1, -1}, {2, 1, -1}, {-2, 1, 0}, {2, 1, 0}, {-2, 1, 1}, {2, 1, 1}, {-2, 1, 2}, {-1, 1, 2}, {0, 1, 2}, {1, 1, 2}, {2, 1, 2}};

    @Nullable
    public static Block getLowestBlockAt(@NotNull Location location) {
        return BlockUtils.getLowestBlockAt(Objects.requireNonNull(location.getWorld()), location.getBlockX(), location.getBlockZ());
    }

    @Nullable
    public static Block getLowestBlockAt(@NotNull World world, int n, int n2) {
        for (int i = WorldUtils.getWorldMinHeight(world); i < world.getMaxHeight(); ++i) {
            Block block = world.getBlockAt(n, i, n2);
            if (block.getType().isAir()) continue;
            return block;
        }
        return null;
    }

    public static List<Map.Entry<String, String>> getBlockDataAsEntries(Block block) {
        String[] stringArray;
        ArrayList<Map.Entry<String, String>> arrayList = new ArrayList<Map.Entry<String, String>>();
        String[] stringArray2 = block.getBlockData().getAsString().split("\\[");
        if (stringArray2.length == 1) {
            return arrayList;
        }
        String string = stringArray2[1];
        string = string.substring(0, string.length() - 1);
        for (String string2 : stringArray = string.split(",")) {
            String string3 = string2.split("=")[0];
            String string4 = string2.split("=")[1];
            arrayList.add(new AbstractMap.SimpleEntry<String, String>(string3, string4));
        }
        return arrayList;
    }

    public static List<Block> getBlocksInRadius(Location location, int n, RadiusType radiusType) {
        return BlockUtils.getBlocksInRadius(location, n, radiusType, block -> true);
    }

    public static List<Block> getBlocksInRadius(Location location, int n, RadiusType radiusType, Predicate<Block> predicate) {
        switch (radiusType.ordinal()) {
            case 1: {
                return BlockUtils.getBlocksInRadiusCircle(location, n, predicate);
            }
            case 0: {
                return BlockUtils.getBlocksInRadiusSquare(location, n, predicate);
            }
        }
        throw new IllegalArgumentException("Unknown RadiusType: " + radiusType.name());
    }

    private static List<Block> getBlocksInRadiusCircle(Location location, int n, Predicate<Block> predicate) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        World world = location.getWorld();
        for (int i = location.getBlockX() - n; i <= location.getBlockX() + n; ++i) {
            for (int j = location.getBlockY() - n; j <= location.getBlockY() + n; ++j) {
                for (int k = location.getBlockZ() - n; k <= location.getBlockZ() + n; ++k) {
                    Block block;
                    Location location2 = new Location(world, (double)i, (double)j, (double)k);
                    double d = location2.distanceSquared(location);
                    if (!(d <= (double)(n * n)) || !predicate.test(block = location2.getBlock())) continue;
                    arrayList.add(block);
                }
            }
        }
        return arrayList;
    }

    private static List<Block> getBlocksInRadiusSquare(Location location, int n, Predicate<Block> predicate) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        for (int i = location.getBlockX() - n; i <= location.getBlockX() + n; ++i) {
            for (int j = location.getBlockY() - n; j <= location.getBlockY() + n; ++j) {
                for (int k = location.getBlockZ() - n; k <= location.getBlockZ() + n; ++k) {
                    Block block = Objects.requireNonNull(location.getWorld()).getBlockAt(i, j, k);
                    if (!predicate.test(block)) continue;
                    arrayList.add(block);
                }
            }
        }
        return arrayList;
    }

    public static Location getCenter(Block block) {
        return block.getLocation().add(0.5, 0.5, 0.5);
    }

    @NMS
    public static void playComposterFillParticlesAndSound(Block block, boolean bl) {
        JeffLib.getNMSHandler().getBlockHandler().playComposterParticlesAndSound(block, bl);
    }

    public static List<Chunk> getChunks(World world, BoundingBox boundingBox, boolean bl) {
        int n = (int)boundingBox.getMinX() >> 4;
        int n2 = (int)boundingBox.getMaxX() >> 4;
        int n3 = (int)boundingBox.getMinZ() >> 4;
        int n4 = (int)boundingBox.getMaxZ() >> 4;
        ArrayList<Chunk> arrayList = new ArrayList<Chunk>();
        for (int i = n; i <= n2; ++i) {
            for (int j = n3; j <= n4; ++j) {
                if (bl && !world.isChunkLoaded(i, j)) continue;
                Chunk chunk = world.getChunkAt(i, j);
                arrayList.add(chunk);
            }
        }
        return arrayList;
    }

    public static List<BlockVector> getBlocks(World world, BoundingBox boundingBox, boolean bl, Predicate<BlockData> predicate) {
        List<ChunkSnapshot> list = BlockUtils.getChunkSnapshots(world, boundingBox, bl);
        ArrayList<BlockVector> arrayList = new ArrayList<BlockVector>();
        int n = WorldUtils.getWorldMinHeight(world);
        for (ChunkSnapshot chunkSnapshot : list) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    for (int k = boundingBox.getMin().getBlockY(); k < boundingBox.getMax().getBlockY() && k <= chunkSnapshot.getHighestBlockYAt(i, j); ++k) {
                        BlockData blockData;
                        BlockVector blockVector = new BlockVector(i, k, j);
                        if (!boundingBox.contains((Vector)BlockUtils.chunkToWorldCoordinates(blockVector, chunkSnapshot.getX(), k, chunkSnapshot.getZ())) || !predicate.test(blockData = chunkSnapshot.getBlockData(i, k, j))) continue;
                        arrayList.add(BlockUtils.chunkToWorldCoordinates(blockVector, chunkSnapshot.getX(), k, chunkSnapshot.getZ()));
                    }
                }
            }
        }
        return arrayList;
    }

    public static List<ChunkSnapshot> getChunkSnapshots(World world, BoundingBox boundingBox, boolean bl) {
        int n = (int)boundingBox.getMinX() >> 4;
        int n2 = (int)boundingBox.getMaxX() >> 4;
        int n3 = (int)boundingBox.getMinZ() >> 4;
        int n4 = (int)boundingBox.getMaxZ() >> 4;
        ArrayList<ChunkSnapshot> arrayList = new ArrayList<ChunkSnapshot>();
        for (int i = n; i <= n2; ++i) {
            for (int j = n3; j <= n4; ++j) {
                if (bl && !world.isChunkLoaded(i, j)) continue;
                Chunk chunk = world.getChunkAt(i, j);
                arrayList.add(chunk.getChunkSnapshot(true, false, false));
            }
        }
        return arrayList;
    }

    public static BlockVector chunkToWorldCoordinates(BlockVector blockVector, int n, int n2, int n3) {
        return new BlockVector(blockVector.getBlockX() + (n << 4), n2, blockVector.getBlockZ() + (n3 << 4));
    }

    public static int getNumberOfEnchantmentTableBookShelves(Block block) {
        return (int)Arrays.stream(BOOKSHELF_OFFSETS).filter(nArray -> BlockUtils.isValidBookShelf(block, nArray)).count();
    }

    private static boolean isValidBookShelf(Block block, int[] nArray) {
        return block.getRelative(nArray[0], nArray[1], nArray[2]).getType() == Material.BOOKSHELF && block.getRelative(nArray[0] / 2, nArray[1], nArray[2] / 2).getType().isAir();
    }

    private BlockUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static enum RadiusType {
        CUBOID,
        SPHERE;

    }

    public static final class Predicates {
        public static final Predicate<Block> AIR = block -> block.getType().isAir();
        public static final Predicate<Block> NOT_AIR = block -> !block.getType().isAir();
        public static final Predicate<Block> SOLID = block -> block.getType().isSolid();
        public static final Predicate<Block> NOT_SOLID = block -> !block.getType().isSolid();
        public static final Predicate<Block> GRAVITY = block -> block.getType().hasGravity();
        public static final Predicate<Block> NO_GRAVITY = block -> !block.getType().hasGravity();
        public static final Predicate<Block> BURNABLE = block -> block.getType().isBurnable();
        public static final Predicate<Block> NOT_BURNABLE = block -> !block.getType().isBurnable();
        public static final Predicate<Block> INTERACTABLE = block -> block.getType().isInteractable();
        public static final Predicate<Block> NOT_INTERACTABLE = block -> !block.getType().isInteractable();
        public static final Predicate<Block> OCCLUDING = block -> block.getType().isOccluding();
        public static final Predicate<Block> NOT_OCCLUDING = block -> !block.getType().isOccluding();

        private Predicates() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }
}

