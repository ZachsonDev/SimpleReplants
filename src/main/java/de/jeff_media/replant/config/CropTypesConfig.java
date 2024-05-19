package de.jeff_media.replant.config;

import de.jeff_media.replant.jefflib.EnumUtils;
import de.jeff_media.replant.jefflib.data.Config;
import java.util.Set;
import org.bukkit.Material;

public class CropTypesConfig
extends Config {
    private final boolean cropsBlacklist = this.getBoolean("crops-blacklist", true);
    private final boolean saplingsBlacklist = this.getBoolean("saplings-blacklist", true);
    private final Set<Material> cropsList = EnumUtils.getEnumsFromListAsSet(Material.class, this.getStringList("crops"));
    private final Set<Material> saplingsList = EnumUtils.getEnumsFromListAsSet(Material.class, this.getStringList("saplings"));

    public CropTypesConfig() {
        super("crop-types.yml");
    }

    public boolean isCropEnabled(Material material) {
        boolean bl = this.cropsList.contains(material);
        if (this.cropsBlacklist) {
            return !bl;
        }
        return bl;
    }

    public boolean isSaplingEnabled(Material material) {
        boolean bl = this.saplingsList.contains(material);
        if (this.saplingsBlacklist) {
            return !bl;
        }
        return bl;
    }
}

