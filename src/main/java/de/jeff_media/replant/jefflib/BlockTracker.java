package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.exceptions.UtilityClassInstantiationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BlockTracker {
    private static final Plugin plugin = JeffLib.getPlugin();
    private static final NamespacedKey PLAYER_PLACED_TAG = new NamespacedKey(plugin, "playerplaced");
    private static final Collection<Material> TRACKED_TYPES = new HashSet<Material>();

    private BlockTracker() {
        throw new UtilityClassInstantiationException();
    }

    public static void addTrackedBlockType(Material material) {
        TRACKED_TYPES.add(material);
    }

    public static Collection<Material> getTrackedBlockTypes() {
        return TRACKED_TYPES;
    }

    public static void trackAllBlockTypes() {
        BlockTracker.addTrackedBlockTypes(Arrays.asList(Material.values()));
    }

    public static void addTrackedBlockTypes(Collection<Material> collection) {
        TRACKED_TYPES.addAll(collection);
    }

    public static void clearTrackedBlockTypes() {
        TRACKED_TYPES.clear();
    }

    public static boolean isTrackedBlockType(Material material) {
        return TRACKED_TYPES.contains(material);
    }

    public static void removeTrackedBlockTypes(Collection<Material> collection) {
        TRACKED_TYPES.removeAll(collection);
    }

    public static boolean isPlayerPlacedBlock(Block block) {
        PersistentDataContainer persistentDataContainer = BlockTracker.getPlayerPlacedPDC((PersistentDataHolder)block.getChunk());
        return persistentDataContainer.has(BlockTracker.getKey(block), PersistentDataType.BYTE);
    }

    private static PersistentDataContainer getPlayerPlacedPDC(PersistentDataHolder persistentDataHolder) {
        PersistentDataContainer persistentDataContainer = persistentDataHolder.getPersistentDataContainer();
        return (PersistentDataContainer)persistentDataContainer.getOrDefault(PLAYER_PLACED_TAG, PersistentDataType.TAG_CONTAINER, (Object)persistentDataContainer.getAdapterContext().newPersistentDataContainer());
    }

    @Contract(value="_ -> new")
    private static NamespacedKey getKey(@NotNull Block block) {
        int n = block.getX() & 0xF;
        int n2 = block.getY();
        int n3 = block.getZ() & 0xF;
        return new NamespacedKey(plugin, String.format("%d/%d/%d", n, n2, n3));
    }

    @NotNull
    public static Collection<Block> getPlayerPlacedBlocks(Chunk chunk) {
        HashSet<Block> hashSet = new HashSet<Block>();
        PersistentDataContainer persistentDataContainer = BlockTracker.getPlayerPlacedPDC((PersistentDataHolder)chunk);
        for (NamespacedKey namespacedKey : persistentDataContainer.getKeys()) {
            if (!namespacedKey.getNamespace().equals(PLAYER_PLACED_TAG.getNamespace())) continue;
            String[] stringArray = namespacedKey.getKey().split("/");
            int n = Integer.parseInt(stringArray[0]);
            int n2 = Integer.parseInt(stringArray[1]);
            int n3 = Integer.parseInt(stringArray[2]);
            hashSet.add(chunk.getBlock(n, n2, n3));
        }
        return hashSet;
    }

    public static void setPlayerPlacedBlock(Block block, boolean bl) {
        PersistentDataContainer persistentDataContainer = block.getChunk().getPersistentDataContainer();
        PersistentDataContainer persistentDataContainer2 = BlockTracker.getPlayerPlacedPDC((PersistentDataHolder)block.getChunk());
        NamespacedKey namespacedKey = BlockTracker.getKey(block);
        if (bl) {
            persistentDataContainer2.set(namespacedKey, PersistentDataType.BYTE, (Object)1);
        } else {
            persistentDataContainer2.remove(namespacedKey);
        }
        persistentDataContainer.set(PLAYER_PLACED_TAG, PersistentDataType.TAG_CONTAINER, (Object)persistentDataContainer2);
    }
}

