package de.jeff_media.replant.updatechecker;

import de.jeff_media.replant.updatechecker.ArtifactVersion;
import de.jeff_media.replant.updatechecker.ComparableVersion;
import java.util.StringTokenizer;
import org.apache.commons.lang.math.NumberUtils;

public class DefaultArtifactVersion
implements ArtifactVersion {
    private Integer majorVersion;
    private Integer minorVersion;
    private Integer incrementalVersion;
    private Integer buildNumber;
    private String qualifier;
    private ComparableVersion comparable;

    public DefaultArtifactVersion(String string) {
        this.parseVersion(string);
    }

    public int hashCode() {
        return 11 + this.comparable.hashCode();
    }

    public boolean equals(Object object) {
        return this == object || object instanceof ArtifactVersion && this.compareTo((ArtifactVersion)object) == 0;
    }

    @Override
    public int compareTo(ArtifactVersion artifactVersion) {
        if (artifactVersion instanceof DefaultArtifactVersion) {
            return this.comparable.compareTo(((DefaultArtifactVersion)artifactVersion).comparable);
        }
        return this.compareTo(new DefaultArtifactVersion(artifactVersion.toString()));
    }

    @Override
    public int getMajorVersion() {
        return this.majorVersion != null ? this.majorVersion : 0;
    }

    @Override
    public int getMinorVersion() {
        return this.minorVersion != null ? this.minorVersion : 0;
    }

    @Override
    public int getIncrementalVersion() {
        return this.incrementalVersion != null ? this.incrementalVersion : 0;
    }

    @Override
    public int getBuildNumber() {
        return this.buildNumber != null ? this.buildNumber : 0;
    }

    @Override
    public String getQualifier() {
        return this.qualifier;
    }

    @Override
    public final void parseVersion(String string) {
        String string2;
        this.comparable = new ComparableVersion(string);
        int n = string.indexOf(45);
        String string3 = null;
        if (n < 0) {
            string2 = string;
        } else {
            string2 = string.substring(0, n);
            string3 = string.substring(n + 1);
        }
        if (string3 != null) {
            if (string3.length() == 1 || !string3.startsWith("0")) {
                this.buildNumber = DefaultArtifactVersion.tryParseInt(string3);
                if (this.buildNumber == null) {
                    this.qualifier = string3;
                }
            } else {
                this.qualifier = string3;
            }
        }
        if (!string2.contains(".") && !string2.startsWith("0")) {
            this.majorVersion = DefaultArtifactVersion.tryParseInt(string2);
            if (this.majorVersion == null) {
                this.qualifier = string;
                this.buildNumber = null;
            }
        } else {
            boolean bl = false;
            StringTokenizer stringTokenizer = new StringTokenizer(string2, ".");
            if (stringTokenizer.hasMoreTokens()) {
                this.majorVersion = DefaultArtifactVersion.getNextIntegerToken(stringTokenizer);
                if (this.majorVersion == null) {
                    bl = true;
                }
            } else {
                bl = true;
            }
            if (stringTokenizer.hasMoreTokens()) {
                this.minorVersion = DefaultArtifactVersion.getNextIntegerToken(stringTokenizer);
                if (this.minorVersion == null) {
                    bl = true;
                }
            }
            if (stringTokenizer.hasMoreTokens()) {
                this.incrementalVersion = DefaultArtifactVersion.getNextIntegerToken(stringTokenizer);
                if (this.incrementalVersion == null) {
                    bl = true;
                }
            }
            if (stringTokenizer.hasMoreTokens()) {
                this.qualifier = stringTokenizer.nextToken();
                bl = NumberUtils.isDigits((String)this.qualifier);
            }
            if (string2.contains("..") || string2.startsWith(".") || string2.endsWith(".")) {
                bl = true;
            }
            if (bl) {
                this.qualifier = string;
                this.majorVersion = null;
                this.minorVersion = null;
                this.incrementalVersion = null;
                this.buildNumber = null;
            }
        }
    }

    private static Integer getNextIntegerToken(StringTokenizer stringTokenizer) {
        String string = stringTokenizer.nextToken();
        if (string.length() > 1 && string.startsWith("0")) {
            return null;
        }
        return DefaultArtifactVersion.tryParseInt(string);
    }

    private static Integer tryParseInt(String string) {
        if (!NumberUtils.isDigits((String)string)) {
            return null;
        }
        try {
            long l = Long.parseLong(string);
            if (l > Integer.MAX_VALUE) {
                return null;
            }
            return (int)l;
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    public String toString() {
        return this.comparable.toString();
    }
}

