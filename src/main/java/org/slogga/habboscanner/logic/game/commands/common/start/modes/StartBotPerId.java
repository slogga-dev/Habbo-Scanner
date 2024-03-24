package org.slogga.habboscanner.logic.game.commands.common.start.modes;

import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.slogga.habboscanner.logic.game.HabboActions;

import org.slogga.habboscanner.logic.game.commands.*;
import org.slogga.habboscanner.logic.game.commands.common.start.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.enums.CommandKeys;

public class StartBotPerId implements IStarter {
    @Override
    public void execute(CommandExecutorProperties properties) {
        Properties botProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("bot");

        String botPerIdOrder = String.valueOf(botProperties.getProperty("bot.per.id.order"));

        boolean isOrderInvalid = !"asc".equals(botPerIdOrder) && !"desc".equals(botPerIdOrder);

        if (isOrderInvalid) {
            String incorrectOrderMessage = HabboScanner.getInstance().
                    getConfigurator().getProperties().get("message").getProperty("incorrect.order.message");

            int userId = properties.getUserId();
            HabboActions.sendPrivateMessage(userId, incorrectOrderMessage);

            return;
        }

        AtomicInteger initialRoomId = new AtomicInteger(Integer.parseInt(botProperties.getProperty("bot.initial.room.id")));
        int directionStep = "asc".equals(botPerIdOrder) ? 1 : -1;

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            StartCommand startConsoleCommand = (StartCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
            boolean isBotRunning = startConsoleCommand.isBotRunning();

            if (!isBotRunning) return;

            HabboActions.moveToRoom(initialRoomId.get());

            initialRoomId.addAndGet(directionStep);
        }, 0, 2, TimeUnit.SECONDS);
    }
}