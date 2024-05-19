package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ReflUtils;
import de.jeff_media.replant.jefflib.data.OfflinePlayerPersistentDataContainer;
import de.jeff_media.replant.jefflib.exceptions.NMSNotSupportedException;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import de.jeff_media.replant.jefflib.internal.cherokee.Validate;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PDCUtils {
    public static final PersistentDataType<?, ?>[] PRIMITIVE_DATA_TYPES;
    private static final Map<String, NamespacedKey> KEYS;
    private static final Method namespacedKeyFromStringMethod;
    private static final Constructor<NamespacedKey> namespacedKeyConstructor;
    private static final Object craftPersistentDataTypeRegistry;
    private static final Constructor<?> craftPersistentDataContainerConstructor;

    @NotNull
    @Contract(value=" -> new")
    @NMS
    public static PersistentDataContainer createPersistentDataContainer() {
        if (craftPersistentDataTypeRegistry == null || craftPersistentDataContainerConstructor == null) {
            throw new NMSNotSupportedException("Couldn't find class or appropriate constructor of CraftPersistentDataTypeRegistry or CraftPersistentDataContainer.");
        }
        try {
            return (PersistentDataContainer)craftPersistentDataContainerConstructor.newInstance(craftPersistentDataTypeRegistry);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            throw new RuntimeException("Couldn't create new CraftPersistentDataContainer", reflectiveOperationException);
        }
    }

    @Nullable
    public static NamespacedKey getKeyFromString(@NotNull String string, @NotNull String string2) {
        block7: {
            Validate.notNull(string, "Namespace cannot be null");
            Validate.notNull(string2, "Key cannot be null");
            if (namespacedKeyFromStringMethod != null) {
                try {
                    return (NamespacedKey)namespacedKeyFromStringMethod.invoke(null, string + ":" + string2);
                }
                catch (ReflectiveOperationException reflectiveOperationException) {
                    reflectiveOperationException.printStackTrace();
                    break block7;
                }
            }
            if (namespacedKeyConstructor != null) {
                try {
                    return namespacedKeyConstructor.newInstance(string, string2);
                }
                catch (ReflectiveOperationException reflectiveOperationException) {
                    reflectiveOperationException.printStackTrace();
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    return null;
                }
            }
        }
        throw new IllegalStateException("Could not find or invoke NamespacedKey#fromString(String) nor NamespacedKey#NamespacedKey(String, String).");
    }

    public static NamespacedKey getRandomKey() {
        return new NamespacedKey(JeffLib.getPlugin(), UUID.randomUUID().toString());
    }

    public static <T, Z> void set(@NotNull PersistentDataHolder persistentDataHolder, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
        PDCUtils.set(persistentDataHolder, PDCUtils.getKey(string), persistentDataType, z);
    }

    public static <T, Z> void set(@NotNull PersistentDataHolder persistentDataHolder, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
        persistentDataHolder.getPersistentDataContainer().set(namespacedKey, persistentDataType, z);
    }

    public static NamespacedKey getKey(String string) {
        return KEYS.computeIfAbsent(string, string2 -> new NamespacedKey(JeffLib.getPlugin(), string));
    }

    @Nullable
    public static <T, Z> Z get(@NotNull PersistentDataHolder persistentDataHolder, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType) {
        return PDCUtils.get(persistentDataHolder, PDCUtils.getKey(string), persistentDataType);
    }

    @Nullable
    public static <T, Z> Z get(@NotNull PersistentDataHolder persistentDataHolder, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
        return (Z)persistentDataHolder.getPersistentDataContainer().get(namespacedKey, persistentDataType);
    }

    public static <T, Z> void set(@NotNull ItemStack itemStack, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
        PDCUtils.set(itemStack, PDCUtils.getKey(string), persistentDataType, z);
    }

    public static <T, Z> void set(@NotNull ItemStack itemStack, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Objects.requireNonNull(itemMeta);
        PDCUtils.set((PersistentDataHolder)itemMeta, namespacedKey, persistentDataType, z);
        itemStack.setItemMeta(itemMeta);
    }

    @Nullable
    public static <T, Z> Z get(@NotNull ItemStack itemStack, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.get((PersistentDataHolder)itemStack.getItemMeta(), PDCUtils.getKey(string), persistentDataType);
    }

    @Nullable
    public static <T, Z> Z get(@NotNull ItemStack itemStack, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.get((PersistentDataHolder)itemStack.getItemMeta(), namespacedKey, persistentDataType);
    }

    @Nullable
    @Contract(value="_, _, _, !null -> !null")
    public static <T, Z> Z getOrDefault(@NotNull ItemStack itemStack, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType, Z z) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.getOrDefault((PersistentDataHolder)itemStack.getItemMeta(), string, persistentDataType, z);
    }

    @Nullable
    @Contract(value="_, _, _, !null -> !null")
    public static <T, Z> Z getOrDefault(@NotNull PersistentDataHolder persistentDataHolder, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType, Z z) {
        return PDCUtils.getOrDefault(persistentDataHolder, PDCUtils.getKey(string), persistentDataType, z);
    }

    @Nullable
    @Contract(value="_, _, _, !null -> !null")
    public static <T, Z> Z getOrDefault(@NotNull PersistentDataHolder persistentDataHolder, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, Z z) {
        return (Z)persistentDataHolder.getPersistentDataContainer().getOrDefault(namespacedKey, persistentDataType, z);
    }

    @Nullable
    @Contract(value="_, _, _, !null -> !null")
    public static <T, Z> Z getOrDefault(@NotNull ItemStack itemStack, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, Z z) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.getOrDefault((PersistentDataHolder)itemStack.getItemMeta(), namespacedKey, persistentDataType, z);
    }

    public static void remove(@NotNull PersistentDataHolder persistentDataHolder, @NotNull String string) {
        PDCUtils.remove(persistentDataHolder, PDCUtils.getKey(string));
    }

    public static void remove(@NotNull PersistentDataHolder persistentDataHolder, @NotNull NamespacedKey namespacedKey) {
        persistentDataHolder.getPersistentDataContainer().remove(namespacedKey);
    }

    public static <T, Z> boolean has(@NotNull PersistentDataHolder persistentDataHolder, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType) {
        return persistentDataHolder.getPersistentDataContainer().has(PDCUtils.getKey(string), persistentDataType);
    }

    public static <T, Z> boolean has(@NotNull PersistentDataHolder persistentDataHolder, @NotNull String string) {
        return persistentDataHolder.getPersistentDataContainer().getKeys().contains(PDCUtils.getKey(string));
    }

    public static void remove(@NotNull ItemStack itemStack, @NotNull NamespacedKey namespacedKey) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Objects.requireNonNull(itemMeta);
        PDCUtils.remove((PersistentDataHolder)itemMeta, namespacedKey);
        itemStack.setItemMeta(itemMeta);
    }

    public static void remove(@NotNull ItemStack itemStack, @NotNull String string) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Objects.requireNonNull(itemMeta);
        PDCUtils.remove((PersistentDataHolder)itemMeta, PDCUtils.getKey(string));
        itemStack.setItemMeta(itemMeta);
    }

    public static <T, Z> boolean has(@NotNull ItemStack itemStack, @NotNull String string, @NotNull PersistentDataType<T, Z> persistentDataType) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.has((PersistentDataHolder)itemStack.getItemMeta(), PDCUtils.getKey(string), persistentDataType);
    }

    public static <T, Z> boolean has(@NotNull ItemStack itemStack, @NotNull String string) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.has((PersistentDataHolder)itemStack.getItemMeta(), PDCUtils.getKey(string));
    }

    public static <T, Z> boolean has(@NotNull PersistentDataHolder persistentDataHolder, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
        return persistentDataHolder.getPersistentDataContainer().has(namespacedKey, persistentDataType);
    }

    public static <T, Z> boolean has(@NotNull PersistentDataHolder persistentDataHolder, @NotNull NamespacedKey namespacedKey) {
        return persistentDataHolder.getPersistentDataContainer().getKeys().contains(namespacedKey);
    }

    public static <T, Z> boolean has(@NotNull ItemStack itemStack, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.has((PersistentDataHolder)itemStack.getItemMeta(), namespacedKey, persistentDataType);
    }

    public static <T, Z> boolean has(@NotNull ItemStack itemStack, @NotNull NamespacedKey namespacedKey) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.has((PersistentDataHolder)itemStack.getItemMeta(), namespacedKey);
    }

    @NotNull
    public static Set<NamespacedKey> getKeys(@NotNull ItemStack itemStack) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.getKeys((PersistentDataHolder)itemStack.getItemMeta());
    }

    @NotNull
    public static Set<NamespacedKey> getKeys(@NotNull PersistentDataHolder persistentDataHolder) {
        return persistentDataHolder.getPersistentDataContainer().getKeys();
    }

    public static boolean isEmpty(@NotNull ItemStack itemStack) {
        Objects.requireNonNull(itemStack.getItemMeta());
        return PDCUtils.isEmpty((PersistentDataHolder)itemStack.getItemMeta());
    }

    public static boolean isEmpty(@NotNull PersistentDataHolder persistentDataHolder) {
        return persistentDataHolder.getPersistentDataContainer().isEmpty();
    }

    public static void copy(@NotNull PersistentDataContainer persistentDataContainer, @NotNull PersistentDataContainer persistentDataContainer2) {
        for (NamespacedKey namespacedKey : persistentDataContainer.getKeys()) {
            PersistentDataType<?, ?> persistentDataType = PDCUtils.getDataType(persistentDataContainer, namespacedKey);
            Validate.notNull(persistentDataType, "Could not find data type for key " + namespacedKey);
            Object object = persistentDataContainer.get(namespacedKey, persistentDataType);
            if (object == null) continue;
            persistentDataContainer2.set(namespacedKey, persistentDataType, object);
        }
    }

    public static PersistentDataType<?, ?> getDataType(@NotNull PersistentDataContainer persistentDataContainer, @NotNull NamespacedKey namespacedKey) {
        for (PersistentDataType<?, ?> persistentDataType : PRIMITIVE_DATA_TYPES) {
            if (!persistentDataContainer.has(namespacedKey, persistentDataType)) continue;
            return persistentDataType;
        }
        return null;
    }

    @NMS
    @NotNull
    public static String serialize(@NotNull PersistentDataContainer persistentDataContainer) {
        return JeffLib.getNMSHandler().serializePdc(persistentDataContainer);
    }

    public static void deserialize(@NotNull String string, @NotNull PersistentDataContainer persistentDataContainer) {
        try {
            JeffLib.getNMSHandler().deserializePdc(string, persistentDataContainer);
        }
        catch (Exception exception) {
            throw new IOException("Could not deserialize PDC", exception);
        }
    }

    @NotNull
    @NMS
    public static CompletableFuture<OfflinePlayerPersistentDataContainer> getOfflinePlayerPersistentDataContainer(UUID uUID) {
        return OfflinePlayerPersistentDataContainer.of(uUID);
    }

    @NotNull
    @NMS
    public static CompletableFuture<OfflinePlayerPersistentDataContainer> getOfflinePlayerPersistentDataContainer(OfflinePlayer offlinePlayer) {
        return OfflinePlayerPersistentDataContainer.of(offlinePlayer);
    }

    private PDCUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        Object object = new PersistentDataType[]{PersistentDataType.BYTE, PersistentDataType.SHORT, PersistentDataType.INTEGER, PersistentDataType.LONG, PersistentDataType.FLOAT, PersistentDataType.DOUBLE, PersistentDataType.STRING, PersistentDataType.BYTE_ARRAY, PersistentDataType.INTEGER_ARRAY, PersistentDataType.LONG_ARRAY};
        try {
            object = new PersistentDataType[]{PersistentDataType.BYTE, PersistentDataType.SHORT, PersistentDataType.INTEGER, PersistentDataType.LONG, PersistentDataType.FLOAT, PersistentDataType.DOUBLE, PersistentDataType.STRING, PersistentDataType.BYTE_ARRAY, PersistentDataType.INTEGER_ARRAY, PersistentDataType.LONG_ARRAY, PersistentDataType.TAG_CONTAINER_ARRAY, PersistentDataType.TAG_CONTAINER};
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        PRIMITIVE_DATA_TYPES = object;
        KEYS = new HashMap<String, NamespacedKey>();
        namespacedKeyFromStringMethod = ReflUtils.getMethod(NamespacedKey.class, "fromString", String.class);
        namespacedKeyConstructor = ReflUtils.getConstructor(NamespacedKey.class, String.class, String.class);
        object = null;
        Object var1_2 = null;
        Constructor<?> constructor = null;
        try {
            object = ReflUtils.getOBCClass("persistence.CraftPersistentDataTypeRegistry");
            var1_2 = ReflUtils.getConstructor(object).newInstance(new Object[0]);
            constructor = ReflUtils.getOBCClass("persistence.CraftPersistentDataContainer").getConstructor(new Class[]{object});
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        craftPersistentDataTypeRegistry = var1_2;
        craftPersistentDataContainerConstructor = constructor;
    }
}

