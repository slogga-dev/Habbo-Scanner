package org.slogga.habboscanner.logic.game.commands.console.commands;

import java.util.concurrent.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;

public class LogoutConsoleCommand implements IExecuteCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        String noMoreArchivingMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("no.more.archiving.message");

        HabboActions.sendPrivateMessage(properties.getUserId(), noMoreArchivingMessage);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.logout.command.description");
    }
}
