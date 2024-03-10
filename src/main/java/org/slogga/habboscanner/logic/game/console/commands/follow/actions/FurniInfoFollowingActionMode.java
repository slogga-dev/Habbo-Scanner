package org.slogga.habboscanner.logic.game.console.commands.follow.actions;

import org.slogga.habboscanner.logic.game.console.commands.follow.FollowingActionMode;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.HabboScanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FurniInfoFollowingActionMode implements FollowingActionMode {
    @Override
    public void handle() {
        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        String startFurniInfoModeMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message").getProperty("start.furni_info.mode.message");

        HabboScanner.getInstance().sendPrivateMessage(consoleUserId, startFurniInfoModeMessage);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            String endOfFurniInfoModeMessage = HabboScanner.getInstance().getConfigurator()
                    .getProperties().get("message").getProperty("end.of.furni_info.mode.message");

            HabboScanner.getInstance().sendPrivateMessage(consoleUserId, endOfFurniInfoModeMessage);

            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                    .getConfigurator()
                    .getConsoleHandlers().getCommands().get(":start");

            startConsoleCommand.setIsBotRunning(true);

            HabboScanner.getInstance()
                    .getConfigurator()
                    .getRoomInfoHandlers().refreshLastRoomAccess();
        }, 120, TimeUnit.SECONDS);
    }
}
