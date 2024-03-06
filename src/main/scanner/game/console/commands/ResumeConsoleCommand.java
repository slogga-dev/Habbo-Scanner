package scanner.game.console.commands;

import gearth.protocol.HMessage;

import org.apache.commons.lang3.NotImplementedException;
import scanner.game.console.IConsoleCommand;
import scanner.game.console.commands.start.StartConsoleCommand;

import scanner.HabboScanner;

public class ResumeConsoleCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                .getInstance().getConfigurator().getConsoleHandlers().getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(true);

        HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().refreshLastRoomAccess();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.resume.command.description");
    }
    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }
}
