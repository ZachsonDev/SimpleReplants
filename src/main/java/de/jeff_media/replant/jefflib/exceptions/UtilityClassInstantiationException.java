package de.jeff_media.replant.jefflib.exceptions;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.ClassUtils;

@DoNotRename
public class UtilityClassInstantiationException
extends RuntimeException {
    public UtilityClassInstantiationException() {
        super(ClassUtils.getCurrentClassName(1) + " is a utility class and cannot be instantiated.");
    }
}

