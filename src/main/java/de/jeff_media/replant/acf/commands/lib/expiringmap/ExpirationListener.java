package de.jeff_media.replant.acf.commands.lib.expiringmap;

public interface ExpirationListener<K, V> {
    public void expired(K var1, V var2);
}

