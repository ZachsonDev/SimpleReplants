package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.JeffLib;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class EnumUtils {
    private static final Map<Class<? extends Enum<?>>, Set<String>> ENUM_CACHE = new HashMap();
    private static final Map<Class<? extends Enum<?>>, List<? extends Enum<?>>> ENUM_ARRAY_CACHE = new HashMap();
    private static final Map<Class<? extends Enum<?>>, EnumMap<?, ?>> NEXT_ENUMS = new HashMap();

    public static <E extends Enum<E>> EnumSet<E> getEnumsFromListAsEnumSet(Class<E> clazz, List<String> list) {
        return EnumUtils.getEnumsFromList(clazz, list, Collectors.toCollection(() -> EnumSet.noneOf(clazz)));
    }

    public static <E extends Enum<E>> Set<E> getEnumsFromListAsSet(Class<E> clazz, List<String> list) {
        return EnumUtils.getEnumsFromList(clazz, list, Collectors.toSet());
    }

    public static <E extends Enum<E>, C extends Collection<E>> C getEnumsFromList(Class<E> clazz, List<String> list, Collector<? super E, ?, C> collector) {
        return (C)((Collection)list.stream().map(string -> {
            Optional optional = EnumUtils.getIfPresent(clazz, clazz.getName().startsWith("org.bukkit") ? string.toUpperCase(Locale.ROOT) : string);
            if (!optional.isPresent()) {
                JeffLib.getPlugin().getLogger().severe("Could not find " + clazz.getSimpleName() + ": '" + string + "'");
                return null;
            }
            return (Enum)optional.get();
        }).filter(Objects::nonNull).collect(collector));
    }

    public static <E extends Enum<E>> Optional<E> getIfPresent(Class<E> clazz, String string) {
        Set set = ENUM_CACHE.computeIfAbsent(clazz, EnumUtils::toStringSet);
        return Optional.ofNullable(set.contains(string) ? (Object)Enum.valueOf(clazz, string) : null);
    }

    private static Set<String> toStringSet(Class<? extends Enum<?>> clazz) {
        return Arrays.stream(clazz.getEnumConstants()).map(Enum::toString).collect(Collectors.toSet());
    }

    public static <E extends Enum<E>> EnumSet<E> getEnumsFromRegexList(Class<E> clazz, List<String> list) {
        EnumSet<Enum> enumSet = EnumSet.noneOf(clazz);
        for (String string : list) {
            Pattern pattern = Pattern.compile(string);
            for (Enum enum_ : (Enum[])clazz.getEnumConstants()) {
                String string2;
                if (enumSet.contains(enum_) || !pattern.matcher(string2 = enum_.name()).matches()) continue;
                enumSet.add(enum_);
            }
        }
        return enumSet;
    }

    public static <E extends Enum<E>> E getRandomElement(Class<E> clazz) {
        List<E> list = EnumUtils.getValues(clazz);
        return (E)((Enum)list.get(JeffLib.getThreadLocalRandom().nextInt(list.size())));
    }

    public static <E extends Enum<E>> List<E> getValues(Class<E> clazz) {
        List<Enum> list = ENUM_ARRAY_CACHE.get(clazz);
        if (list == null) {
            list = Collections.unmodifiableList(Arrays.asList((Enum[])clazz.getEnumConstants()));
            ENUM_ARRAY_CACHE.put(clazz, list);
        }
        return list;
    }

    public static <E extends Enum<E>> E getNextElement(E e) {
        Class<?> clazz = e.getClass();
        EnumMap enumMap = NEXT_ENUMS.computeIfAbsent(clazz, clazz2 -> new EnumMap(clazz));
        Enum enum_ = (Enum)enumMap.get(e);
        if (enum_ == null) {
            int n = e.ordinal();
            List<?> list = EnumUtils.getValues(clazz);
            enum_ = (Enum)list.get((n + 1) % list.size());
            enumMap.put(e, enum_);
        }
        return (E)enum_;
    }

    private EnumUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

