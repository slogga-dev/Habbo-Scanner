package org.slogga.habboscanner.logic.game.commands.Console.commands;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.IConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;
import org.slogga.habboscanner.models.CommandKeys;

public class PauseConsoleCommand implements IExecuteCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        String relaxMomentMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("relax.moment.message");

        HabboActions.sendPrivateMessage(properties.getUserId(), relaxMomentMessage);

        properties.getMessage().setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
        startConsoleCommand.setBotRunning(false);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.pause.command.description");
    }
}
