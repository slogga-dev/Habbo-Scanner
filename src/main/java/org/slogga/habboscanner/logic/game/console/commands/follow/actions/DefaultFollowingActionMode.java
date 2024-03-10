package org.slogga.habboscanner.logic.game.console.commands.follow.actions;

import org.slogga.habboscanner.logic.configurators.HabboScannerConfigurator;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.follow.*;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.HabboScanner;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultFollowingActionMode implements FollowingActionMode {
    @Override
    public void handle() {
        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            String endOfFurniInfoModeMessage = HabboScanner.getInstance().getConfigurator()
                    .getProperties().get("message").getProperty("end.of.furni_info.mode.message");

            HabboActions.sendPrivateMessage(consoleUserId, endOfFurniInfoModeMessage);

            Map<String, IConsoleCommand> commands = HabboScanner.getInstance().getConfigurator()
                    .getConsoleHandlers().getCommands();
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(":start");
            FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) commands.get(":follow");

            startConsoleCommand.setBotRunning(true);
            followConsoleCommand.setFollowing(false);

            HabboScanner.getInstance()
                    .getConfigurator()
                    .getRoomInfoHandlers().refreshLastRoomAccess();
        }, 3, TimeUnit.SECONDS);
    }
}

