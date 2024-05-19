package de.jeff_media.replant.handlers;

import de.jeff_media.replant.Main;
import de.jeff_media.replant.jefflib.EnumUtils;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SeedManager {
    private static final Set<Material> GROUND_SUGAR_CANE = EnumUtils.getEnumsFromListAsSet(Material.class, Arrays.asList("GRASS_BLOCK", "DIRT", "COARSE_DIRT", "ROOTED_DIRT", "PODZOL", "SAND", "MOSS_BLOCK", "MYCELIUM", "RED_SAND"));
    private static final EnumSet<Material> GROUND_BAMBOO = EnumSet.copyOf(GROUND_SUGAR_CANE);

    public static Material getSeedFromCrop(Material material) {
        Main.debug("getSeedFromCrop: " + material);
        switch (material) {
            case WHEAT: {
                return Material.WHEAT_SEEDS;
            }
            case BEETROOTS: {
                return Material.BEETROOT_SEEDS;
            }
            case CARROTS: {
                return Material.CARROT;
            }
            case POTATOES: {
                return Material.POTATO;
            }
            case COCOA: {
                return Material.COCOA_BEANS;
            }
            case BAMBOO: {
                return Material.BAMBOO_SAPLING;
            }
        }
        return material;
    }

    public static boolean isValidGround(Block block, Material material) {
        switch (material) {
            case NETHER_WART: {
                return block.getRelative(BlockFace.DOWN).getType() == Material.SOUL_SAND;
            }
            case COCOA: 
            case COCOA_BEANS: {
                return true;
            }
            case SUGAR_CANE: {
                return GROUND_SUGAR_CANE.contains(block.getRelative(BlockFace.DOWN).getType());
            }
            case BAMBOO: 
            case BAMBOO_SAPLING: {
                return GROUND_BAMBOO.contains(block.getRelative(BlockFace.DOWN).getType());
            }
        }
        return block.getRelative(BlockFace.DOWN).getType() == Material.FARMLAND;
    }

    public static Material getSeedToRemove(Material material) {
        switch (material) {
            case BAMBOO_SAPLING: {
                return Material.BAMBOO;
            }
        }
        return material;
    }

    public static Material getSeedToReplant(Material material) {
        switch (material) {
            case BAMBOO: {
                return Material.BAMBOO_SAPLING;
            }
        }
        return material;
    }

    static {
        GROUND_BAMBOO.add(Material.GRAVEL);
    }
}

