package de.jeff_media.replant.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
    private static final Set<Material> HOES = new HashSet<Material>();

    public static boolean isHoe(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return HOES.contains(itemStack.getType());
    }

    public static boolean hasHoe(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        return ItemUtils.isHoe(itemStack);
    }

    static {
        Arrays.stream(Material.values()).filter(material -> material.name().endsWith("_HOE")).forEach(HOES::add);
    }
}

