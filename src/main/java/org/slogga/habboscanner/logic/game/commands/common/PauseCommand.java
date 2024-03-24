package org.slogga.habboscanner.logic.game.commands.common;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.start.StartCommand;
import org.slogga.habboscanner.models.CommandKeys;

public class PauseCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        StartCommand startConsoleCommand = (StartCommand) CommandFactory.
                commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
        startConsoleCommand.setBotRunning(false);

        String relaxMomentMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("relax.moment.message");

        sendMessage(relaxMomentMessage, properties);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("pause.command.description");
    }
}
