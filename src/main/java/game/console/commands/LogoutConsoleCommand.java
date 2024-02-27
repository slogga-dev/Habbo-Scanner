package game.console.commands;

import game.console.ConsoleCommand;
import gearth.protocol.HMessage;
import scanner.HabboScanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogoutConsoleCommand implements ConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        String noMoreArchivingMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("no.more.archiving.message");

        HabboScanner.getInstance().sendPrivateMessage(userId, noMoreArchivingMessage);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.logout.command.description");
    }
}
