package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.ACFUtil;
import de.jeff_media.replant.acf.commands.CommandExecutionContext;
import de.jeff_media.replant.acf.commands.CommandHelp;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.InvalidCommandArgument;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.commands.MessageKeys;
import de.jeff_media.replant.acf.commands.annotation.Single;
import de.jeff_media.replant.acf.commands.annotation.Split;
import de.jeff_media.replant.acf.commands.annotation.Values;
import de.jeff_media.replant.acf.commands.contexts.ContextResolver;
import de.jeff_media.replant.acf.commands.contexts.IssuerAwareContextResolver;
import de.jeff_media.replant.acf.commands.contexts.IssuerOnlyContextResolver;
import de.jeff_media.replant.acf.commands.contexts.OptionalContextResolver;
import de.jeff_media.replant.acf.locales.MessageKeyProvider;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class CommandContexts<R extends CommandExecutionContext<?, ? extends CommandIssuer>> {
    protected final Map<Class<?>, ContextResolver<?, R>> contextMap = new HashMap();
    protected final CommandManager manager;

    CommandContexts(CommandManager commandManager) {
        this.manager = commandManager;
        this.registerIssuerOnlyContext(CommandIssuer.class, commandExecutionContext -> commandExecutionContext.getIssuer());
        this.registerContext(Short.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, (short)Short.MIN_VALUE, (short)Short.MAX_VALUE).shortValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Short.TYPE, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, (short)Short.MIN_VALUE, (short)Short.MAX_VALUE).shortValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Integer.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, Integer.MIN_VALUE, Integer.MAX_VALUE).intValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Integer.TYPE, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, Integer.MIN_VALUE, Integer.MAX_VALUE).intValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Long.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, Long.MIN_VALUE, Long.MAX_VALUE).longValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Long.TYPE, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, Long.MIN_VALUE, Long.MAX_VALUE).longValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Float.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return Float.valueOf(this.parseAndValidateNumber(string, commandExecutionContext, Float.valueOf(-3.4028235E38f), Float.valueOf(Float.MAX_VALUE)).floatValue());
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Float.TYPE, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return Float.valueOf(this.parseAndValidateNumber(string, commandExecutionContext, Float.valueOf(-3.4028235E38f), Float.valueOf(Float.MAX_VALUE)).floatValue());
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Double.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, -1.7976931348623157E308, Double.MAX_VALUE).doubleValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Double.TYPE, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, -1.7976931348623157E308, Double.MAX_VALUE).doubleValue();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Number.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                return this.parseAndValidateNumber(string, commandExecutionContext, -1.7976931348623157E308, Double.MAX_VALUE);
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(BigDecimal.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                BigDecimal bigDecimal = ACFUtil.parseBigNumber(string, commandExecutionContext.hasFlag("suffixes"));
                this.validateMinMax(commandExecutionContext, bigDecimal);
                return bigDecimal;
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(BigInteger.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            try {
                BigDecimal bigDecimal = ACFUtil.parseBigNumber(string, commandExecutionContext.hasFlag("suffixes"));
                this.validateMinMax(commandExecutionContext, bigDecimal);
                return bigDecimal.toBigIntegerExact();
            }
            catch (NumberFormatException numberFormatException) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string);
            }
        });
        this.registerContext(Boolean.class, commandExecutionContext -> ACFUtil.isTruthy(commandExecutionContext.popFirstArg()));
        this.registerContext(Boolean.TYPE, commandExecutionContext -> ACFUtil.isTruthy(commandExecutionContext.popFirstArg()));
        this.registerContext(Character.TYPE, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            if (string.length() > 1) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_MAX_LENGTH, "{max}", String.valueOf(1));
            }
            return Character.valueOf(string.charAt(0));
        });
        this.registerContext(String.class, commandExecutionContext -> {
            if (commandExecutionContext.hasAnnotation(Values.class)) {
                return commandExecutionContext.popFirstArg();
            }
            String string = commandExecutionContext.isLastArg() && !commandExecutionContext.hasAnnotation(Single.class) ? ACFUtil.join(commandExecutionContext.getArgs()) : commandExecutionContext.popFirstArg();
            Integer n = commandExecutionContext.getFlagValue("minlen", (Integer)null);
            Integer n2 = commandExecutionContext.getFlagValue("maxlen", (Integer)null);
            if (n != null && string.length() < n) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_MIN_LENGTH, "{min}", String.valueOf(n));
            }
            if (n2 != null && string.length() > n2) {
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_MAX_LENGTH, "{max}", String.valueOf(n2));
            }
            return string;
        });
        this.registerContext(String[].class, commandExecutionContext -> {
            List<String> list = commandExecutionContext.getArgs();
            String string = commandExecutionContext.isLastArg() && !commandExecutionContext.hasAnnotation(Single.class) ? ACFUtil.join(list) : commandExecutionContext.popFirstArg();
            String string2 = commandExecutionContext.getAnnotationValue(Split.class, 8);
            if (string2 != null) {
                if (string.isEmpty()) {
                    throw new InvalidCommandArgument();
                }
                return ACFPatterns.getPattern(string2).split(string);
            }
            if (!commandExecutionContext.isLastArg()) {
                ACFUtil.sneaky(new IllegalStateException("Weird Command signature... String[] should be last or @Split"));
            }
            String[] stringArray = list.toArray(new String[0]);
            list.clear();
            return stringArray;
        });
        this.registerContext(Enum.class, commandExecutionContext -> {
            String string = commandExecutionContext.popFirstArg();
            Class<?> clazz = commandExecutionContext.getParam().getType();
            Object e = ACFUtil.simpleMatch(clazz, string);
            if (e == null) {
                List<String> list = ACFUtil.enumNames(clazz);
                throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.PLEASE_SPECIFY_ONE_OF, "{valid}", ACFUtil.join(list, ", "));
            }
            return e;
        });
        this.registerOptionalContext(CommandHelp.class, commandExecutionContext -> {
            String string;
            String string2 = commandExecutionContext.getFirstArg();
            String string3 = commandExecutionContext.getLastArg();
            Integer n = 1;
            List<String> list = null;
            if (string3 != null && ACFUtil.isInteger(string3)) {
                commandExecutionContext.popLastArg();
                n = ACFUtil.parseInt(string3);
                if (n == null) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string3);
                }
                if (!commandExecutionContext.getArgs().isEmpty()) {
                    list = commandExecutionContext.getArgs();
                }
            } else if (string2 != null && ACFUtil.isInteger(string2)) {
                commandExecutionContext.popFirstArg();
                n = ACFUtil.parseInt(string2);
                if (n == null) {
                    throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.MUST_BE_A_NUMBER, "{num}", string2);
                }
                if (!commandExecutionContext.getArgs().isEmpty()) {
                    list = commandExecutionContext.getArgs();
                }
            } else if (!commandExecutionContext.getArgs().isEmpty()) {
                list = commandExecutionContext.getArgs();
            }
            CommandHelp commandHelp = commandManager.generateCommandHelp();
            commandHelp.setPage(n);
            Integer n2 = commandExecutionContext.getFlagValue("perpage", (Integer)null);
            if (n2 != null) {
                commandHelp.setPerPage(n2);
            }
            if (list != null && commandHelp.testExactMatch(string = String.join((CharSequence)" ", list))) {
                return commandHelp;
            }
            commandHelp.setSearch(list);
            return commandHelp;
        });
    }

    @NotNull
    private Number parseAndValidateNumber(String string, R r, Number number, Number number2) {
        Number number3 = ACFUtil.parseNumber(string, ((CommandExecutionContext)r).hasFlag("suffixes"));
        this.validateMinMax(r, number3, number, number2);
        return number3;
    }

    private void validateMinMax(R r, Number number) {
        this.validateMinMax(r, number, null, null);
    }

    private void validateMinMax(R r, Number number, Number number2, Number number3) {
        number2 = ((CommandExecutionContext)r).getFlagValue("min", number2);
        if ((number3 = ((CommandExecutionContext)r).getFlagValue("max", number3)) != null && number.doubleValue() > number3.doubleValue()) {
            throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.PLEASE_SPECIFY_AT_MOST, "{max}", String.valueOf(number3));
        }
        if (number2 != null && number.doubleValue() < number2.doubleValue()) {
            throw new InvalidCommandArgument((MessageKeyProvider)MessageKeys.PLEASE_SPECIFY_AT_LEAST, "{min}", String.valueOf(number2));
        }
    }

    @Deprecated
    public <T> void registerSenderAwareContext(Class<T> clazz, IssuerAwareContextResolver<T, R> issuerAwareContextResolver) {
        this.contextMap.put(clazz, issuerAwareContextResolver);
    }

    public <T> void registerIssuerAwareContext(Class<T> clazz, IssuerAwareContextResolver<T, R> issuerAwareContextResolver) {
        this.contextMap.put(clazz, issuerAwareContextResolver);
    }

    public <T> void registerIssuerOnlyContext(Class<T> clazz, IssuerOnlyContextResolver<T, R> issuerOnlyContextResolver) {
        this.contextMap.put(clazz, issuerOnlyContextResolver);
    }

    public <T> void registerOptionalContext(Class<T> clazz, OptionalContextResolver<T, R> optionalContextResolver) {
        this.contextMap.put(clazz, optionalContextResolver);
    }

    public <T> void registerContext(Class<T> clazz, ContextResolver<T, R> contextResolver) {
        this.contextMap.put(clazz, contextResolver);
    }

    public ContextResolver<?, R> getResolver(Class<?> clazz) {
        Class<?> clazz2 = clazz;
        while (clazz != Object.class) {
            ContextResolver<?, R> contextResolver = this.contextMap.get(clazz);
            if (contextResolver != null) {
                return contextResolver;
            }
            if ((clazz = clazz.getSuperclass()) != null) continue;
        }
        this.manager.log(LogLevel.ERROR, "Could not find context resolver", new IllegalStateException("No context resolver defined for " + clazz2.getName()));
        return null;
    }
}

