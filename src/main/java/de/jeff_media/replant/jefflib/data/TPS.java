package de.jeff_media.replant.jefflib.data;

import java.util.Arrays;
import java.util.Objects;

public class TPS {
    private final double last1Minute;
    private final double last5Minute;
    private final double last15Minute;

    public TPS(double[] dArray) {
        if (dArray.length != 3) {
            throw new IllegalArgumentException("TPS array doesn't contain 3 values but " + dArray.length + ": " + Arrays.toString(dArray));
        }
        this.last1Minute = dArray[0];
        this.last5Minute = dArray[1];
        this.last15Minute = dArray[2];
    }

    public double getLast1Minute() {
        return this.last1Minute;
    }

    public double getLast5Minute() {
        return this.last5Minute;
    }

    public double getLast15Minute() {
        return this.last15Minute;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TPS tPS = (TPS)object;
        return Double.compare(tPS.last1Minute, this.last1Minute) == 0 && Double.compare(tPS.last5Minute, this.last5Minute) == 0 && Double.compare(tPS.last15Minute, this.last15Minute) == 0;
    }

    public int hashCode() {
        return Objects.hash(this.last1Minute, this.last5Minute, this.last15Minute);
    }

    public String toString() {
        return "TPS{last1Minute=" + this.last1Minute + ", last5Minute=" + this.last5Minute + ", last15Minute=" + this.last15Minute + '}';
    }
}

