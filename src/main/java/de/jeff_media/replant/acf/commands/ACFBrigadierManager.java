package de.jeff_media.replant.acf.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.jeff_media.replant.acf.commands.ACFPatterns;
import de.jeff_media.replant.acf.commands.BaseCommand;
import de.jeff_media.replant.acf.commands.CommandManager;
import de.jeff_media.replant.acf.commands.CommandParameter;
import de.jeff_media.replant.acf.commands.ForwardingCommand;
import de.jeff_media.replant.acf.commands.RegisteredCommand;
import de.jeff_media.replant.acf.commands.RootCommand;
import de.jeff_media.replant.acf.commands.UnstableAPI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Deprecated
@UnstableAPI
public class ACFBrigadierManager<S> {
    protected final CommandManager<?, ?, ?, ?, ?, ?> manager;
    private final Map<Class<?>, ArgumentType<?>> arguments = new HashMap();

    ACFBrigadierManager(CommandManager<?, ?, ?, ?, ?, ?> commandManager) {
        commandManager.verifyUnstableAPI("brigadier");
        this.manager = commandManager;
        this.registerArgument((Class)String.class, (ArgumentType<?>)StringArgumentType.word());
        this.registerArgument(Float.TYPE, (ArgumentType<?>)FloatArgumentType.floatArg());
        this.registerArgument((Class)Float.class, (ArgumentType<?>)FloatArgumentType.floatArg());
        this.registerArgument(Double.TYPE, (ArgumentType<?>)DoubleArgumentType.doubleArg());
        this.registerArgument((Class)Double.class, (ArgumentType<?>)DoubleArgumentType.doubleArg());
        this.registerArgument(Boolean.TYPE, (ArgumentType<?>)BoolArgumentType.bool());
        this.registerArgument((Class)Boolean.class, (ArgumentType<?>)BoolArgumentType.bool());
        this.registerArgument(Integer.TYPE, (ArgumentType<?>)IntegerArgumentType.integer());
        this.registerArgument((Class)Integer.class, (ArgumentType<?>)IntegerArgumentType.integer());
        this.registerArgument(Long.TYPE, (ArgumentType<?>)IntegerArgumentType.integer());
        this.registerArgument((Class)Long.class, (ArgumentType<?>)IntegerArgumentType.integer());
    }

    <T> void registerArgument(Class<T> clazz, ArgumentType<?> argumentType) {
        this.arguments.put(clazz, argumentType);
    }

    ArgumentType<Object> getArgumentTypeByClazz(CommandParameter commandParameter) {
        if (commandParameter.consumesRest) {
            return StringArgumentType.greedyString();
        }
        return (ArgumentType)this.arguments.getOrDefault(commandParameter.getType(), (ArgumentType<?>)StringArgumentType.string());
    }

    LiteralCommandNode<S> register(RootCommand rootCommand, LiteralCommandNode<S> literalCommandNode, SuggestionProvider<S> suggestionProvider, Command<S> command, BiPredicate<RootCommand, S> biPredicate, BiPredicate<RegisteredCommand, S> biPredicate2) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)literalCommandNode.getLiteral()).requires(object -> biPredicate.test(rootCommand, object));
        RegisteredCommand registeredCommand = rootCommand.getDefaultRegisteredCommand();
        if (registeredCommand != null && registeredCommand.requiredResolvers == 0) {
            literalArgumentBuilder.executes(command);
        }
        literalCommandNode = literalArgumentBuilder.build();
        boolean bl = rootCommand.getDefCommand() instanceof ForwardingCommand;
        if (registeredCommand != null) {
            this.registerParameters(registeredCommand, (CommandNode<S>)literalCommandNode, suggestionProvider, command, biPredicate2);
        }
        for (Map.Entry entry : rootCommand.getSubCommands().entries()) {
            LiteralCommandNode literalCommandNode2;
            if (BaseCommand.isSpecialSubcommand((String)entry.getKey()) && !bl || !((String)entry.getKey()).equals("help") && ((RegisteredCommand)entry.getValue()).prefSubCommand.equals("help")) continue;
            String string = (String)entry.getKey();
            LiteralCommandNode literalCommandNode3 = literalCommandNode;
            Predicate<Object> predicate = object -> biPredicate2.test((RegisteredCommand)entry.getValue(), object);
            if (!bl) {
                LiteralArgumentBuilder literalArgumentBuilder2;
                if (string.contains(" ")) {
                    literalArgumentBuilder2 = ACFPatterns.SPACE.split(string);
                    for (int i = 0; i < ((String[])literalArgumentBuilder2).length - 1; ++i) {
                        if (literalCommandNode3.getChild((String)literalArgumentBuilder2[i]) == null) {
                            LiteralCommandNode literalCommandNode4 = ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)literalArgumentBuilder2[i]).requires(predicate)).build();
                            literalCommandNode3.addChild((CommandNode)literalCommandNode4);
                            literalCommandNode3 = literalCommandNode4;
                            continue;
                        }
                        literalCommandNode3 = literalCommandNode3.getChild((String)literalArgumentBuilder2[i]);
                    }
                    string = literalArgumentBuilder2[((LiteralArgumentBuilder)literalArgumentBuilder2).length - 1];
                }
                if ((literalCommandNode2 = literalCommandNode3.getChild(string)) == null) {
                    literalArgumentBuilder2 = (LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)string).requires(predicate);
                    if (((RegisteredCommand)entry.getValue()).requiredResolvers == 0) {
                        literalArgumentBuilder2.executes(command);
                    }
                    literalCommandNode2 = literalArgumentBuilder2.build();
                }
            } else {
                literalCommandNode2 = literalCommandNode;
            }
            this.registerParameters((RegisteredCommand)entry.getValue(), (CommandNode<S>)literalCommandNode2, suggestionProvider, command, biPredicate2);
            if (bl) continue;
            literalCommandNode3.addChild((CommandNode)literalCommandNode2);
        }
        return literalCommandNode;
    }

    void registerParameters(RegisteredCommand registeredCommand, CommandNode<S> argumentCommandNode, SuggestionProvider<S> suggestionProvider, Command<S> command, BiPredicate<RegisteredCommand, S> biPredicate) {
        for (int i = 0; i < registeredCommand.parameters.length; ++i) {
            CommandParameter commandParameter = registeredCommand.parameters[i];
            CommandParameter commandParameter2 = commandParameter.getNextParam();
            if (commandParameter.isCommandIssuer() || commandParameter.canExecuteWithoutInput() && commandParameter2 != null && !commandParameter2.canExecuteWithoutInput()) continue;
            RequiredArgumentBuilder requiredArgumentBuilder = (RequiredArgumentBuilder)RequiredArgumentBuilder.argument((String)commandParameter.getName(), this.getArgumentTypeByClazz(commandParameter)).suggests(suggestionProvider).requires(object -> biPredicate.test(registeredCommand, object));
            if (commandParameter2 == null || commandParameter2.canExecuteWithoutInput()) {
                requiredArgumentBuilder.executes(command);
            }
            ArgumentCommandNode argumentCommandNode2 = requiredArgumentBuilder.build();
            argumentCommandNode.addChild((CommandNode)argumentCommandNode2);
            argumentCommandNode = argumentCommandNode2;
        }
    }
}

