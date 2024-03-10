package org.slogga.habboscanner.logic.game.console.commands.start.modes;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.start.StartMode;

import org.slogga.habboscanner.HabboScanner;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StartBotPerRoomIdListMode implements StartMode {
    @Override
    public void handle(int userId) {
        String botRoomIdList = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.room.id.list");
        String[] roomIds = botRoomIdList.split(" ");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentIndex = new AtomicInteger(0);

        executorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                    .getInstance().getConfigurator().getConsoleHandlers().getCommands().get(":start");
            boolean isBotRunning = startConsoleCommand.getIsBotRunning();

            if (!isBotRunning || currentIndex.get() >= roomIds.length)
                return;

            int roomId = Integer.parseInt(roomIds[currentIndex.getAndIncrement()]);

            HabboActions.moveToRoom(roomId);
        }, 0, 2, TimeUnit.SECONDS);
    }
}
