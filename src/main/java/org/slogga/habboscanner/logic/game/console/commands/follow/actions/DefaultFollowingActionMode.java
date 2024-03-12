package org.slogga.habboscanner.logic.game.console.commands.follow.actions;

import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.follow.*;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.Map;
import java.util.concurrent.*;

public class DefaultFollowingActionMode implements FollowingActionMode {
    @Override
    public void handle() {
        Map<String, IConsoleCommand> commands = HabboScanner.getInstance().getConfigurator()
                .getConsoleHandlers().getCommands();
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(CommandKeys.START.getKey());
        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) commands.get(CommandKeys.FOLLOW.getKey());

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            startConsoleCommand.setBotRunning(true);
            followConsoleCommand.setFollowing(false);

            HabboScanner.getInstance()
                    .getConfigurator()
                    .getRoomInfoHandlers().refreshLastRoomAccess();
        }, 10, TimeUnit.SECONDS);
    }
}

