package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.BaseCommand;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.regex.Pattern;

abstract class AnnotationLookups {
    AnnotationLookups() {
    }

    boolean hasAnnotation(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz) {
        return this.getAnnotationValue(annotatedElement, clazz, 0) != null;
    }

    boolean hasAnnotation(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz, boolean bl) {
        return this.getAnnotationValue(annotatedElement, clazz, 0 | (bl ? 0 : 8)) != null;
    }

    String[] getAnnotationValues(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz) {
        return this.getAnnotationValues(annotatedElement, clazz, ACFPatterns.PIPE, 1);
    }

    String[] getAnnotationValues(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz, Pattern pattern) {
        return this.getAnnotationValues(annotatedElement, clazz, pattern, 1);
    }

    String[] getAnnotationValues(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz, int n) {
        return this.getAnnotationValues(annotatedElement, clazz, ACFPatterns.PIPE, n);
    }

    String[] getAnnotationValues(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz, Pattern pattern, int n) {
        String string = this.getAnnotationValue(annotatedElement, clazz, n);
        if (string == null) {
            return null;
        }
        return pattern.split(string);
    }

    String getAnnotationValue(AnnotatedElement annotatedElement, Class<? extends Annotation> clazz) {
        return this.getAnnotationValue(annotatedElement, clazz, 1);
    }

    abstract String getAnnotationValue(AnnotatedElement var1, Class<? extends Annotation> var2, int var3);

    <T extends Annotation> T getAnnotationFromClass(Class<?> clazz, Class<T> clazz2) {
        while (clazz != null && BaseCommand.class.isAssignableFrom(clazz)) {
            T t = clazz.getAnnotation(clazz2);
            if (t != null) {
                return t;
            }
            for (Class<?> clazz3 = clazz.getSuperclass(); clazz3 != null && BaseCommand.class.isAssignableFrom(clazz3); clazz3 = clazz3.getSuperclass()) {
                t = clazz3.getAnnotation(clazz2);
                if (t == null) continue;
                return t;
            }
            clazz = clazz.getEnclosingClass();
        }
        return null;
    }
}

