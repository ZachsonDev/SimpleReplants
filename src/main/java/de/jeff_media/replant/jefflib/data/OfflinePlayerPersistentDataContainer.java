package de.jeff_media.replant.jefflib.data;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.ProfileUtils;
import de.jeff_media.replant.jefflib.ReflUtils;
import de.jeff_media.replant.jefflib.exceptions.NMSNotSupportedException;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import de.jeff_media.replant.jefflib.internal.annotations.Paper;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@NMS
public class OfflinePlayerPersistentDataContainer
implements PersistentDataContainer {
    private final PersistentDataContainer craftPersistentDataContainer;
    private final File file;
    private final Object compoundTag;

    @Internal
    public OfflinePlayerPersistentDataContainer(@NotNull PersistentDataContainer persistentDataContainer, @NotNull File file, @NotNull Object object) {
        this.craftPersistentDataContainer = persistentDataContainer;
        this.file = file;
        this.compoundTag = object;
    }

    @NotNull
    @NMS
    public static CompletableFuture<OfflinePlayerPersistentDataContainer> of(UUID uUID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return JeffLib.getNMSHandler().getPDCFromDatFile(ProfileUtils.getPlayerDataFile(uUID));
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
        });
    }

    @NotNull
    @NMS
    public static CompletableFuture<OfflinePlayerPersistentDataContainer> of(OfflinePlayer offlinePlayer) {
        return OfflinePlayerPersistentDataContainer.of(offlinePlayer.getUniqueId());
    }

    @Deprecated
    @Internal
    public Object getCraftPersistentDataContainer() {
        return this.craftPersistentDataContainer;
    }

    @Deprecated
    @Internal
    public Object getCompoundTag() {
        return this.compoundTag;
    }

    public File getFile() {
        return this.file;
    }

    public void save() {
        try {
            JeffLib.getNMSHandler().updatePdcInDatFile(this);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    public <T, Z> void set(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
        this.craftPersistentDataContainer.set(namespacedKey, persistentDataType, z);
    }

    public <T, Z> boolean has(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
        return this.craftPersistentDataContainer.has(namespacedKey, persistentDataType);
    }

    public boolean has(@NotNull NamespacedKey namespacedKey) {
        return this.craftPersistentDataContainer.getKeys().contains(namespacedKey);
    }

    @Paper
    @NotNull
    public byte[] serializeToBytes() {
        try {
            return (byte[])ReflUtils.getMethod(this.craftPersistentDataContainer.getClass(), "serializeToBytes").invoke((Object)this.craftPersistentDataContainer, new Object[0]);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            throw new RuntimeException(reflectiveOperationException);
        }
    }

    @Paper
    public void readFromBytes(@NotNull byte[] byArray, boolean bl) {
        try {
            ReflUtils.getMethod(this.craftPersistentDataContainer.getClass(), "readFromBytes", byte[].class, Boolean.TYPE).invoke((Object)this.craftPersistentDataContainer, byArray, bl);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            throw new RuntimeException(reflectiveOperationException);
        }
    }

    public <T, Z> Z get(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
        return (Z)this.craftPersistentDataContainer.get(namespacedKey, persistentDataType);
    }

    @NotNull
    public <T, Z> Z getOrDefault(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
        return (Z)this.craftPersistentDataContainer.getOrDefault(namespacedKey, persistentDataType, z);
    }

    @NotNull
    public Set<NamespacedKey> getKeys() {
        return this.craftPersistentDataContainer.getKeys();
    }

    public void remove(@NotNull NamespacedKey namespacedKey) {
        this.craftPersistentDataContainer.remove(namespacedKey);
    }

    public boolean isEmpty() {
        return this.craftPersistentDataContainer.isEmpty();
    }

    public void copyTo(@NotNull PersistentDataContainer persistentDataContainer, boolean bl) {
        try {
            this.craftPersistentDataContainer.copyTo(persistentDataContainer, bl);
        }
        catch (Throwable throwable) {
            throw new NMSNotSupportedException("Requires 1.20.5+");
        }
    }

    @NotNull
    public PersistentDataAdapterContext getAdapterContext() {
        return this.craftPersistentDataContainer.getAdapterContext();
    }
}

