package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.locales.MessageKey;
import de.jeff_media.replant.acf.locales.MessageKeyProvider;

public class InvalidCommandArgument
extends RuntimeException {
    final boolean showSyntax;
    final MessageKey key;
    final String[] replacements;

    public InvalidCommandArgument() {
        this(null, true);
    }

    public InvalidCommandArgument(boolean bl) {
        this(null, bl);
    }

    public InvalidCommandArgument(MessageKeyProvider messageKeyProvider, String ... stringArray) {
        this(messageKeyProvider.getMessageKey(), stringArray);
    }

    public InvalidCommandArgument(MessageKey messageKey, String ... stringArray) {
        this(messageKey, true, stringArray);
    }

    public InvalidCommandArgument(MessageKeyProvider messageKeyProvider, boolean bl, String ... stringArray) {
        this(messageKeyProvider.getMessageKey(), bl, stringArray);
    }

    public InvalidCommandArgument(MessageKey messageKey, boolean bl, String ... stringArray) {
        super(messageKey.getKey(), null, false, false);
        this.showSyntax = bl;
        this.key = messageKey;
        this.replacements = stringArray;
    }

    public InvalidCommandArgument(String string) {
        this(string, true);
    }

    public InvalidCommandArgument(String string, boolean bl) {
        super(string, null, false, false);
        this.showSyntax = bl;
        this.replacements = null;
        this.key = null;
    }
}

