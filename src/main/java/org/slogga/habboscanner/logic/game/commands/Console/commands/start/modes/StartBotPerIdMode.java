package org.slogga.habboscanner.logic.game.commands.Console.commands.start.modes;

import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartMode;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

public class StartBotPerIdMode implements StartMode {
    @Override
    public void handle(int userId) {
        Properties botProperties = HabboScanner.getInstance().getConfigurator().getProperties().get("bot");

        String botPerIdOrder = String.valueOf(botProperties.getProperty("bot.per.id.order"));

        boolean isOrderInvalid = !"asc".equals(botPerIdOrder) && !"desc".equals(botPerIdOrder);

        if (isOrderInvalid) {
            String incorrectOrderMessage = HabboScanner.getInstance().
                    getConfigurator().getProperties().get("message").getProperty("incorrect.order.message");

            HabboActions.sendPrivateMessage(userId, incorrectOrderMessage);

            return;
        }

        AtomicInteger initialRoomId = new AtomicInteger(Integer.parseInt(botProperties.getProperty("bot.initial.room.id")));
        int directionStep = "asc".equals(botPerIdOrder) ? 1 : -1;

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
            boolean isBotRunning = startConsoleCommand.isBotRunning();

            if (!isBotRunning) return;

            HabboActions.moveToRoom(initialRoomId.get());

            initialRoomId.addAndGet(directionStep);
        }, 0, 2, TimeUnit.SECONDS);
    }
}