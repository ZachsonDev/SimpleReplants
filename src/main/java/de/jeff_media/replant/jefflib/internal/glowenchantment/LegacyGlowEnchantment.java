package de.jeff_media.replant.jefflib.internal.glowenchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LegacyGlowEnchantment
extends Enchantment {
    private static final NamespacedKey KEY = new NamespacedKey("jefflib", "glow");

    public LegacyGlowEnchantment() {
        super(KEY);
    }

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
        return KEY;
    }
}

