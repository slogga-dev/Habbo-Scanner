package org.slogga.habboscanner.logic.game.commands.Console.commands.start.modes;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartMode;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StartBotPerOwnerNameListMode implements StartMode {
    @Override
    public void handle(int userId) {
        String botOwnerNameList = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.owner.name.list");
        String[] namesToScan = botOwnerNameList.split(" ");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentIndex = new AtomicInteger(0);

        executorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
            boolean isBotRunning = startConsoleCommand.isBotRunning();

            if (!isBotRunning || currentIndex.get() >= namesToScan.length)
                return;

            String name = namesToScan[currentIndex.getAndIncrement()];
            String searchType = name.equals("%popular_rooms%") ? "popular" : "query";
            String searchValue = name.equals("%popular_rooms%") ? "" : "owner:" + name;

            HabboActions.sendNavigatorSearch(searchType, searchValue);
        }, 0, 2, TimeUnit.SECONDS);
    }
}
