package de.jeff_media.replant.acf.commands.contexts;

import de.jeff_media.replant.acf.commands.CommandExecutionContext;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.InvalidCommandArgument;

@FunctionalInterface
public interface ContextResolver<T, C extends CommandExecutionContext<?, ? extends CommandIssuer>> {
    public T getContext(C var1) throws InvalidCommandArgument;
}

