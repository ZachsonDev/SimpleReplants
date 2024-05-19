package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandCompletionTextLookupException;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandCompletionContext<I extends CommandIssuer> {
    private final RegisteredCommand command;
    protected final I issuer;
    private final String input;
    private final String config;
    private final Map<String, String> configs = new HashMap<String, String>();
    private final List<String> args;

    CommandCompletionContext(RegisteredCommand registeredCommand, I i, String string, String string2, String[] stringArray) {
        this.command = registeredCommand;
        this.issuer = i;
        this.input = string;
        if (string2 != null) {
            String[] stringArray2;
            for (String string3 : stringArray2 = ACFPatterns.COMMA.split(string2)) {
                String[] stringArray3 = ACFPatterns.EQUALS.split(string3, 2);
                this.configs.put(stringArray3[0].toLowerCase(Locale.ENGLISH), stringArray3.length > 1 ? stringArray3[1] : null);
            }
            this.config = stringArray2[0];
        } else {
            this.config = null;
        }
        this.args = Arrays.asList(stringArray);
    }

    public Map<String, String> getConfigs() {
        return this.configs;
    }

    public String getConfig(String string) {
        return this.getConfig(string, null);
    }

    public String getConfig(String string, String string2) {
        return this.configs.getOrDefault(string.toLowerCase(Locale.ENGLISH), string2);
    }

    public boolean hasConfig(String string) {
        return this.configs.containsKey(string.toLowerCase(Locale.ENGLISH));
    }

    public <T> T getContextValue(Class<? extends T> clazz) {
        return this.getContextValue(clazz, null);
    }

    public <T> T getContextValue(Class<? extends T> clazz, Integer n) {
        String string = null;
        if (n != null) {
            if (n >= this.command.parameters.length) {
                throw new IllegalArgumentException("Param index is higher than number of parameters");
            }
            CommandParameter commandParameter = this.command.parameters[n];
            Class<?> clazz2 = commandParameter.getType();
            if (!clazz.isAssignableFrom(clazz2)) {
                throw new IllegalArgumentException(commandParameter.getName() + ":" + clazz2.getName() + " can not satisfy " + clazz.getName());
            }
            string = commandParameter.getName();
        } else {
            CommandParameter<CEC>[] commandParameterArray = this.command.parameters;
            for (int i = 0; i < commandParameterArray.length; ++i) {
                CommandParameter commandParameter = commandParameterArray[i];
                if (!clazz.isAssignableFrom(commandParameter.getType())) continue;
                n = i;
                string = commandParameter.getName();
                break;
            }
            if (n == null) {
                throw new IllegalStateException("Can not find any parameter that can satisfy " + clazz.getName());
            }
        }
        return this.getContextValueByName(clazz, string);
    }

    public <T> T getContextValueByName(Class<? extends T> clazz, String string) {
        Map<String, Object> map = this.command.resolveContexts((CommandIssuer)this.issuer, this.args, string);
        if (map == null || !map.containsKey(string)) {
            ACFUtil.sneaky(new CommandCompletionTextLookupException());
        }
        return (T)map.get(string);
    }

    public CommandIssuer getIssuer() {
        return this.issuer;
    }

    public String getInput() {
        return this.input;
    }

    public String getConfig() {
        return this.config;
    }

    public boolean isAsync() {
        return CommandManager.getCurrentCommandOperationContext().isAsync();
    }
}

