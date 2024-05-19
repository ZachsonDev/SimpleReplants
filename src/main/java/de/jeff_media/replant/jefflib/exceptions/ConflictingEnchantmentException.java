package de.jeff_media.replant.jefflib.exceptions;

import com.allatori.annotations.DoNotRename;

@DoNotRename
public class ConflictingEnchantmentException
extends Exception {
    public ConflictingEnchantmentException(String string) {
        super(string);
    }
}

