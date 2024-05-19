package de.jeff_media.replant.acf.locales;

import de.jeff_media.replant.acf.locales.MessageKeyProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageKey
implements MessageKeyProvider {
    private static final AtomicInteger counter = new AtomicInteger(1);
    private static final Map<String, MessageKey> keyMap = new ConcurrentHashMap<String, MessageKey>();
    private final int id = counter.getAndIncrement();
    private final String key;

    private MessageKey(String string) {
        this.key = string;
    }

    public static MessageKey of(String string) {
        return keyMap.computeIfAbsent(string.toLowerCase().intern(), MessageKey::new);
    }

    public int hashCode() {
        return this.id;
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public MessageKey getMessageKey() {
        return this;
    }
}

