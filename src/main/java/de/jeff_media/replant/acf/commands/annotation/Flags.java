package de.jeff_media.replant.acf.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface Flags {
    public String value();
}

