package org.slogga.habboscanner.logic.commands.common.follow.actions;

import java.util.concurrent.*;

import gearth.protocol.HMessage;
import org.slogga.habboscanner.logic.configurators.HabboScannerConfigurator;

import org.slogga.habboscanner.logic.game.HabboActions;

import org.slogga.habboscanner.HabboScanner;

public class FurniInfoFollowingAction extends BaseFollowingAction {
    @Override
    public void execute(HMessage message) {
        super.execute(message);
        HabboScannerConfigurator habboScannerConfigurator = HabboScanner.getInstance().getConfigurator();

        int consoleUserId = habboScannerConfigurator.getConsoleHandlers().getUserId();

        String startFurniInfoModeMessage = habboScannerConfigurator.getProperties()
                .get("message").getProperty("start.furni_info.mode.message");

        HabboActions.sendMessage(consoleUserId, startFurniInfoModeMessage);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(this::goAway, 2, TimeUnit.MINUTES);
    }
}
