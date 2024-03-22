package org.slogga.habboscanner.logic.game.commands.common;

import java.util.concurrent.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.*;

public class ShutdownCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        String message = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("bot.shutdown.message");

        sendMessage(message, properties);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(() -> System.exit(0), 2, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("shutdown.command.description");
    }
}