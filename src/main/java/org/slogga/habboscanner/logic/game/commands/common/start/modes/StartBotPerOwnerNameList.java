package org.slogga.habboscanner.logic.game.commands.common.start.modes;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.start.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.enums.CommandKeys;

public class StartBotPerOwnerNameList implements IStarter {
    @Override
    public void execute(CommandExecutorProperties properties) {
        String botOwnerNameList = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.owner.name.list");
        String[] namesToScan = botOwnerNameList.split(" ");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        AtomicInteger currentIndex = new AtomicInteger(0);

        executorService.scheduleAtFixedRate(() -> {
            StartCommand startConsoleCommand = (StartCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
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
