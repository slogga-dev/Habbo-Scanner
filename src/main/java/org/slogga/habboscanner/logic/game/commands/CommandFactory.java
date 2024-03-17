package org.slogga.habboscanner.logic.game.commands;

import lombok.Data;

import org.slogga.habboscanner.logic.game.commands.console.ConsoleCommandExecutor;
import org.slogga.habboscanner.logic.game.commands.discord.DiscordCommandExecutor;

@Data
public class CommandFactory {
    public static CommandExecutor commandExecutorInstance; // This could be Console or Discord.

    public static void getCommandExecutor(CommandExecutorType commandExecutorType, CommandExecutorProperties properties) {
        if (commandExecutorInstance != null) {
            commandExecutorInstance.setProperties(properties);

            return;
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
    }
}
