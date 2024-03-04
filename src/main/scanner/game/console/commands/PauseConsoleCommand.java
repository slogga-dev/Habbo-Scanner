package scanner.game.console.commands;

import scanner.game.console.ConsoleCommand;
import scanner.game.console.commands.start.StartConsoleCommand;
import gearth.protocol.HMessage;
import scanner.HabboScanner;

public class PauseConsoleCommand implements ConsoleCommand {
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
}
