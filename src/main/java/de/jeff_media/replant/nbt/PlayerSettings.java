package de.jeff_media.replant.nbt;

import de.jeff_media.replant.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class PlayerSettings {
    private static final Main main = Main.getInstance();
    public static final NamespacedKey CROP_REPLANTING = new NamespacedKey((Plugin)main, "replant_crops");
    public static final NamespacedKey TREE_REPLANTING = new NamespacedKey((Plugin)main, "replant_trees");

    public static boolean hasEnabled(Player player, NamespacedKey namespacedKey) {
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        byte by = main.getConfig().getBoolean("crop-replant-enabled-by-default") ? (byte)1 : 0;
        return (Byte)persistentDataContainer.getOrDefault(namespacedKey, PersistentDataType.BYTE, (Object)by) == 1;
    }

    public static void setEnabled(Player player, NamespacedKey namespacedKey, boolean bl) {
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        persistentDataContainer.set(namespacedKey, PersistentDataType.BYTE, (Object)(bl ? (byte)1 : 0));
    }
}

