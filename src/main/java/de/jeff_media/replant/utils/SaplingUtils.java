package de.jeff_media.replant.utils;

import de.jeff_media.replant.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;

public class SaplingUtils {
    private static final Main main = Main.getInstance();
    private static final HashSet<Material> saplings = new HashSet<Material>(Arrays.asList(Material.ACACIA_SAPLING, Material.BIRCH_SAPLING, Material.DARK_OAK_SAPLING, Material.JUNGLE_SAPLING, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS, Material.NETHER_WART));
    private static final HashSet<Material> overworldGroundTypes = new HashSet<Material>(Arrays.asList(Material.DIRT, Material.COARSE_DIRT, Material.PODZOL, Material.GRASS_BLOCK, Material.FARMLAND));
    private static final HashSet<Material> crimsonGroundTypes = new HashSet<Material>(Collections.singleton(Material.CRIMSON_NYLIUM));
    private static final HashSet<Material> warpedGroundTypes = new HashSet<Material>(Collections.singleton(Material.WARPED_NYLIUM));
    private static final HashSet<Material> netherwartGroundTypes = new HashSet<Material>(Collections.singleton(Material.SOUL_SAND));

    @Nullable
    public static Block findValidSpot(Block block, Item item) {
        if (!Main.getInstance().getConfig().getBoolean("search-nearby-when-block-is-occupied")) {
            return null;
        }
        ArrayList<Block> arrayList = new ArrayList<Block>();
        int n = main.getConfig().getInt("sapling-spread");
        int n2 = 0;
        for (int i = -n; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    Block block2 = block.getRelative(i, j, k);
                    Block block3 = block2.getRelative(BlockFace.DOWN);
                    if (block2.getType().isAir() && SaplingUtils.isValidGround(block3, item)) {
                        arrayList.add(block2);
                    }
                    if (!SaplingUtils.isSapling(block2.getType())) continue;
                    ++n2;
                }
            }
        }
        if (main.getConfig().getInt("max-saplings") > 0 && n2 >= main.getConfig().getInt("max-saplings")) {
            return null;
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        if (arrayList.contains(block)) {
            return block;
        }
        Collections.shuffle(arrayList);
        return (Block)arrayList.get(0);
    }

    public static boolean isSapling(Item item) {
        return SaplingUtils.isSapling(item.getItemStack().getType());
    }

    public static boolean isSapling(Material material) {
        return saplings.contains(material);
    }

    public static boolean isValidGround(Block block, Item item) {
        Material material = item.getItemStack().getType();
        return SaplingUtils.getValidGroundTypes(material).contains(block.getType());
    }

    public static Set<Material> getValidGroundTypes(Material material) {
        HashSet hashSet = new HashSet();
        switch (material) {
            case ACACIA_SAPLING: 
            case BIRCH_SAPLING: 
            case DARK_OAK_SAPLING: 
            case JUNGLE_SAPLING: 
            case OAK_SAPLING: 
            case SPRUCE_SAPLING: {
                return overworldGroundTypes;
            }
            case CRIMSON_FUNGUS: {
                return crimsonGroundTypes;
            }
            case WARPED_FUNGUS: {
                return warpedGroundTypes;
            }
            case NETHER_WART: {
                return netherwartGroundTypes;
            }
        }
        throw new IllegalArgumentException();
    }
}

