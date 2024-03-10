package org.slogga.habboscanner.logic.game.furni;

import java.sql.*;

import java.util.List;
import java.util.concurrent.*;

import org.slogga.habboscanner.dao.mysql.data.DataDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.ItemProcessor;

import org.slogga.habboscanner.logic.game.console.commands.follow.FollowConsoleCommand;

import org.slogga.habboscanner.handlers.RoomInfoHandlers;

import org.slogga.habboscanner.models.Furni;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.utils.DateUtils;

public class FurniTracker {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void manageFurniTracking(Date estimatedDate) {
        FollowConsoleCommand followCommand = (FollowConsoleCommand)HabboScanner.getInstance()
                .getConfigurator().getConsoleHandlers().getCommands().get(":follow");
        if (estimatedDate == null || !followCommand.isFollowing())
            return;

        ItemProcessor itemProcessor = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().getItemProcessor();
        Furni oldestFurni = itemProcessor.getOldestFurni();

        String name = oldestFurni.getName();
        String classname = oldestFurni.getClassname();
        String formattedDate = DateUtils.formatToStandardDate(estimatedDate);
        String rarestFurniName = itemProcessor.getRarestFurniName();
        int highestSeenPieces = itemProcessor.getHighestSeenPieces();

        String oldestFurniInRoomMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties()
                .get("message").getProperty("oldest.furni.in.room.message");

        if (oldestFurniInRoomMessage == null) return;

        oldestFurniInRoomMessage = oldestFurniInRoomMessage
                .replace("%name%", name)
                .replace("%classname%", classname)
                .replace("%date%", formattedDate)
                .replace("%rarestFurniName%", rarestFurniName)
                .replace("%highestSeenPieces%", Integer.toString(highestSeenPieces));

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String finalMessage = oldestFurniInRoomMessage;

        scheduledExecutorService.schedule(() -> HabboActions.sendPrivateMessage(consoleUserId, finalMessage),
                1, TimeUnit.SECONDS);

        processTransactionsAndTerminate();
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

            HabboActions.sendPrivateMessage(consoleUserId, noTradesDetectedMessage);

            return;
        }

        String latestFurniPassedMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("latest.furni.passed.message");

        HabboActions.sendPrivateMessage(consoleUserId, latestFurniPassedMessage);

        scheduledExecutorService.schedule(() -> transactions.forEach(transaction ->
                HabboActions.sendPrivateMessage(consoleUserId, transaction)), 2, TimeUnit.SECONDS);
    }

    private void sayGoodbye() {
        String botGoodbyeMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("message").getProperty("bot.goodbye.message");
        String[] botGoodbyeMessageArray = botGoodbyeMessage.split("---");

        int randomIndex = (int) (Math.random() * botGoodbyeMessageArray.length);
        botGoodbyeMessage = botGoodbyeMessageArray[randomIndex];

        int userId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String finalMessage = botGoodbyeMessage;

        scheduledExecutorService.schedule(() -> HabboActions.sendPrivateMessage(userId, finalMessage), 3, TimeUnit.SECONDS);
    }
}
