package game.console.commands;

import game.console.ConsoleCommand;
import game.console.commands.start.StartConsoleCommand;
import gearth.protocol.HMessage;
import scanner.HabboScanner;

public class PauseConsoleCommand implements ConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        String relaxMomentMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("relax.moment.message");

        HabboScanner.getInstance().sendPrivateMessage(userId, relaxMomentMessage);

        message.setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":start");
        startConsoleCommand.setIsBotRunning(false);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.pause.command.description");
    }
}
