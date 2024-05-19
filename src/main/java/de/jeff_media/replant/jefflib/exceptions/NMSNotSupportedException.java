package de.jeff_media.replant.jefflib.exceptions;

import com.allatori.annotations.DoNotRename;
import de.jeff_media.replant.jefflib.JeffLib;

@DoNotRename
public class NMSNotSupportedException
extends RuntimeException {
    public NMSNotSupportedException(String string) {
        super(string);
    }

    public NMSNotSupportedException() {
    }

    @Deprecated
    public static void check() {
        if (JeffLib.getNMSHandler() == null) {
            throw new NMSNotSupportedException();
        }
    }
}

