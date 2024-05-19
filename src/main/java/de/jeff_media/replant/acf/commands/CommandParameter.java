package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.Annotations;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.CommandExecutionContext;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.InvalidCommandContextException;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.annotation.CommandPermission;
import de.jeff_media.replant.acf.commands.annotation.Conditions;
import de.jeff_media.replant.acf.commands.annotation.Default;
import de.jeff_media.replant.acf.commands.annotation.Description;
import de.jeff_media.replant.acf.commands.annotation.Flags;
import de.jeff_media.replant.acf.commands.annotation.Name;
import de.jeff_media.replant.acf.commands.annotation.Optional;
import de.jeff_media.replant.acf.commands.annotation.Single;
import de.jeff_media.replant.acf.commands.annotation.Syntax;
import de.jeff_media.replant.acf.commands.annotation.Values;
import de.jeff_media.replant.acf.commands.contexts.ContextResolver;
import de.jeff_media.replant.acf.commands.contexts.IssuerAwareContextResolver;
import de.jeff_media.replant.acf.commands.contexts.IssuerOnlyContextResolver;
import de.jeff_media.replant.acf.commands.contexts.OptionalContextResolver;
import de.jeff_media.replant.acf.locales.MessageKey;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandParameter<CEC extends CommandExecutionContext<CEC, ? extends CommandIssuer>> {
    private final Parameter parameter;
    private final Class<?> type;
    private final String name;
    private final CommandManager manager;
    private final int paramIndex;
    private ContextResolver<?, CEC> resolver;
    private boolean optional;
    private Set<String> permissions = new HashSet<String>();
    private String permission;
    private String description;
    private String defaultValue;
    private String syntax;
    private String conditions;
    private boolean requiresInput;
    private boolean commandIssuer;
    private String[] values;
    private Map<String, String> flags;
    private boolean canConsumeInput;
    private boolean optionalResolver;
    boolean consumesRest;
    private boolean isLast;
    private boolean isOptionalInput;
    private CommandParameter<CEC> nextParam;

    public CommandParameter(RegisteredCommand<CEC> registeredCommand, Parameter parameter, int n, boolean bl) {
        this.parameter = parameter;
        this.isLast = bl;
        this.type = parameter.getType();
        this.manager = registeredCommand.manager;
        this.paramIndex = n;
        Annotations annotations = this.manager.getAnnotations();
        String string = annotations.getAnnotationValue(parameter, Name.class, 1);
        this.name = string != null ? string : parameter.getName();
        this.defaultValue = annotations.getAnnotationValue(parameter, Default.class, 1 | (this.type != String.class ? 8 : 0));
        this.description = annotations.getAnnotationValue(parameter, Description.class, 17);
        this.conditions = annotations.getAnnotationValue(parameter, Conditions.class, 9);
        this.resolver = this.manager.getCommandContexts().getResolver(this.type);
        if (this.resolver == null) {
            ACFUtil.sneaky(new InvalidCommandContextException("Parameter " + this.type.getSimpleName() + " of " + registeredCommand + " has no applicable context resolver"));
        }
        this.optional = annotations.hasAnnotation(parameter, Optional.class) || this.defaultValue != null || bl && this.type == String[].class;
        this.permission = annotations.getAnnotationValue(parameter, CommandPermission.class, 9);
        this.optionalResolver = this.isOptionalResolver(this.resolver);
        this.requiresInput = !this.optional && !this.optionalResolver;
        this.commandIssuer = n == 0 && this.manager.isCommandIssuer(this.type);
        this.canConsumeInput = !this.commandIssuer && !(this.resolver instanceof IssuerOnlyContextResolver);
        this.consumesRest = bl && (this.type == String.class && !annotations.hasAnnotation(parameter, Single.class) || this.type == String[].class);
        this.values = annotations.getAnnotationValues((AnnotatedElement)parameter, Values.class, 9);
        this.syntax = null;
        boolean bl2 = this.isOptionalInput = !this.requiresInput && this.canConsumeInput;
        if (!this.commandIssuer) {
            this.syntax = annotations.getAnnotationValue(parameter, Syntax.class);
        }
        this.flags = new HashMap<String, String>();
        String string2 = annotations.getAnnotationValue(parameter, Flags.class, 9);
        if (string2 != null) {
            this.parseFlags(string2);
        }
        this.inheritContextFlags(registeredCommand.scope);
        this.computePermissions();
    }

    private void inheritContextFlags(BaseCommand baseCommand) {
        if (!baseCommand.contextFlags.isEmpty()) {
            Class<?> clazz = this.type;
            do {
                this.parseFlags(baseCommand.contextFlags.get(clazz));
            } while ((clazz = clazz.getSuperclass()) != null);
        }
        if (baseCommand.parentCommand != null) {
            this.inheritContextFlags(baseCommand.parentCommand);
        }
    }

    private void parseFlags(String string) {
        if (string != null) {
            for (String string2 : ACFPatterns.COMMA.split(this.manager.getCommandReplacements().replace(string))) {
                String[] stringArray = ACFPatterns.EQUALS.split(string2, 2);
                if (this.flags.containsKey(stringArray[0])) continue;
                this.flags.put(stringArray[0], stringArray.length > 1 ? stringArray[1] : null);
            }
        }
    }

    private void computePermissions() {
        this.permissions.clear();
        if (this.permission != null && !this.permission.isEmpty()) {
            this.permissions.addAll(Arrays.asList(ACFPatterns.COMMA.split(this.permission)));
        }
    }

    private boolean isOptionalResolver(ContextResolver<?, CEC> contextResolver) {
        return contextResolver instanceof IssuerAwareContextResolver || contextResolver instanceof IssuerOnlyContextResolver || contextResolver instanceof OptionalContextResolver;
    }

    public Parameter getParameter() {
        return this.parameter;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName(CommandIssuer commandIssuer) {
        String string = this.manager.getLocales().getOptionalMessage(commandIssuer, MessageKey.of("acf-core.parameter." + this.name.toLowerCase()));
        return string != null ? string : this.name;
    }

    public CommandManager getManager() {
        return this.manager;
    }

    public int getParamIndex() {
        return this.paramIndex;
    }

    public ContextResolver<?, CEC> getResolver() {
        return this.resolver;
    }

    public void setResolver(ContextResolver<?, CEC> contextResolver) {
        this.resolver = contextResolver;
    }

    public boolean isOptionalInput() {
        return this.isOptionalInput;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public void setOptional(boolean bl) {
        this.optional = bl;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String string) {
        this.description = string;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String string) {
        this.defaultValue = string;
    }

    public boolean isCommandIssuer() {
        return this.commandIssuer;
    }

    public void setCommandIssuer(boolean bl) {
        this.commandIssuer = bl;
    }

    public String[] getValues() {
        return this.values;
    }

    public void setValues(String[] stringArray) {
        this.values = stringArray;
    }

    public Map<String, String> getFlags() {
        return this.flags;
    }

    public void setFlags(Map<String, String> map) {
        this.flags = map;
    }

    public boolean canConsumeInput() {
        return this.canConsumeInput;
    }

    public void setCanConsumeInput(boolean bl) {
        this.canConsumeInput = bl;
    }

    public void setOptionalResolver(boolean bl) {
        this.optionalResolver = bl;
    }

    public boolean isOptionalResolver() {
        return this.optionalResolver;
    }

    public boolean requiresInput() {
        return this.requiresInput;
    }

    public void setRequiresInput(boolean bl) {
        this.requiresInput = bl;
    }

    public String getSyntax() {
        return this.getSyntax(null);
    }

    public String getSyntax(CommandIssuer commandIssuer) {
        if (this.commandIssuer) {
            return null;
        }
        if (this.syntax == null) {
            if (this.isOptionalInput) {
                return "[" + this.getDisplayName(commandIssuer) + "]";
            }
            if (this.requiresInput) {
                return "<" + this.getDisplayName(commandIssuer) + ">";
            }
        }
        return this.syntax;
    }

    public void setSyntax(String string) {
        this.syntax = string;
    }

    public String getConditions() {
        return this.conditions;
    }

    public void setConditions(String string) {
        this.conditions = string;
    }

    public Set<String> getRequiredPermissions() {
        return this.permissions;
    }

    public void setNextParam(CommandParameter<CEC> commandParameter) {
        this.nextParam = commandParameter;
    }

    public CommandParameter<CEC> getNextParam() {
        return this.nextParam;
    }

    public boolean canExecuteWithoutInput() {
        return !(this.canConsumeInput && !this.isOptionalInput() || this.nextParam != null && !this.nextParam.canExecuteWithoutInput());
    }

    public boolean isLast() {
        return this.isLast;
    }
}

