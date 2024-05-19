package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.CommandExecutionContext;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandOperationContext;
import de.jeff_media.replant.acf.commands.ConditionContext;
import de.jeff_media.replant.acf.commands.InvalidCommandArgument;
import de.jeff_media.replant.acf.commands.LogLevel;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.lib.util.Table;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class CommandConditions<I extends CommandIssuer, CEC extends CommandExecutionContext<CEC, I>, CC extends ConditionContext<I>> {
    private CommandManager manager;
    private Map<String, Condition<I>> conditions = new HashMap<String, Condition<I>>();
    private Table<Class<?>, String, ParameterCondition<?, ?, ?>> paramConditions = new Table();

    CommandConditions(CommandManager commandManager) {
        this.manager = commandManager;
    }

    public Condition<I> addCondition(@NotNull String string, @NotNull Condition<I> condition) {
        return this.conditions.put(string.toLowerCase(Locale.ENGLISH), condition);
    }

    public <P> ParameterCondition addCondition(Class<P> clazz, @NotNull String string, @NotNull ParameterCondition<P, CEC, I> parameterCondition) {
        return this.paramConditions.put(clazz, string.toLowerCase(Locale.ENGLISH), parameterCondition);
    }

    void validateConditions(CommandOperationContext commandOperationContext) {
        RegisteredCommand registeredCommand = commandOperationContext.getRegisteredCommand();
        this.validateConditions(registeredCommand.conditions, commandOperationContext);
        this.validateConditions(registeredCommand.scope, commandOperationContext);
    }

    private void validateConditions(BaseCommand baseCommand, CommandOperationContext commandOperationContext) {
        this.validateConditions(baseCommand.conditions, commandOperationContext);
        if (baseCommand.parentCommand != null) {
            this.validateConditions(baseCommand.parentCommand, commandOperationContext);
        }
    }

    private void validateConditions(String string, CommandOperationContext commandOperationContext) {
        if (string == null) {
            return;
        }
        string = this.manager.getCommandReplacements().replace(string);
        Object i = commandOperationContext.getCommandIssuer();
        for (String string2 : ACFPatterns.PIPE.split(string)) {
            RegisteredCommand registeredCommand;
            String[] stringArray = ACFPatterns.COLON.split(string2, 2);
            String string3 = stringArray[0].toLowerCase(Locale.ENGLISH);
            Condition<I> condition = this.conditions.get(string3);
            if (condition == null) {
                registeredCommand = commandOperationContext.getRegisteredCommand();
                this.manager.log(LogLevel.ERROR, "Could not find command condition " + string3 + " for " + registeredCommand.method.getName());
                continue;
            }
            registeredCommand = stringArray.length == 2 ? stringArray[1] : null;
            ConditionContext conditionContext = this.manager.createConditionContext((CommandIssuer)i, (String)((Object)registeredCommand));
            condition.validateCondition(conditionContext);
        }
    }

    void validateConditions(CEC CEC, Object object) {
        String string = ((CommandExecutionContext)CEC).getCommandParameter().getConditions();
        if (string == null) {
            return;
        }
        string = this.manager.getCommandReplacements().replace(string);
        Object i = ((CommandExecutionContext)CEC).getIssuer();
        for (String string2 : ACFPatterns.PIPE.split(string)) {
            RegisteredCommand registeredCommand;
            ParameterCondition<?, ?, ?> parameterCondition;
            String[] stringArray = ACFPatterns.COLON.split(string2, 2);
            Class<?> clazz = ((CommandExecutionContext)CEC).getParam().getType();
            String string3 = stringArray[0].toLowerCase(Locale.ENGLISH);
            while ((parameterCondition = this.paramConditions.get(clazz, string3)) == null && clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class && (clazz = clazz.getSuperclass()) != null) {
            }
            if (parameterCondition == null) {
                registeredCommand = ((CommandExecutionContext)CEC).getCmd();
                this.manager.log(LogLevel.ERROR, "Could not find command condition " + string3 + " for " + registeredCommand.method.getName() + "::" + ((CommandExecutionContext)CEC).getParam().getName());
                continue;
            }
            registeredCommand = stringArray.length == 2 ? stringArray[1] : null;
            ConditionContext conditionContext = this.manager.createConditionContext((CommandIssuer)i, (String)((Object)registeredCommand));
            parameterCondition.validateCondition(conditionContext, CEC, object);
        }
    }

    public static interface Condition<I extends CommandIssuer> {
        public void validateCondition(ConditionContext<I> var1) throws InvalidCommandArgument;
    }

    public static interface ParameterCondition<P, CEC extends CommandExecutionContext, I extends CommandIssuer> {
        public void validateCondition(ConditionContext<I> var1, CEC var2, P var3) throws InvalidCommandArgument;
    }
}

