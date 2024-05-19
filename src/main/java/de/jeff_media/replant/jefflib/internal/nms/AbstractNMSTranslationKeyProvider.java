package de.jeff_media.replant.jefflib.internal.nms;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface AbstractNMSTranslationKeyProvider {
    public String getItemTranslationKey(Material var1);

    public String getBlockTranslationKey(Material var1);

    public String getTranslationKey(Block var1);

    public String getTranslationKey(EntityType var1);

    public String getTranslationKey(ItemStack var1);
}

