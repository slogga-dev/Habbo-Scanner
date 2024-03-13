package org.slogga.habboscanner.logic.game.furni;

import java.sql.*;

import java.util.List;
import java.util.concurrent.*;

import org.slogga.habboscanner.dao.mysql.data.DataDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.ItemProcessor;
import org.slogga.habboscanner.logic.game.console.commands.follow.FollowConsoleCommand;

import org.slogga.habboscanner.handlers.RoomInfoHandlers;

import org.slogga.habboscanner.models.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.utils.DateUtils;

public class FurniTracker {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private List<String> transactions;

    public void manageFurniTracking(Date estimatedDate) {
        if (estimatedDate == null) return;

        ItemProcessor itemProcessor = HabboScanner.getInstance()
                .getConfigurator().getRoomInfoHandlers().getItemProcessor();
        Furni oldestFurni = itemProcessor.getOldestFurni();

        String oldestFurniInRoomMessage = prepareOldestFurniMessage(oldestFurni, estimatedDate);
        String rarestFurniInRoomMessage = prepareRarestFurniMessage(itemProcessor);

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        HabboActions.sendPrivateMessage(consoleUserId, oldestFurniInRoomMessage);
        HabboActions.sendPrivateMessage(consoleUserId, rarestFurniInRoomMessage);

        this.processTransactionsAndTerminate();
    }

    private String prepareOldestFurniMessage(Furni oldestFurni, Date estimatedDate) {
        String name = oldestFurni.getName();
        String classname = oldestFurni.getClassname();

        Timestamp timestamp = new Timestamp(estimatedDate.getTime());
        String formattedDate = DateUtils.formatTimestampToDate(timestamp);

        String oldestFurniInRoomMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties()
                .get("message").getProperty("oldest.furni.in.room.message");

        return oldestFurniInRoomMessage
                .replace("%name%", name)
                .replace("%classname%", classname)
                .replace("%date%", formattedDate);
    }

    private String prepareRarestFurniMessage(ItemProcessor itemProcessor) {
        String rarestFurniName = itemProcessor.getRarestFurniName();
        int highestSeenPieces = itemProcessor.getHighestSeenPieces();

        String rarestFurniInRoomMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties()
                .get("message").getProperty("rarest.furni.in.room.message");

        return rarestFurniInRoomMessage
                .replace("%rarestFurniName%", rarestFurniName)
                .replace("%highestSeenPieces%", Integer.toString(highestSeenPieces));
    }

    private void processTransactionsAndTerminate() {
        try {
            processTransactions();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        // Schedule the sayGoodbye method to run after all transactions have been processed
        scheduledExecutorService.schedule(this::sayGoodbye, 2L * transactions.size() + 1, TimeUnit.SECONDS);
    }

    private void processTransactions() throws SQLException {
        RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers();
        String currentOwnerName = roomInfoHandlers.getCurrentOwnerName();
        int roomId = roomInfoHandlers.getRoomId();

        transactions = DataDAO.retrieveDataTransactions(currentOwnerName, roomId);

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        if (transactions.isEmpty()) {
            String noTradesDetectedMessage = HabboScanner.getInstance().getConfigurator()
                    .getProperties().get("message").getProperty("no.trades.detected.message");

            HabboActions.sendPrivateMessage(consoleUserId, noTradesDetectedMessage);

            return;
        }

        String latestFurniPassedMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("latest.furni.passed.message");

        HabboActions.sendPrivateMessage(consoleUserId, latestFurniPassedMessage);

        transactions.forEach((transaction) -> scheduledExecutorService.schedule(() -> HabboActions.
                sendPrivateMessage(consoleUserId, transaction), 2, TimeUnit.SECONDS));
    }

    private void sayGoodbye() {
        String botGoodbyeMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("message").getProperty("bot.goodbye.message");
        String[] botGoodbyeMessageArray = botGoodbyeMessage.split("---");

        int randomIndex = (int) (Math.random() * botGoodbyeMessageArray.length);
        botGoodbyeMessage = botGoodbyeMessageArray[randomIndex];

        int userId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String finalMessage = botGoodbyeMessage;

        HabboActions.sendPrivateMessage(userId, finalMessage);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance().getConfigurator()
                .getConsoleHandlers().getCommands().get(CommandKeys.FOLLOW.getKey());

        followConsoleCommand.initiateBotAndRefreshRoomAccess();
    }
}
