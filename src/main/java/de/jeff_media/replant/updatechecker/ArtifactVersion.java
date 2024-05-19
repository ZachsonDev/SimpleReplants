package de.jeff_media.replant.updatechecker;

public interface ArtifactVersion
extends Comparable<ArtifactVersion> {
    public int getMajorVersion();

    public int getMinorVersion();

    public int getIncrementalVersion();

    public int getBuildNumber();

    public String getQualifier();

    public void parseVersion(String var1);
}

