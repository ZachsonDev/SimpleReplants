package de.jeff_media.replant.jefflib.internal.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierNickname;

@Documented
@Retention(value=RetentionPolicy.CLASS)
@TypeQualifierNickname
public @interface NMS {
    public String value() default "";
}

