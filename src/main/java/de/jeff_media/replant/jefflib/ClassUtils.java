package de.jeff_media.replant.jefflib;

import de.jeff_media.replant.jefflib.ReflUtils;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.jetbrains.annotations.NotNull;

public final class ClassUtils {
    public static boolean exists(@NotNull String string) {
        if (ReflUtils.isClassCached(string)) {
            return true;
        }
        try {
            Class.forName(string);
            return true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    public static int getCurrentLineNumber() {
        return ClassUtils.getCurrentLineNumber(1);
    }

    public static int getCurrentLineNumber(int n) {
        return Thread.currentThread().getStackTrace()[2 + n].getLineNumber();
    }

    public static String getCurrentClassName() {
        return ClassUtils.getCurrentClassName(1);
    }

    public static String getCurrentClassName(int n) {
        return Thread.currentThread().getStackTrace()[2 + n].getClassName();
    }

    public static Class<?> getCurrentClass() {
        return ClassUtils.getCurrentClass(1);
    }

    public static Class<?> getCurrentClass(int n) {
        try {
            return Class.forName(Thread.currentThread().getStackTrace()[2 + n].getClassName());
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new RuntimeException(classNotFoundException);
        }
    }

    public static String getCurrentClassFileName() {
        return ClassUtils.getCurrentClassFileName(1);
    }

    public static String getCurrentClassFileName(int n) {
        return Thread.currentThread().getStackTrace()[2 + n].getFileName();
    }

    public static String getCurrentMethodName() {
        return ClassUtils.getCurrentMethodName(1);
    }

    public static String getCurrentMethodName(int n) {
        return Thread.currentThread().getStackTrace()[2 + n].getMethodName();
    }

    @NotNull
    public static List<String> listAllClasses() {
        return ClassUtils.listAllClasses(ClassUtils.class);
    }

    @NotNull
    public static List<String> listAllClasses(@NotNull Class<?> clazz) {
        Cloneable cloneable;
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return Collections.emptyList();
        }
        URL uRL = codeSource.getLocation();
        ZipInputStream zipInputStream = new ZipInputStream(uRL.openStream());
        try {
            ArrayList<String> arrayList = new ArrayList<String>();
            while ((cloneable = zipInputStream.getNextEntry()) != null) {
                String string;
                if (((ZipEntry)cloneable).isDirectory() || !(string = ((ZipEntry)cloneable).getName()).endsWith(".class")) continue;
                arrayList.add(string.replace('/', '.').substring(0, string.length() - 6));
            }
            cloneable = arrayList;
        }
        catch (Throwable throwable) {
            try {
                try {
                    zipInputStream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException iOException) {
                return Collections.emptyList();
            }
        }
        zipInputStream.close();
        return cloneable;
    }

    private ClassUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

