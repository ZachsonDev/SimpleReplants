package de.jeff_media.replant.jefflib.data;

import de.jeff_media.replant.jefflib.WordUtils;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class McVersion
implements Comparable<McVersion> {
    private static final McVersion CURRENT_VERSION;
    private static final McVersion v1_17;
    private final int major;
    private final int minor;
    private final int patch;

    public McVersion(int n, int n2) {
        this(n, n2, 0);
    }

    public McVersion(int n, int n2, int n3) {
        this.major = n;
        this.minor = n2;
        this.patch = n3;
    }

    public static McVersion current() {
        return CURRENT_VERSION;
    }

    public boolean hasVersionInNmsPackageName() {
        return !this.isAtLeast(v1_17);
    }

    public boolean isAtLeast(McVersion mcVersion) {
        return this.compareTo(mcVersion) >= 0;
    }

    @Override
    public int compareTo(@NotNull McVersion mcVersion) {
        if (this.major > mcVersion.major) {
            return 3;
        }
        if (mcVersion.major > this.major) {
            return -3;
        }
        if (this.minor > mcVersion.minor) {
            return 2;
        }
        if (mcVersion.minor > this.minor) {
            return -2;
        }
        return Integer.compare(this.patch, mcVersion.patch);
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getPatch() {
        return this.patch;
    }

    public int hashCode() {
        return Objects.hash(this.major, this.minor, this.patch);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        McVersion mcVersion = (McVersion)object;
        return this.major == mcVersion.major && this.minor == mcVersion.minor && this.patch == mcVersion.patch;
    }

    public String toString() {
        return this.getName();
    }

    public String getName() {
        if (this.patch == 0) {
            return this.major + "." + this.minor;
        }
        return this.major + "." + this.minor + "." + this.patch;
    }

    public boolean isAtLeast(int n, int n2, int n3) {
        return this.isAtLeast(new McVersion(n, n2, n3));
    }

    public boolean isAtLeast(int n, int n2) {
        return this.isAtLeast(new McVersion(n, n2));
    }

    static {
        v1_17 = new McVersion(1, 17);
        int n = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[0]);
        int n2 = Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[1].split("-")[0]);
        boolean bl = WordUtils.countChar(Bukkit.getBukkitVersion(), '.') == 3;
        int n3 = bl ? Integer.parseInt(Bukkit.getBukkitVersion().split("\\.")[2].split("-")[0]) : 0;
        CURRENT_VERSION = new McVersion(n, n2, n3);
    }
}

