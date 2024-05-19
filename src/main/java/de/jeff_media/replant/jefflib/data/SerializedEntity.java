package de.jeff_media.replant.jefflib.data;

import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.internal.annotations.NMS;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

@NMS
public class SerializedEntity
implements ConfigurationSerializable {
    private final EntityType entityType;
    private final String nbtData;

    public SerializedEntity(EntityType entityType, String string) {
        this.entityType = entityType;
        this.nbtData = string;
    }

    public SerializedEntity(Map<String, Object> map) {
        this.entityType = EntityType.valueOf((String)((String)map.get("entityType")));
        this.nbtData = (String)map.get("nbtData");
    }

    public static SerializedEntity of(Entity entity) {
        return JeffLib.getNMSHandler().serialize(entity);
    }

    public static SerializedEntity deserialize(Map<String, Object> map) {
        return new SerializedEntity(map);
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public String getNbtData() {
        return this.nbtData;
    }

    public Entity spawn(@NotNull Location location) {
        Entity entity = location.getWorld().spawnEntity(location, this.entityType);
        JeffLib.getNMSHandler().applyNbt(entity, this.nbtData);
        return entity;
    }

    @NotNull
    public Map<String, Object> serialize() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("entityType", this.entityType.name());
        hashMap.put("nbtData", this.nbtData);
        return hashMap;
    }
}

