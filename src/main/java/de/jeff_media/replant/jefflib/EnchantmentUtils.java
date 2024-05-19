package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.data.McVersion;
import de.jeff_media.replant.jefflib.exceptions.ConflictingEnchantmentException;
import de.jeff_media.replant.jefflib.internal.glowenchantment.GlowEnchantmentFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EnchantmentUtils {
    public static final Enchantment DURABILIRY_ENCHANTMENT = EnchantmentUtils.getEnchant("unbreaking", "DURABILITY", "UNBREAKING");
    public static final Enchantment DIG_SPEED_ENCHANTMENT = EnchantmentUtils.getEnchant("efficiency", "DIG_SPEED", "EFFICIENCY");
    private static boolean hasNagged0 = false;

    private static Enchantment getEnchant(String string, String string2, String string3) {
        Enchantment enchantment = null;
        try {
            enchantment = (Enchantment)Bukkit.getRegistry(Enchantment.class).get(NamespacedKey.minecraft((String)string));
        }
        catch (Throwable throwable) {
            try {
                enchantment = (Enchantment)Enchantment.class.getDeclaredField(string3).get(null);
            }
            catch (Throwable throwable2) {
                try {
                    enchantment = (Enchantment)Enchantment.class.getDeclaredField(string2).get(null);
                }
                catch (Throwable throwable3) {
                    // empty catch block
                }
            }
        }
        return Objects.requireNonNull(enchantment, "Couldn't get enchantment " + string + " from registry, field " + string3 + " or field " + string2);
    }

    public static void registerEnchantment(Enchantment enchantment) {
        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
            field.setAccessible(false);
            Method method = Enchantment.class.getDeclaredMethod("registerEnchantment", Enchantment.class);
            method.invoke(null, enchantment);
        }
        catch (Throwable throwable) {
            throw new ConflictingEnchantmentException(throwable.getMessage());
        }
    }

    public static void applyBook(EnchantmentStorageMeta enchantmentStorageMeta, ItemStack itemStack, boolean bl) {
        Map map = itemStack.getEnchantments();
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        block0: for (Map.Entry entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
            int n;
            Enchantment enchantment = (Enchantment)entry.getKey();
            int n2 = (Integer)entry.getValue();
            if (!bl) {
                if (!enchantment.canEnchantItem(itemStack)) continue;
                for (Enchantment enchantment2 : map.keySet()) {
                    if (enchantment.conflictsWith(enchantment2)) continue;
                    continue block0;
                }
            }
            if ((n = map.getOrDefault(enchantment, 0).intValue()) > n2) continue;
            if (n == n2) {
                ++n2;
            }
            itemMeta.addEnchant(enchantment, n2, true);
        }
        itemStack.setItemMeta(itemMeta);
    }

    public static int getLevel(ItemStack itemStack, Enchantment enchantment) {
        if (!itemStack.hasItemMeta()) {
            return 0;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert (itemMeta != null);
        if (itemMeta.hasEnchant(enchantment)) {
            return itemMeta.getEnchantLevel(enchantment);
        }
        return 0;
    }

    @NotNull
    public static Map<Enchantment, Integer> fromConfigurationSection(ConfigurationSection configurationSection) {
        HashMap<Enchantment, Integer> hashMap = new HashMap<Enchantment, Integer>();
        for (String string : configurationSection.getKeys(false)) {
            Enchantment enchantment = EnchantmentUtils.getByName(string);
            int n = configurationSection.getInt(string);
            if (enchantment == null) continue;
            hashMap.put(enchantment, n);
        }
        return hashMap;
    }

    @Nullable
    public static Enchantment getByName(@NotNull String string) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (!enchantment.getKey().getKey().equalsIgnoreCase(string)) continue;
            return enchantment;
        }
        return null;
    }

    public static boolean addGlowEffect(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        boolean bl = EnchantmentUtils.addGlowEffect(itemMeta);
        if (bl) {
            itemStack.setItemMeta(itemMeta);
        }
        return bl;
    }

    public static boolean addGlowEffect(@NotNull ItemMeta itemMeta) {
        if (McVersion.current().isAtLeast(1, 20, 5)) {
            if (itemMeta.hasEnchantmentGlintOverride()) {
                return false;
            }
            itemMeta.setEnchantmentGlintOverride(Boolean.valueOf(true));
            return true;
        }
        if (itemMeta.hasEnchant(GlowEnchantmentFactory.getDeprecatedInstance())) {
            return false;
        }
        itemMeta.addEnchant(GlowEnchantmentFactory.getDeprecatedInstance(), 1, true);
        return true;
    }

    public static boolean hasGlowEffect(@NotNull ItemMeta itemMeta) {
        if (McVersion.current().isAtLeast(1, 20, 5)) {
            return itemMeta.hasEnchantmentGlintOverride();
        }
        return itemMeta.hasEnchant(GlowEnchantmentFactory.getDeprecatedInstance());
    }

    public static boolean removeGlowEffect(@NotNull ItemMeta itemMeta) {
        if (EnchantmentUtils.hasGlowEffect(itemMeta)) {
            return false;
        }
        if (McVersion.current().isAtLeast(1, 20, 5)) {
            itemMeta.setEnchantmentGlintOverride(null);
        } else {
            try {
                itemMeta.removeEnchant(GlowEnchantmentFactory.getDeprecatedInstance());
            }
            catch (Throwable throwable) {
                if (!hasNagged0) {
                    JeffLib.getLogger().warning("Failed to remove glow effect - consider updating to 1.20.5+");
                    hasNagged0 = true;
                }
                return false;
            }
        }
        return true;
    }

    private EnchantmentUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

