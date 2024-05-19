package de.jeff_media.replant.jefflib.internal.cherokee;

import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Validate {
    private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified inclusive range of %s to %s";
    private static final String DEFAULT_MATCHES_PATTERN_EX = "The string %s does not match the pattern %s";

    @Contract(value="false, _ -> fail")
    public static void isTrue(boolean bl, @NotNull String string) {
        if (!bl) {
            throw new IllegalArgumentException(string);
        }
    }

    @Contract(value="false -> fail")
    public static void isTrue(boolean bl) {
        throw new IllegalArgumentException();
    }

    public static <T> void inclusiveBetween(T t, T t2, Comparable<T> comparable) {
        if (comparable.compareTo(t) < 0 || comparable.compareTo(t2) > 0) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, comparable, t, t2));
        }
    }

    public static void matchesPattern(CharSequence charSequence, String string) {
        if (!Pattern.matches(string, charSequence)) {
            throw new IllegalArgumentException(String.format(DEFAULT_MATCHES_PATTERN_EX, charSequence, string));
        }
    }

    public static void matchesPattern(CharSequence charSequence, String string, String string2, Object ... objectArray) {
        if (!Pattern.matches(string, charSequence)) {
            throw new IllegalArgumentException(String.format(string2, objectArray));
        }
    }

    @Contract(value="null -> fail", pure=true)
    public static void notNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
    }

    @Contract(value="null, _ -> fail", pure=true)
    public static void notNull(Object object, String string) {
        if (object == null) {
            throw new IllegalArgumentException(string);
        }
    }
}

