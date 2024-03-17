package org.slogga.habboscanner.logic.game.commands;

import lombok.Data;

import org.slogga.habboscanner.logic.game.commands.Console.ConsoleCommandExecutor;
import org.slogga.habboscanner.logic.game.commands.Discord.DiscordCommandExecutor;

@Data
public class CommandFactory {
    public static CommandExecutor commandExecutorInstance; // This could be Console or Discord.

    public static CommandExecutor getCommandExecutor(CommandExecutorType commandExecutorType, CommandExecutorProperties properties, Boolean isNewCommand) {
        if (commandExecutorInstance != null){
            if(isNewCommand)
                commandExecutorInstance.setProperties(properties);
            return commandExecutorInstance;
        }

        switch (commandExecutorType) {
            case CONSOLE:
                commandExecutorInstance = new ConsoleCommandExecutor(properties);
                break;

            case DISCORD:
                commandExecutorInstance = new DiscordCommandExecutor(properties);
                break;

            default:
                throw new RuntimeException("Impossible to get instance of command executor type.");
        }

        return commandExecutorInstance;
    }
}
