package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrigadierManager {

    private final Commodore commodore;

    public BrigadierManager() {
        commodore = CommodoreProvider.getCommodore(FactionsPlugin.getInstance());
    }

    public void build() {
        FCmdRoot cmdBase = FCmdRoot.instance;
        LiteralArgumentBuilder<Object> factionsBrigadier = LiteralArgumentBuilder.literal("factions");
        LiteralArgumentBuilder<Object> fBrigadier = LiteralArgumentBuilder.literal("f");

        for (FCommand command : cmdBase.subCommands) {
            List<ArgumentBuilder<Object, ?>> aliases = addCommand(command, factionsBrigadier);
            aliases.forEach(alias -> {
                factionsBrigadier.then(alias);
                fBrigadier.then(alias);
            });
        }

        commodore.register(factionsBrigadier.build());
        commodore.register(fBrigadier.build());
    }

    private List<ArgumentBuilder<Object, ?>> addCommand(FCommand command, ArgumentBuilder<Object, ?> parent) {
        List<ArgumentBuilder<Object, ?>> aliases = command.aliases.stream()
                .map(alias -> createCommandAliasLiteral(command, alias, parent))
                .collect(Collectors.toList());

        aliases.forEach(literal -> {
            // Add subcommands to the current command
            List<FCommand> subCommands = command.subCommands;
            subCommands.stream().map(subCmd -> this.addCommand(subCmd, literal)).forEach(subLiterals -> subLiterals.forEach(literal::then));
        });
        return aliases;
    }

    private ArgumentBuilder<Object, ?> createCommandAliasLiteral(FCommand command, String alias, ArgumentBuilder<Object, ?> parent) {
        LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(alias);
        Class<? extends BrigadierProvider> brigadier = command.requirements.getBrigadier();
        if (brigadier != null) {
            // Command has it's own brigadier provider
            try {
                Constructor<? extends BrigadierProvider> constructor = brigadier.getDeclaredConstructor();
                return constructor.newInstance().get(literal);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }

        // Add the arguments to the command
        List<RequiredArgumentBuilder<Object, ?>> argsStack = generateArgsStack(command);
        RequiredArgumentBuilder<Object, ?> previous = null;
        for (int i = argsStack.size() - 1; i >= 0; i--) {
            if (previous == null) {
                previous = argsStack.get(i);
            } else {
                previous = argsStack.get(i).then(previous);
            }
        }
        if (previous != null) {
            literal.then(previous);
        }

        return literal;
    }

    private List<RequiredArgumentBuilder<Object, ?>> generateArgsStack(FCommand subCommand) {
        List<RequiredArgumentBuilder<Object, ?>> stack = new ArrayList<>(subCommand.requiredArgs.size() + subCommand.optionalArgs.size());

        for (String required : subCommand.requiredArgs) {
            stack.add(RequiredArgumentBuilder.argument(required, StringArgumentType.word()));
        }

        for (Map.Entry<String, String> optionalEntry : subCommand.optionalArgs.entrySet()) {
            RequiredArgumentBuilder<Object, ?> optional;
            if (optionalEntry.getKey().equalsIgnoreCase(optionalEntry.getValue())) {
                optional = RequiredArgumentBuilder.argument(":" + optionalEntry.getKey(), StringArgumentType.word());
            } else {
                optional = RequiredArgumentBuilder.argument(optionalEntry.getKey() + "|" + optionalEntry.getValue(), StringArgumentType.word());
            }
            stack.add(optional);
        }

        return stack;
    }
}