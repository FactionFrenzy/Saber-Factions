package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.command.PluginCommand;

import java.util.List;
import java.util.stream.Collectors;

public class BrigadierManager {

    private final Commodore commodore;

    public BrigadierManager() {

        commodore = CommodoreProvider.getCommodore(FactionsPlugin.getInstance());
    }

    public void build() {
/*        commodore.register(brigadier.build());

        // Register 'f' alias with all children of 'factions'
        LiteralArgumentBuilder<Object> fLiteral = LiteralArgumentBuilder.literal("f");
        for (CommandNode<Object> node : brigadier.getArguments()) {

            fLiteral.then(node);
        }
        commodore.register(fLiteral.build());*/
        PluginCommand pluginCommand = FactionsPlugin.getInstance().getCommand("factions");

        FCmdRoot cmdBase = FCmdRoot.instance;
        LiteralArgumentBuilder<?> brigadier = addCommands(cmdBase.subCommands, LiteralArgumentBuilder.literal("factions"));
        commodore.register(pluginCommand, brigadier);
    }

    private LiteralArgumentBuilder<Object> addCommands(List<FCommand> commands, LiteralArgumentBuilder<Object> parent) {
        for (FCommand subCommand : commands) {
            for (String alias : subCommand.aliases) {
                System.out.println("Adding command " + alias);
                LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(alias);

                List<LiteralArgumentBuilder<Object>> subCommands = subCommand.subCommands.stream().map(subCmd -> this.addCommands(subCmd.subCommands, literal)).collect(Collectors.toList());
                for (LiteralArgumentBuilder<Object> subCmd : subCommands) {
                    System.out.println("Added subcommand " + subCmd.getLiteral() + " to " + literal.getLiteral());
                    literal.then(subCmd);
                }

                parent.then(literal);
            }
        }

/*        for (String alias : command.aliases) {
            System.out.println("Adding command " + alias);
            LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(alias);

            List<LiteralArgumentBuilder<Object>> subCommands = command.subCommands.stream().map(subCommand -> this.addSubCommands(subCommand, literal)).collect(Collectors.toList());
            for (LiteralArgumentBuilder<Object> subCommand : subCommands) {
                *//*literal.then(subCommand);*//*
                System.out.println("Added subcommand " + subCommand.getLiteral() + " to " + literal.getLiteral());
            }

            parent.then(literal);
        }*/

        return parent;
/*        LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(command.aliases.get(0));
        List<LiteralArgumentBuilder<Object>> subCommands = command.subCommands.stream().map(this::addCommand).collect(Collectors.toList());
        for (LiteralArgumentBuilder<Object> subCommand : subCommands) {
            literal.then(subCommand);
        }
        return literal;*/
    }

    public void addSubCommand(FCommand subCommand) {
        //System.out.println("Added subcommand " + subCommand.aliases.get(0));
/*        for (String alias : subCommand.aliases) {
            LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(alias);

            if (subCommand.requirements.getBrigadier() != null) {
                registerUsingProvider(subCommand, literal);
            } else {
                registerGeneratedBrigadier(subCommand, literal);
            }
        }*/
    }

    private void registerUsingProvider(FCommand subCommand, LiteralArgumentBuilder<Object> literal) {
        System.out.println("Registered using provider " + subCommand.aliases.get(0));
/*        Class<? extends BrigadierProvider> brigadierProvider = subCommand.requirements.getBrigadier();
        try {
            Constructor<? extends BrigadierProvider> constructor = brigadierProvider.getDeclaredConstructor();
            brigadier.then(constructor.newInstance().get(literal));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException exception) {
            exception.printStackTrace();
        }*/
    }

    private void registerGeneratedBrigadier(FCommand subCommand, LiteralArgumentBuilder<Object> literal) {
        System.out.println("Registered generated brigadier " + subCommand.aliases.get(0));
/*        List<RequiredArgumentBuilder<Object, ?>> argsStack = generateArgsStack(subCommand);

        RequiredArgumentBuilder<Object, ?> previous = null;
        for (int i = argsStack.size() - 1; i >= 0; i--) {
            if (previous == null) {
                previous = argsStack.get(i);
            } else {
                previous = argsStack.get(i).then(previous);
            }
        }

        if (previous == null) {
            brigadier.then(literal);
        } else {
            brigadier.then(literal.then(previous));
        }*/
    }

    private List<RequiredArgumentBuilder<Object, ?>> generateArgsStack(FCommand subCommand) {
        System.out.println("generated args stack " + subCommand.aliases.get(0));
/*        List<RequiredArgumentBuilder<Object, ?>> stack = new ArrayList<>(subCommand.requiredArgs.size() + subCommand.optionalArgs.size());

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

        return stack;*/
        return null;
    }
}