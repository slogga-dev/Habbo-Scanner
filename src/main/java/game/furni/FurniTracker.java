package game.furni;

import java.sql.*;

import java.util.List;
import java.util.concurrent.*;

import database.dao.data.DataDAO;

import game.ItemProcessor;

import game.console.commands.follow.FollowConsoleCommand;

import handlers.RoomInfoHandlers;

import models.Furni;

import scanner.HabboScanner;

import utils.DateUtils;

public class FurniTracker {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void manageFurniTracking(Date estimatedDate) {
        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":follow");

        boolean isFollowingFriend = followConsoleCommand.getIsFollowingFriend();

        if (!isFollowingFriend) {
            sendEmptyRoomMessage();

            return;
        }

        ItemProcessor itemProcessor = HabboScanner.getInstance().getRoomInfoHandlers().getItemProcessor();
        Furni oldestFurni = itemProcessor.getOldestFurni();

        String name = oldestFurni.getName();
        String classname = oldestFurni.getClassname();
        String formattedDate = DateUtils.formatToStandardDate(estimatedDate);
        String rarestFurniName = itemProcessor.getRarestFurniName();
        int highestSeenPieces = itemProcessor.getHighestSeenPieces();

        String oldestFurniInRoomMessage = HabboScanner.getInstance().getMessageProperties().getProperty("oldest.furni.in.room.message");

        if (oldestFurniInRoomMessage == null) return;

        oldestFurniInRoomMessage = oldestFurniInRoomMessage
                .replace("%name%", name)
                .replace("%classname%", classname)
                .replace("%date%", formattedDate)
                .replace("%rarestFurniName%", rarestFurniName)
                .replace("%highestSeenPieces%", Integer.toString(highestSeenPieces));

        String finalMessage = oldestFurniInRoomMessage;

        scheduledExecutorService.schedule(() -> HabboScanner.getInstance()
                .whisperMessage(finalMessage), 1, TimeUnit.SECONDS);

        scheduleTransactionProcessing();

        followConsoleCommand.setIsFollowingFriend(false);
    }

    private void sendEmptyRoomMessage() {
        String botEmptyRoomMessage = HabboScanner.getInstance().getMessageProperties().getProperty("bot.empty.room.message");
        String[] botMessageEmptyRoomArray = botEmptyRoomMessage.split("---");

        int randomIndex = (int) (Math.random() * botMessageEmptyRoomArray.length);
        botEmptyRoomMessage = botMessageEmptyRoomArray[randomIndex];

        HabboScanner.getInstance().whisperMessage(botEmptyRoomMessage);
    }

    private void scheduleTransactionProcessing() {
        try {
            processTransactions();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        sayGoodbye();
    }

    private void processTransactions() throws SQLException {
        RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getRoomInfoHandlers();
        String currentOwnerName = roomInfoHandlers.getCurrentOwnerName();
        int roomId = roomInfoHandlers.getRoomId();

        List<String> transactions = DataDAO.retrieveDataTransactions(currentOwnerName, roomId);

        int consoleUserId = HabboScanner.getInstance().getConsoleHandlers().getUserId();

        if (transactions.isEmpty()) {
            String noTradesDetectedMessage = HabboScanner.getInstance()
                    .getMessageProperties().getProperty("no.trades.detected.message");

            HabboScanner.getInstance().sendPrivateMessage(consoleUserId, noTradesDetectedMessage);

            return;
        }

        String latestFurniPassedMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("latest.furni.passed.message");

        HabboScanner.getInstance().sendPrivateMessage(consoleUserId, latestFurniPassedMessage);

        scheduledExecutorService.schedule(() -> transactions.forEach(transaction ->
                HabboScanner.getInstance().sendPrivateMessage(consoleUserId, transaction)), 2, TimeUnit.SECONDS);
    }

    private void sayGoodbye() {
        String botGoodbyeMessage = HabboScanner.getInstance().getMessageProperties().getProperty("bot.goodbye.message");
        String[] botGoodbyeMessageArray = botGoodbyeMessage.split("---");

        int randomIndex = (int) (Math.random() * botGoodbyeMessageArray.length);
        botGoodbyeMessage = botGoodbyeMessageArray[randomIndex];

        int userId = HabboScanner.getInstance().getConsoleHandlers().getUserId();
        String finalMessage = botGoodbyeMessage;

        scheduledExecutorService.schedule(() ->
                HabboScanner.getInstance().sendPrivateMessage(userId, finalMessage), 3, TimeUnit.SECONDS);
    }
}
