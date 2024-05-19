package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.CommandIssuer;
import java.util.Locale;

public interface IssuerLocaleChangedCallback<I extends CommandIssuer> {
    public void onIssuerLocaleChange(I var1, Locale var2, Locale var3);
}

