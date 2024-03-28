package org.slogga.habboscanner.logic.game.console.commands;

import java.util.concurrent.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.commands.common.InfoCommand;

public class InfoConsoleCommand extends InfoCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        super.execute(properties);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> HabboActions.
                sendMessage(properties.getUserId(), "-----------------------"));
    }

    @Override
    protected void printStatus(CommandExecutorProperties properties, String variableName, boolean isActive) {
        String statusKey = isActive ? "variable.status.enabled.message" : "variable.status.disabled.message";
        String status = HabboScanner.getInstance().getConfigurator().getProperties().get("message").getProperty(statusKey);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            String message = variableName + " " + status;

            HabboActions.sendMessage(properties.getUserId(), message);
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
}
