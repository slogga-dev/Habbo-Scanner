package org.slogga.habboscanner.logic.game.commands.common.follow.actions;

import java.util.Map;
import java.util.concurrent.*;

import org.slogga.habboscanner.logic.configurators.HabboScannerConfigurator;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.console.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.follow.IFollower;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.models.CommandKeys;

public class FurniInfoFollowingAction implements IFollower {
    @Override
    public void execute() {
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

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory.
                commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

        followConsoleCommand.handleEmptyRoom();
    }
}
