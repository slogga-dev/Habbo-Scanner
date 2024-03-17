package org.slogga.habboscanner.logic.game.commands.Console.commands.follow.actions;

import org.slogga.habboscanner.logic.configurators.HabboScannerConfigurator;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.FollowConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.FollowingActionMode;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.IConsoleCommand;
import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FurniInfoFollowingActionMode implements FollowingActionMode {
    @Override
    public void handle() {
        HabboScannerConfigurator habboScannerConfigurator = HabboScanner.getInstance().getConfigurator();

        int consoleUserId = habboScannerConfigurator.getConsoleHandlers().getUserId();

        String startFurniInfoModeMessage = habboScannerConfigurator.getProperties()
                .get("message").getProperty("start.furni_info.mode.message");

        HabboActions.sendPrivateMessage(consoleUserId, startFurniInfoModeMessage);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(this::goAway, 2, TimeUnit.MINUTES);
    }

    public void goAway() {
        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        String endOfFurniInfoModeMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("end.of.furni_info.mode.message");

        HabboActions.sendPrivateMessage(consoleUserId, endOfFurniInfoModeMessage);

        Map<String, IExecuteCommand> commands = CommandFactory.commandExecutorInstance.getCommands();
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(CommandKeys.START.getKey());
        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) commands.get(CommandKeys.FOLLOW.getKey());

        startConsoleCommand.setBotRunning(true);
        followConsoleCommand.setFollowing(false);

        HabboScanner.getInstance()
                .getConfigurator()
                .getRoomEntryHandler().refreshLastRoomAccess();
    }
}
