package org.slogga.habboscanner.logic.game.console.commands;

import org.apache.commons.lang3.NotImplementedException;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import gearth.protocol.HMessage;
import org.slogga.habboscanner.HabboScanner;

public class PauseConsoleCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        String relaxMomentMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("relax.moment.message");

        HabboScanner.getInstance().sendPrivateMessage(userId, relaxMomentMessage);

        message.setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers().getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(false);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.pause.command.description");
    }
    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }
}
