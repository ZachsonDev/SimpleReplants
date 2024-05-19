package de.jeff_media.replant.acf.commands.contexts;

import de.jeff_media.replant.acf.commands.CommandExecutionContext;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.contexts.ContextResolver;

public interface IssuerOnlyContextResolver<T, C extends CommandExecutionContext<?, ? extends CommandIssuer>>
extends ContextResolver<T, C> {
}

