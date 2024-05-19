package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.AnnotationLookups;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.LogLevel;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;

class Annotations<M extends CommandManager>
extends AnnotationLookups {
    public static final int NOTHING = 0;
    public static final int REPLACEMENTS = 1;
    public static final int LOWERCASE = 2;
    public static final int UPPERCASE = 4;
    public static final int NO_EMPTY = 8;
    public static final int DEFAULT_EMPTY = 16;
    private final M manager;
    private final Map<Class<? extends Annotation>, Method> valueMethods = new IdentityHashMap<Class<? extends Annotation>, Method>();
    private final Map<Class<? extends Annotation>, Void> noValueAnnotations = new IdentityHashMap<Class<? extends Annotation>, Void>();

    Annotations(M m) {
        this.manager = m;
    }

    @Override
    String getAnnotationValue(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz, int n) {
        Annotation annotation = Annotations.getAnnotationRecursive(annotatedElement, clazz, new HashSet<Annotation>());
        String string = null;
        if (annotation != null) {
            Method method = this.valueMethods.get(clazz);
            if (this.noValueAnnotations.containsKey(clazz)) {
                string = "";
            } else {
                try {
                    if (method == null) {
                        method = clazz.getMethod("value", new Class[0]);
                        method.setAccessible(true);
                        this.valueMethods.put(clazz, method);
                    }
                    string = (String)method.invoke((Object)annotation, new Object[0]);
                }
                catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException reflectiveOperationException) {
                    if (!(reflectiveOperationException instanceof NoSuchMethodException)) {
                        ((CommandManager)this.manager).log(LogLevel.ERROR, "Error getting annotation value", reflectiveOperationException);
                    }
                    this.noValueAnnotations.put(clazz, null);
                    string = "";
                }
            }
        }
        if (string == null) {
            if (Annotations.hasOption(n, 16)) {
                string = "";
            } else {
                return null;
            }
        }
        if (Annotations.hasOption(n, 1)) {
            string = ((CommandManager)this.manager).getCommandReplacements().replace(string);
        }
        if (Annotations.hasOption(n, 2)) {
            string = string.toLowerCase(((CommandManager)this.manager).getLocales().getDefaultLocale());
        } else if (Annotations.hasOption(n, 4)) {
            string = string.toUpperCase(((CommandManager)this.manager).getLocales().getDefaultLocale());
        }
        if (string.isEmpty() && Annotations.hasOption(n, 8)) {
            string = null;
        }
        return string;
    }

    private static Annotation getAnnotationRecursive(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz, Collection<Annotation> collection) {
        if (annotatedElement.isAnnotationPresent(clazz)) {
            return annotatedElement.getAnnotation(clazz);
        }
        for (Annotation annotation : annotatedElement.getDeclaredAnnotations()) {
            if (annotation.annotationType().getPackage().getName().startsWith("java.")) continue;
            if (collection.contains(annotation)) {
                return null;
            }
            collection.add(annotation);
            Annotation annotation2 = Annotations.getAnnotationRecursive(annotation.annotationType(), clazz, collection);
            if (annotation2 == null) continue;
            return annotation2;
        }
        return null;
    }

    private static boolean hasOption(int n, int n2) {
        return (n & n2) == n2;
    }
}

