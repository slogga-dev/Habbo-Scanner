package game.console.commands;

import gearth.protocol.HMessage;

import game.console.ConsoleCommand;
import game.console.commands.start.StartConsoleCommand;

import scanner.HabboScanner;

public class ResumeConsoleCommand implements ConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                .getInstance().getConsoleHandlers().getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(true);

        HabboScanner.getInstance().getRoomInfoHandlers().refreshLastRoomAccess();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.resume.command.description");
    }
}
