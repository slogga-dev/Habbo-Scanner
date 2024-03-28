package org.slogga.habboscanner.logic.commands;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.ConsoleCommandExecutor;
import org.slogga.habboscanner.logic.discord.commands.DiscordCommandExecutor;

public abstract class Command {
    public static void sendMessage(String message, CommandExecutorProperties properties) {
        if (!(CommandFactory.commandExecutorInstance instanceof ConsoleCommandExecutor ||
                CommandFactory.commandExecutorInstance instanceof DiscordCommandExecutor))
            throw new IllegalArgumentException("Invalid command executor instance");

        if (CommandFactory.commandExecutorInstance instanceof ConsoleCommandExecutor) {
            HabboActions.sendPrivateMessage(properties.getUserId(), message);

            return;
        }

        properties.getEvent().reply(message).queue();
    }

    public abstract void execute(CommandExecutorProperties properties);

    public abstract String getDescription();
}
