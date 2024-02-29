package game.console.commands.start.modes;

import game.console.commands.start.StartConsoleCommand;
import game.console.commands.start.StartMode;

import scanner.HabboScanner;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StartBotPerOwnerNameListMode implements StartMode {
    @Override
    public void handle(int userId) {
        String botOwnerNameList = HabboScanner.getInstance().getBotProperties().getProperty("bot.owner.name.list");
        String[] namesToScan = botOwnerNameList.split(" ");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentIndex = new AtomicInteger(0);

        executorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                    .getInstance().getConsoleHandlers().getCommands().get(":start");
            boolean isBotRunning = startConsoleCommand.getIsBotRunning();

            if (!isBotRunning || currentIndex.get() >= namesToScan.length)
                return;

            String name = namesToScan[currentIndex.getAndIncrement()];
            String searchType = name.equals("%popular_rooms%") ? "popular" : "query";
            String searchValue = name.equals("%popular_rooms%") ? "" : "owner:" + name;

            HabboScanner.getInstance().sendNavigatorSearch(searchType, searchValue);
        }, 0, 2, TimeUnit.SECONDS);
    }
}
