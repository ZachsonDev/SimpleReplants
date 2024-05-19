package de.jeff_media.replant.jefflib.data;

import com.google.common.base.Enums;
import de.jeff_media.replant.jefflib.HologramManager;
import de.jeff_media.replant.jefflib.JeffLib;
import de.jeff_media.replant.jefflib.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Hologram
implements ConfigurationSerializable {
    private static final double LINE_OFFSET_DEFAULT = -0.25;
    private static final boolean VISIBLE_FOR_ANYONE_DEFAULT = true;
    private static final double VISIBILITY_RADIUS_DEFAULT = 64.0;
    private final Type type;
    @NotNull
    private final List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
    @NotNull
    private final List<Object> entities = new ArrayList<Object>();
    private Integer task = null;
    @Nullable
    private OfflinePlayer player;
    @NotNull
    private Location location;
    private double lineOffset = -0.25;
    @NotNull
    private List<String> lines = new ArrayList<String>();
    private boolean isVisibleForAnyone = true;
    private double visibilityRadius = 64.0;

    public Hologram(Type type) {
        this.type = type;
    }

    public static Hologram deserialize(@NotNull Map<String, Object> map) {
        Type type = (Type)((Object)Enums.getIfPresent(Type.class, (String)((String)map.getOrDefault("type", "ARMORSTAND"))).or((Object)Type.ARMORSTAND));
        Hologram hologram = new Hologram(type);
        hologram.setLineOffset((Double)map.getOrDefault("line-offset", -0.25));
        hologram.getLines().addAll(map.getOrDefault("lines", new ArrayList()));
        hologram.setVisibleForAnyone((Boolean)map.getOrDefault("is-visible-for-anyone", true));
        hologram.setVisibilityRadius((Double)map.getOrDefault("visibility-radius", 64.0));
        hologram.setLocation((Location)map.get("location"));
        List list = map.getOrDefault("players", new ArrayList());
        list.forEach(string -> {
            try {
                UUID uUID = UUID.fromString(string);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)uUID);
                hologram.getPlayers().add(offlinePlayer);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        });
        return hologram;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.location, this.lineOffset, this.lines, this.isVisibleForAnyone, this.visibilityRadius, this.players, this.entities});
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Hologram hologram = (Hologram)object;
        return Double.compare(hologram.lineOffset, this.lineOffset) == 0 && this.isVisibleForAnyone == hologram.isVisibleForAnyone && Double.compare(hologram.visibilityRadius, this.visibilityRadius) == 0 && this.type == hologram.type && this.location.equals((Object)hologram.location) && this.lines.equals(hologram.lines) && this.players.equals(hologram.players) && this.entities.equals(hologram.entities);
    }

    public String toString() {
        return "Hologram{type=" + (Object)((Object)this.type) + ", location=" + this.location + ", lineOffset=" + this.lineOffset + ", lines=" + this.lines + ", isVisibleForAnyone=" + this.isVisibleForAnyone + ", visibilityRadius=" + this.visibilityRadius + ", players=" + this.players + ", entities=" + this.entities + '}';
    }

    public void update(int n) {
        if (this.task != null) {
            Bukkit.getScheduler().cancelTask(this.task.intValue());
        }
        if (n > 0) {
            this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(JeffLib.getPlugin(), this::update, (long)n, (long)n);
        }
    }

    public void update() {
        if (this.lines.size() == this.entities.size()) {
            for (int i = 0; i < this.lines.size(); ++i) {
                JeffLib.getNMSHandler().changeNMSEntityName(this.entities.get(i), this.format().get(i));
            }
        } else {
            for (Object object : this.entities) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    JeffLib.getNMSHandler().hideEntityFromPlayer(object, player);
                }
            }
            this.create();
        }
    }

    private List<String> format() {
        return TextUtils.format(this.lines, this.player);
    }

    public void create() {
        Location location = this.location.clone();
        for (String string : this.format()) {
            Object object = JeffLib.getNMSHandler().createHologram(location, string, this.type);
            this.entities.add(object);
            location = location.add(0.0, this.lineOffset, 0.0);
        }
        HologramManager.getHOLOGRAMS().add(this);
    }

    @NotNull
    public Map<String, Object> serialize() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("line-offset", this.lineOffset);
        hashMap.put("lines", this.lines);
        hashMap.put("is-visible-for-anyone", this.isVisibleForAnyone);
        hashMap.put("visibility-radius", this.visibilityRadius);
        hashMap.put("players", new ArrayList(this.players.stream().map(offlinePlayer -> offlinePlayer.getUniqueId().toString()).collect(Collectors.toList())));
        hashMap.put("location", this.location);
        return hashMap;
    }

    public Type getType() {
        return this.type;
    }

    @NotNull
    public List<OfflinePlayer> getPlayers() {
        return this.players;
    }

    @NotNull
    public List<Object> getEntities() {
        return this.entities;
    }

    @Nullable
    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public void setPlayer(@Nullable OfflinePlayer offlinePlayer) {
        this.player = offlinePlayer;
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(@NotNull Location location) {
        if (location == null) {
            throw new NullPointerException("location is marked non-null but is null");
        }
        this.location = location;
    }

    public double getLineOffset() {
        return this.lineOffset;
    }

    public void setLineOffset(double d) {
        this.lineOffset = d;
    }

    @NotNull
    public List<String> getLines() {
        return this.lines;
    }

    public void setLines(@NotNull List<String> list) {
        if (list == null) {
            throw new NullPointerException("lines is marked non-null but is null");
        }
        this.lines = list;
    }

    public boolean isVisibleForAnyone() {
        return this.isVisibleForAnyone;
    }

    public void setVisibleForAnyone(boolean bl) {
        this.isVisibleForAnyone = bl;
    }

    public double getVisibilityRadius() {
        return this.visibilityRadius;
    }

    public void setVisibilityRadius(double d) {
        this.visibilityRadius = d;
    }

    public static enum Type {
        ARMORSTAND,
        EFFECTCLOUD;

    }

    private static final class Keys {
        static final String TYPE = "type";
        static final String LINE_OFFSET = "line-offset";
        static final String LINES = "lines";
        static final String IS_VISIBlE_FOR_ANYONE = "is-visible-for-anyone";
        static final String VISIBILITY_RADIUS = "visibility-radius";
        static final String PLAYERS = "players";
        static final String LOCATION = "location";

        private Keys() {
        }
    }
}

