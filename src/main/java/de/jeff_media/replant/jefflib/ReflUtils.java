package de.jeff_media.replant.jefflib;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.jeff_media.replant.jefflib.ServerUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReflUtils {
    private static final Map<String, Class<?>> CLASSES = new HashMap();
    private static final Table<Class<?>, String, Method> METHODS_NO_ARGS = HashBasedTable.create();
    private static final Table<Class<?>, MethodParameters, Method> METHODS_WITH_ARGS = HashBasedTable.create();
    private static final Table<Class<?>, String, Field> FIELDS = HashBasedTable.create();
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTORS_NO_ARGS = new HashMap();
    private static final Table<Class<?>, Parameters, Constructor<?>> CONSTRUCTOR_WITH_ARGS = HashBasedTable.create();
    private static String nmsVersion;

    @Deprecated
    public static Class<?> getNMSClass(String string) {
        return ReflUtils.getClass("net.minecraft.server." + ReflUtils.getNMSVersion() + "." + string);
    }

    @Nullable
    public static Class<?> getClass(@NotNull String string) {
        if (CLASSES.containsKey(string)) {
            return CLASSES.get(string);
        }
        try {
            Class<?> clazz = Class.forName(string);
            CLASSES.put(string, clazz);
            return clazz;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    @NotNull
    public static String getNMSVersion() {
        if (nmsVersion == null) {
            nmsVersion = ServerUtils.isRunningMockBukkit() ? "vMockBukkit" : Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
        return nmsVersion;
    }

    public static Class<?> getOBCClass(String string) {
        return ReflUtils.getClass("org.bukkit.craftbukkit." + ReflUtils.getNMSVersion() + "." + string);
    }

    public static boolean isClassCached(String string) {
        return CLASSES.containsKey(string);
    }

    public static boolean isMethodCached(@NotNull Class<?> clazz, @NotNull String string) {
        return METHODS_NO_ARGS.contains(clazz, (Object)string);
    }

    @Nullable
    public static Method getMethod(@NotNull Class<?> clazz, @NotNull String string) {
        if (METHODS_NO_ARGS.contains(clazz, (Object)string)) {
            return (Method)METHODS_NO_ARGS.get(clazz, (Object)string);
        }
        try {
            Method method = clazz.getDeclaredMethod(string, new Class[0]);
            method.setAccessible(true);
            METHODS_NO_ARGS.put(clazz, (Object)string, (Object)method);
            return method;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    public static boolean isMethodCached(@NotNull Class<?> clazz, @NotNull String string, Class<?> ... classArray) {
        return METHODS_WITH_ARGS.contains(clazz, (Object)new MethodParameters(string, classArray));
    }

    public static Method getMethod(@NotNull Class<?> clazz, @NotNull String string, Class<?> ... classArray) {
        MethodParameters methodParameters = new MethodParameters(string, classArray);
        if (METHODS_WITH_ARGS.contains(clazz, (Object)methodParameters)) {
            return (Method)METHODS_WITH_ARGS.get(clazz, (Object)methodParameters);
        }
        try {
            Method method = clazz.getDeclaredMethod(string, classArray);
            method.setAccessible(true);
            METHODS_WITH_ARGS.put(clazz, (Object)methodParameters, (Object)method);
            return method;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    public static void setFieldValue(@NotNull Object object, @NotNull String string, @Nullable Object object2) {
        ReflUtils.setFieldValue(object.getClass(), object, string, object2);
    }

    public static void setFieldValue(@NotNull Class<?> clazz, @Nullable Object object, @NotNull String string, @Nullable Object object2) {
        try {
            Field field = ReflUtils.getField(clazz, string);
            Objects.requireNonNull(field).set(object, object2);
        }
        catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

    public static Field getField(@NotNull Class<?> clazz, @NotNull String string) {
        if (FIELDS.contains(clazz, (Object)string)) {
            return (Field)FIELDS.get(clazz, (Object)string);
        }
        try {
            Field field = clazz.getDeclaredField(string);
            field.setAccessible(true);
            FIELDS.put(clazz, (Object)string, (Object)field);
            return field;
        }
        catch (NoSuchFieldException noSuchFieldException) {
            return null;
        }
    }

    public static Object getFieldValue(@NotNull Class<?> clazz, @NotNull String string, @Nullable Object object) {
        Field field = ReflUtils.getField(clazz, string);
        try {
            return field.get(object);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
        }
    }

    public static boolean isFieldCached(@NotNull Class<?> clazz, @NotNull String string) {
        return FIELDS.contains(clazz, (Object)string);
    }

    public static boolean isConstructorCached(@NotNull Class<?> clazz) {
        return CONSTRUCTORS_NO_ARGS.containsKey(clazz);
    }

    public static boolean isConstructorCached(@NotNull Class<?> clazz, Class<?> ... classArray) {
        return CONSTRUCTOR_WITH_ARGS.contains(clazz, (Object)new Parameters((Class[])classArray));
    }

    public static Constructor<?> getConstructor(@NotNull Class<?> clazz) {
        if (CONSTRUCTORS_NO_ARGS.containsKey(clazz)) {
            return CONSTRUCTORS_NO_ARGS.get(clazz);
        }
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            CONSTRUCTORS_NO_ARGS.put(clazz, constructor);
            return constructor;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(@NotNull Class<?> clazz, Class<?> ... classArray) {
        Parameters parameters = new Parameters((Class[])classArray);
        if (CONSTRUCTOR_WITH_ARGS.contains(clazz, (Object)parameters)) {
            return (Constructor)CONSTRUCTOR_WITH_ARGS.get(clazz, (Object)parameters);
        }
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(classArray);
            constructor.setAccessible(true);
            CONSTRUCTOR_WITH_ARGS.put(clazz, (Object)parameters, constructor);
            return constructor;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    private ReflUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final class MethodParameters
    extends Parameters {
        @NotNull
        private final String name;

        MethodParameters(@NotNull String string, Class<?> ... classArray) {
            super((Class[])classArray);
            this.name = string;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            if (!super.equals(object)) {
                return false;
            }
            MethodParameters methodParameters = (MethodParameters)object;
            return this.name.equals(methodParameters.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.name);
        }
    }

    private static class Parameters {
        @NotNull
        private final Class<?>[] parameterClazzes;

        private Parameters(Class<?> ... classArray) {
            this.parameterClazzes = classArray;
        }

        public int hashCode() {
            return Arrays.hashCode(this.parameterClazzes);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Parameters parameters = (Parameters)object;
            return Arrays.equals(this.parameterClazzes, parameters.parameterClazzes);
        }
    }
}

