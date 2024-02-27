package game.console.commands.start.modes;

import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import game.console.commands.start.StartConsoleCommand;
import game.console.commands.start.StartMode;

import scanner.HabboScanner;

public class StartBotPerIdMode implements StartMode {
    @Override
    public void handle(int userId) {
        Properties botProperties = HabboScanner.getInstance().getBotProperties();

        String botPerIdOrder = String.valueOf(botProperties.getProperty("bot.per.id.order"));

        boolean isOrderInvalid = !"asc".equals(botPerIdOrder) && !"desc".equals(botPerIdOrder);

        if (isOrderInvalid) {
            String incorrectOrderMessage = HabboScanner.getInstance().getMessageProperties().getProperty("incorrect.order.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, incorrectOrderMessage);

            return;
        }

        AtomicInteger initialRoomId = new AtomicInteger(Integer.parseInt(botProperties.getProperty("bot.initial.room.id")));
        int directionStep = "asc".equals(botPerIdOrder) ? 1 : -1;

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                    .getInstance().getConsoleHandlers().getCommands().get(":start");
            boolean isBotRunning = startConsoleCommand.getIsBotRunning();

            if (!isBotRunning) return;

            HabboScanner.getInstance().moveToRoom(initialRoomId.get());

            initialRoomId.addAndGet(directionStep);
        }, 0, 2, TimeUnit.SECONDS);
    }
}