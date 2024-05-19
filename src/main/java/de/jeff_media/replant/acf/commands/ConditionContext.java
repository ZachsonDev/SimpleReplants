package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import java.util.HashMap;
import java.util.Map;

public class ConditionContext<I extends CommandIssuer> {
    private final I issuer;
    private final String config;
    private final Map<String, String> configs;

    ConditionContext(I i, String string) {
        this.issuer = i;
        this.config = string;
        this.configs = new HashMap<String, String>();
        if (string != null) {
            for (String string2 : ACFPatterns.COMMA.split(string)) {
                String[] stringArray = ACFPatterns.EQUALS.split(string2, 2);
                this.configs.put(stringArray[0], stringArray.length > 1 ? stringArray[1] : null);
            }
        }
    }

    public I getIssuer() {
        return this.issuer;
    }

    public String getConfig() {
        return this.config;
    }

    public boolean hasConfig(String string) {
        return this.configs.containsKey(string);
    }

    public String getConfigValue(String string, String string2) {
        return this.configs.getOrDefault(string, string2);
    }

    public Integer getConfigValue(String string, Integer n) {
        return ACFUtil.parseInt(this.configs.get(string), n);
    }
}

