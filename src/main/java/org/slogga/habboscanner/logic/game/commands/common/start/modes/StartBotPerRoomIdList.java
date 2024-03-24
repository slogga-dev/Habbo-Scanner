package org.slogga.habboscanner.logic.game.commands.common.start.modes;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.start.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.enums.CommandKeys;

public class StartBotPerRoomIdList implements IStarter {
    @Override
    public void execute(CommandExecutorProperties properties) {
        String botRoomIdList = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.room.id.list");
        String[] roomIds = botRoomIdList.split(" ");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentIndex = new AtomicInteger(0);

        executorService.scheduleAtFixedRate(() -> {
            StartCommand startConsoleCommand = (StartCommand) CommandFactory.
                    commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
            boolean isBotRunning = startConsoleCommand.isBotRunning();

            if (!isBotRunning || currentIndex.get() >= roomIds.length)
                return;

            int roomId = Integer.parseInt(roomIds[currentIndex.getAndIncrement()]);

            HabboActions.moveToRoom(roomId);
        }, 0, 2, TimeUnit.SECONDS);
    }
}
