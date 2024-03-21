package org.slogga.habboscanner.logic.game.commands;

import java.util.*;
import java.util.function.Function;

import lombok.Data;

import org.slogga.habboscanner.logic.game.commands.console.ConsoleCommandExecutor;
import org.slogga.habboscanner.logic.game.commands.discord.DiscordCommandExecutor;

@Data
public class CommandFactory {
    private static final Map<CommandExecutorType, Function<CommandExecutorProperties, CommandExecutor>> executorMap = new HashMap<>();

    public static CommandExecutor commandExecutorInstance; // This could be Console or Discord.

    static {
        executorMap.put(CommandExecutorType.CONSOLE, ConsoleCommandExecutor::new);
        executorMap.put(CommandExecutorType.DISCORD, DiscordCommandExecutor::new);
    }

    public static void getCommandExecutor(CommandExecutorType commandExecutorType, CommandExecutorProperties properties) {
        if (commandExecutorInstance != null) {
            commandExecutorInstance.setProperties(properties);

            return;
        }

        Function<CommandExecutorProperties, CommandExecutor> executor = executorMap.get(commandExecutorType);

        if (executor == null)
            throw new RuntimeException("Impossible to get instance of command executor type.");

        commandExecutorInstance = executor.apply(properties);
    }
}
