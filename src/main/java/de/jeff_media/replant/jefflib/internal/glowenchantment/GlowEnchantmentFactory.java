package de.jeff_media.replant.jefflib.internal.glowenchantment;

import de.jeff_media.replant.jefflib.EnchantmentUtils;
import de.jeff_media.replant.jefflib.PDCUtils;
import de.jeff_media.replant.jefflib.internal.annotations.Internal;
import de.jeff_media.replant.jefflib.internal.glowenchantment.KeyedGlowEnchantment;
import de.jeff_media.replant.jefflib.internal.glowenchantment.LegacyGlowEnchantment;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

@Deprecated
@Internal
public abstract class GlowEnchantmentFactory {
    public static final NamespacedKey GLOW_ENCHANTMENT_KEY = Objects.requireNonNull(PDCUtils.getKeyFromString("jefflib", "glow"));
    private static final Enchantment instance;

    @Deprecated
    public static Enchantment getDeprecatedInstance() {
        return instance;
    }

    public static void register() {
        try {
            EnchantmentUtils.registerEnchantment(instance);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    static {
        Enchantment enchantment = Enchantment.getByKey((NamespacedKey)GLOW_ENCHANTMENT_KEY);
        if (enchantment == null) {
            try {
                enchantment = new KeyedGlowEnchantment();
            }
            catch (Throwable throwable) {
                try {
                    enchantment = new LegacyGlowEnchantment();
                }
                catch (Throwable throwable2) {
                    // empty catch block
                }
            }
        }
        instance = enchantment;
    }
}

