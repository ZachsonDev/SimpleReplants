package de.jeff_media.replant.jefflib.internal.nms;

import com.google.common.collect.Multimap;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;

public interface BukkitUnsafe {
    public static BukkitUnsafe getInstance() {
        return JeffLib.getNMSHandler().getUnsafe();
    }

    default public Material toLegacy(Material material) {
        return Bukkit.getUnsafe().toLegacy(material);
    }

    default public Material fromLegacy(Material material) {
        return Bukkit.getUnsafe().fromLegacy(material);
    }

    default public Material fromLegacy(MaterialData material) {
        return Bukkit.getUnsafe().fromLegacy(material);
    }

    default public Material fromLegacy(MaterialData material, boolean itemPriority) {
        return Bukkit.getUnsafe().fromLegacy(material, itemPriority);
    }

    default public BlockData fromLegacy(Material material, byte data) {
        return Bukkit.getUnsafe().fromLegacy(material, data);
    }

    default public Material getMaterial(String material, int version) {
        return Bukkit.getUnsafe().getMaterial(material, version);
    }

    default public int getDataVersion() {
        return Bukkit.getUnsafe().getDataVersion();
    }

    default public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        return Bukkit.getUnsafe().modifyItemStack(stack, arguments);
    }

    default public void checkSupported(PluginDescriptionFile pdf) throws InvalidPluginException {
        Bukkit.getUnsafe().checkSupported(pdf);
    }

    default public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
        return Bukkit.getUnsafe().processClass(pdf, path, clazz);
    }

    default public Advancement loadAdvancement(NamespacedKey key, String advancement) {
        return Bukkit.getUnsafe().loadAdvancement(key, advancement);
    }

    default public boolean removeAdvancement(NamespacedKey key) {
        return Bukkit.getUnsafe().removeAdvancement(key);
    }

    default public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Material material, EquipmentSlot slot) {
        return Bukkit.getUnsafe().getDefaultAttributeModifiers(material, slot);
    }

    default public CreativeCategory getCreativeCategory(Material material) {
        return Bukkit.getUnsafe().getCreativeCategory(material);
    }

    default public Object getNMSBlockStateBlock(MaterialData material) {
        return this.getNMSBlockState(material.getItemType(), material.getData());
    }

    public Object getNMSBlockState(Material var1, byte var2);

    public MaterialData getMaterialFromNMSBlockState(Object var1);

    public Object getNMSItem(Material var1, short var2);

    public MaterialData getMaterialDataFromNMSItem(Object var1);

    public Material getMaterialFromNMSBlock(Object var1);

    public Material getMaterialFromNMSItem(Object var1);

    @Deprecated
    @NMS(value="1.16.2")
    public Object getFluidFromNMSFluid(Object var1);

    public Object getNMSItemFromMaterial(Material var1);

    public Object getNMSBlockFromMaterial(Material var1);

    @Deprecated
    @NMS(value="1.16.2")
    public Object getNMSFluid(Object var1);

    public Object getNMSResourceLocation(Material var1);

    public byte NMSBlockStateToLegacyData(Object var1);

    public String getMappingsVersion();

    public File getBukkitDataPackFolder();

    public boolean isLegacy(PluginDescriptionFile var1);

    public static final class NBT {
        public static final int TAG_END = 0;
        public static final int TAG_BYTE = 1;
        public static final int TAG_SHORT = 2;
        public static final int TAG_INT = 3;
        public static final int TAG_LONG = 4;
        public static final int TAG_FLOAT = 5;
        public static final int TAG_DOUBLE = 6;
        public static final int TAG_BYTE_ARRAY = 7;
        public static final int TAG_STRING = 8;
        public static final int TAG_LIST = 9;
        public static final int TAG_COMPOUND = 10;
        public static final int TAG_INT_ARRAY = 11;
        public static final int TAG_ANY_NUMBER = 99;
    }
}

