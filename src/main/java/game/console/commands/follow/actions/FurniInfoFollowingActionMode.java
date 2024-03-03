package game.console.commands.follow.actions;

import game.console.commands.follow.FollowingActionMode;
import game.console.commands.start.StartConsoleCommand;
import scanner.HabboScanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FurniInfoFollowingActionMode implements FollowingActionMode {
    @Override
    public void handle() {
        int consoleUserId = HabboScanner.getInstance().getConsoleHandlers().getUserId();

        String startFurniInfoModeMessage = HabboScanner.getInstance().getMessageProperties().getProperty("start.furni_info.mode.message");

        HabboScanner.getInstance().sendPrivateMessage(consoleUserId, startFurniInfoModeMessage);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            String endOfFurniInfoModeMessage = HabboScanner.getInstance().getMessageProperties().getProperty("end.of.furni_info.mode.message");

            HabboScanner.getInstance().sendPrivateMessage(consoleUserId, endOfFurniInfoModeMessage);

            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                    .getConsoleHandlers().getCommands().get(":start");

            startConsoleCommand.setIsBotRunning(true);

            HabboScanner.getInstance().getRoomInfoHandlers().refreshLastRoomAccess();
        }, 120, TimeUnit.SECONDS);
    }
}
