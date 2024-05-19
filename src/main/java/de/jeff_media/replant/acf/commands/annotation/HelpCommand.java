package de.jeff_media.replant.acf.commands.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface HelpCommand {
    public String value() default "help|?|-help|-h|-?";
}

