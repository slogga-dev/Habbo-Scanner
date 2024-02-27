package game.console.commands.start.modes;

import game.console.commands.start.StartConsoleCommand;
import game.console.commands.start.StartMode;

import scanner.HabboScanner;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StartBotPerRoomIdListMode implements StartMode {
    @Override
    public void handle(int userId) {
        String botRoomIdList = HabboScanner.getInstance().getBotProperties().getProperty("bot.room.id.list");
        String[] roomIds = botRoomIdList.split(" ");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentIndex = new AtomicInteger(0);

        executorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                    .getInstance().getConsoleHandlers().getCommands().get(":start");
            boolean isBotRunning = startConsoleCommand.getIsBotRunning();

            if (!isBotRunning || currentIndex.get() >= roomIds.length)
                return;

            int roomId = Integer.parseInt(roomIds[currentIndex.getAndIncrement()]);

            HabboScanner.getInstance().moveToRoom(roomId);
        }, 0, 2, TimeUnit.SECONDS);
    }
}
