package org.slogga.habboscanner.logic.game.commands;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.console.ConsoleCommandExecutor;
import org.slogga.habboscanner.logic.game.commands.discord.DiscordCommandExecutor;
import org.slogga.habboscanner.models.ConvertFile;

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

    protected abstract void execute(CommandExecutorProperties properties);

    protected abstract String getDescription();
}
