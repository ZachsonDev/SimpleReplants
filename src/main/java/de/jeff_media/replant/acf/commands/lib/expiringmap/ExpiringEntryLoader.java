package de.jeff_media.replant.acf.commands.lib.expiringmap;

import de.jeff_media.replant.acf.commands.lib.expiringmap.ExpiringValue;

public interface ExpiringEntryLoader<K, V> {
    public ExpiringValue<V> load(K var1);
}

