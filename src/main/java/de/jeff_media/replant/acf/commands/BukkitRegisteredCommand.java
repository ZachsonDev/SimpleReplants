package de.jeff_media.replant.acf.commands;

import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.BukkitCommandExecutionContext;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import java.lang.reflect.Method;

public class BukkitRegisteredCommand
extends RegisteredCommand<BukkitCommandExecutionContext> {
    BukkitRegisteredCommand(BaseCommand baseCommand, String string, Method method, String string2) {
        super(baseCommand, string, method, string2);
    }
}

