package de.jeff_media.replant.acf.commands.lib.expiringmap;

import de.jeff_media.replant.acf.commands.lib.expiringmap.ExpirationPolicy;
import java.util.concurrent.TimeUnit;

public final class ExpiringValue<V> {
    private static final long UNSET_DURATION = -1L;
    private final V value;
    private final ExpirationPolicy expirationPolicy;
    private final long duration;
    private final TimeUnit timeUnit;

    public ExpiringValue(V v) {
        this(v, -1L, null, null);
    }

    public ExpiringValue(V v, ExpirationPolicy expirationPolicy) {
        this(v, -1L, null, expirationPolicy);
    }

    public ExpiringValue(V v, long l, TimeUnit timeUnit) {
        this(v, l, timeUnit, null);
        if (timeUnit == null) {
            throw new NullPointerException();
        }
    }

    public ExpiringValue(V v, ExpirationPolicy expirationPolicy, long l, TimeUnit timeUnit) {
        this(v, l, timeUnit, expirationPolicy);
        if (timeUnit == null) {
            throw new NullPointerException();
        }
    }

    private ExpiringValue(V v, long l, TimeUnit timeUnit, ExpirationPolicy expirationPolicy) {
        this.value = v;
        this.expirationPolicy = expirationPolicy;
        this.duration = l;
        this.timeUnit = timeUnit;
    }

    public V getValue() {
        return this.value;
    }

    public ExpirationPolicy getExpirationPolicy() {
        return this.expirationPolicy;
    }

    public long getDuration() {
        return this.duration;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) return false;
        if (this.getClass() != object.getClass()) {
            return false;
        }
        ExpiringValue expiringValue = (ExpiringValue)object;
        if (this.value != null) {
            if (!this.value.equals(expiringValue.value)) {
                return false;
            }
        } else if (expiringValue.value != null) return false;
        if (this.expirationPolicy != expiringValue.expirationPolicy) return false;
        if (this.duration != expiringValue.duration) return false;
        if (this.timeUnit != expiringValue.timeUnit) return false;
        return true;
    }

    public String toString() {
        return "ExpiringValue{value=" + this.value + ", expirationPolicy=" + (Object)((Object)this.expirationPolicy) + ", duration=" + this.duration + ", timeUnit=" + (Object)((Object)this.timeUnit) + '}';
    }
}

