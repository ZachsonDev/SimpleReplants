package de.jeff_media.replant.jefflib.internal.glowenchantment;

import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.Translatable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KeyedGlowEnchantment
extends Enchantment
implements Translatable {
    private final NamespacedKey key = Objects.requireNonNull(NamespacedKey.fromString((String)"jefflib:glow"));

    @NotNull
    public String getName() {
        return "JeffLibGlowEffect";
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getStartLevel() {
        return 1;
    }

    @NotNull
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }

    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return true;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @NotNull
    public String getTranslationKey() {
        return "jefflib:enchantment/glow";
    }
}

