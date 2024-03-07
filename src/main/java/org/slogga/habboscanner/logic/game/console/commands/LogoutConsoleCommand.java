package org.slogga.habboscanner.logic.game.console.commands;

import org.apache.commons.lang3.NotImplementedException;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import gearth.protocol.HMessage;
import org.slogga.habboscanner.HabboScanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogoutConsoleCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        String noMoreArchivingMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("no.more.archiving.message");

        HabboScanner.getInstance().sendPrivateMessage(userId, noMoreArchivingMessage);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.logout.command.description");
    }
    @Override
    public void resetForStart() {
        throw new NotImplementedException();
    }
}
