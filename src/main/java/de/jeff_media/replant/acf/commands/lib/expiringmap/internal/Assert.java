package de.jeff_media.replant.acf.commands.lib.expiringmap.internal;

import java.util.NoSuchElementException;

public final class Assert {
    private Assert() {
    }

    public static <T> T notNull(T t, String string) {
        if (t == null) {
            throw new NullPointerException(string + " cannot be null");
        }
        return t;
    }

    public static void operation(boolean bl, String string) {
        if (!bl) {
            throw new UnsupportedOperationException(string);
        }
    }

    public static void state(boolean bl, String string, Object ... objectArray) {
        if (!bl) {
            throw new IllegalStateException(String.format(string, objectArray));
        }
    }

    public static void element(Object object, Object object2) {
        if (object == null) {
            throw new NoSuchElementException(object2.toString());
        }
    }
}

