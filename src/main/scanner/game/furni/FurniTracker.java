package scanner.game.furni;

import java.sql.*;

import java.util.List;
import java.util.concurrent.*;

import scanner.database.dao.data.DataDAO;

import scanner.game.ItemProcessor;

import scanner.game.console.commands.follow.FollowConsoleCommand;

import scanner.handlers.RoomInfoHandlers;

import scanner.models.Furni;

import scanner.HabboScanner;

import scanner.utils.DateUtils;

public class FurniTracker {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void manageFurniTracking(Date estimatedDate) {
        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance()
                .getConfigurator().getConsoleHandlers().getCommands().get(":follow");

        boolean isFollowing = followConsoleCommand.isFollowing();

        ItemProcessor itemProcessor = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().getItemProcessor();
        Furni oldestFurni = itemProcessor.getOldestFurni();

        // is following the friend that called him via :follow command
        // also the ItemProcessor never cycled the onFloorItems method so the oldestFurni attribute is just instanced with the id null
        if (isFollowing && oldestFurni.getId() == null) {
            sendEmptyRoomMessage();

            return;
        }

        String name = oldestFurni.getName();
        String classname = oldestFurni.getClassname();
        String formattedDate = DateUtils.formatToStandardDate(estimatedDate);
        String rarestFurniName = itemProcessor.getRarestFurniName();
        int highestSeenPieces = itemProcessor.getHighestSeenPieces();

        String oldestFurniInRoomMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message").getProperty("oldest.furni.in.room.message");

        if (oldestFurniInRoomMessage == null) return;

        oldestFurniInRoomMessage = oldestFurniInRoomMessage
                .replace("%name%", name)
                .replace("%classname%", classname)
                .replace("%date%", formattedDate)
                .replace("%rarestFurniName%", rarestFurniName)
                .replace("%highestSeenPieces%", Integer.toString(highestSeenPieces));

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String finalMessage = oldestFurniInRoomMessage;

        scheduledExecutorService.schedule(() -> HabboScanner.getInstance()
                .sendPrivateMessage(consoleUserId, finalMessage), 1, TimeUnit.SECONDS);

        if (!isFollowing) return;

        processTransactionsAndTerminate();
    }

    private void sendEmptyRoomMessage() {
        String botEmptyRoomMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("bot.empty.room.message");
        String[] botMessageEmptyRoomArray = botEmptyRoomMessage.split("---");

        int randomIndex = (int) (Math.random() * botMessageEmptyRoomArray.length);

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        botEmptyRoomMessage = botMessageEmptyRoomArray[randomIndex];

        HabboScanner.getInstance().sendPrivateMessage(consoleUserId, botEmptyRoomMessage);
    }

    private void processTransactionsAndTerminate() {
        try {
            processTransactions();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        sayGoodbye();
    }

    private void processTransactions() throws SQLException {
        RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers();
        String currentOwnerName = roomInfoHandlers.getCurrentOwnerName();
        int roomId = roomInfoHandlers.getRoomId();

        List<String> transactions = DataDAO.retrieveDataTransactions(currentOwnerName, roomId);

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        if (transactions.isEmpty()) {
            String noTradesDetectedMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message").getProperty("no.trades.detected.message");

            HabboScanner.getInstance().sendPrivateMessage(consoleUserId, noTradesDetectedMessage);

            return;
        }

        String latestFurniPassedMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("latest.furni.passed.message");

        HabboScanner.getInstance().sendPrivateMessage(consoleUserId, latestFurniPassedMessage);

        scheduledExecutorService.schedule(() -> transactions.forEach(transaction ->
                HabboScanner.getInstance().sendPrivateMessage(consoleUserId, transaction)), 2, TimeUnit.SECONDS);
    }

    private void sayGoodbye() {
        String botGoodbyeMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("message").getProperty("bot.goodbye.message");
        String[] botGoodbyeMessageArray = botGoodbyeMessage.split("---");

        int randomIndex = (int) (Math.random() * botGoodbyeMessageArray.length);
        botGoodbyeMessage = botGoodbyeMessageArray[randomIndex];

        int userId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String finalMessage = botGoodbyeMessage;

        scheduledExecutorService.schedule(() ->
                HabboScanner.getInstance().sendPrivateMessage(userId, finalMessage), 3, TimeUnit.SECONDS);
    }
}
