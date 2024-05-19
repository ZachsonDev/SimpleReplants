package de.jeff_media.replant.acf.locales;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

class UTF8Control
extends ResourceBundle.Control {
    UTF8Control() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResourceBundle newBundle(String string, Locale locale, String string2, ClassLoader classLoader, boolean bl) {
        String string3 = this.toBundleName(string, locale);
        String string4 = this.toResourceName(string3, "properties");
        PropertyResourceBundle propertyResourceBundle = null;
        InputStream inputStream = null;
        if (bl) {
            URLConnection uRLConnection;
            URL uRL = classLoader.getResource(string4);
            if (uRL != null && (uRLConnection = uRL.openConnection()) != null) {
                uRLConnection.setUseCaches(false);
                inputStream = uRLConnection.getInputStream();
            }
        } else {
            inputStream = classLoader.getResourceAsStream(string4);
        }
        if (inputStream != null) {
            try {
                propertyResourceBundle = new PropertyResourceBundle(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
            finally {
                inputStream.close();
            }
        }
        return propertyResourceBundle;
    }
}

