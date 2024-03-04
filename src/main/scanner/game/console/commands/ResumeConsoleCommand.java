package scanner.game.console.commands;

import gearth.protocol.HMessage;

import scanner.game.console.ConsoleCommand;
import scanner.game.console.commands.start.StartConsoleCommand;

import scanner.HabboScanner;

public class ResumeConsoleCommand implements ConsoleCommand {
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
}
