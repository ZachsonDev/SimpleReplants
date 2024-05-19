package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.CommandIssuer;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import java.util.List;

@FunctionalInterface
public interface ExceptionHandler {
    public boolean execute(BaseCommand var1, RegisteredCommand var2, CommandIssuer var3, List<String> var4, Throwable var5);
}

