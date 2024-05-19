package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import java.lang.annotation.Annotation;
import java.util.List;

public class CommandOperationContext<I extends CommandIssuer> {
    private final CommandManager manager;
    private final I issuer;
    private final BaseCommand command;
    private final String commandLabel;
    private final String[] args;
    private final boolean isAsync;
    private RegisteredCommand registeredCommand;
    List<String> enumCompletionValues;

    CommandOperationContext(CommandManager commandManager, I i, BaseCommand baseCommand, String string, String[] stringArray, boolean bl) {
        this.manager = commandManager;
        this.issuer = i;
        this.command = baseCommand;
        this.commandLabel = string;
        this.args = stringArray;
        this.isAsync = bl;
    }

    public CommandManager getCommandManager() {
        return this.manager;
    }

    public I getCommandIssuer() {
        return this.issuer;
    }

    public BaseCommand getCommand() {
        return this.command;
    }

    public String getCommandLabel() {
        return this.commandLabel;
    }

    public String[] getArgs() {
        return this.args;
    }

    public boolean isAsync() {
        return this.isAsync;
    }

    public void setRegisteredCommand(RegisteredCommand registeredCommand) {
        this.registeredCommand = registeredCommand;
    }

    public RegisteredCommand getRegisteredCommand() {
        return this.registeredCommand;
    }

    @Deprecated
    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return this.registeredCommand.method.getAnnotation(clazz);
    }

    public <T extends Annotation> String getAnnotationValue(Class<T> clazz) {
        return this.manager.getAnnotations().getAnnotationValue(this.registeredCommand.method, clazz);
    }

    public <T extends Annotation> String getAnnotationValue(Class<T> clazz, int n) {
        return this.manager.getAnnotations().getAnnotationValue(this.registeredCommand.method, clazz, n);
    }

    public boolean hasAnnotation(Class<? extends Annotation> clazz) {
        return this.getAnnotation(clazz) != null;
    }
}

