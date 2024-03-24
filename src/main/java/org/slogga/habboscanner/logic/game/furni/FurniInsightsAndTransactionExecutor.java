package org.slogga.habboscanner.logic.game.furni;

import java.sql.*;

import java.util.List;
import java.util.concurrent.*;

import org.slogga.habboscanner.dao.mysql.data.DataDAO;

import org.slogga.habboscanner.logic.game.*;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.console.commands.FollowConsoleCommand;

import org.slogga.habboscanner.models.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.utils.DateUtils;

public class FurniInsightsAndTransactionExecutor {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private List<String> transactions;

    public void executeTransactionsAndProvideFurniInsights(Date estimatedDate) {
        if (estimatedDate == null) return;

        ItemProcessor itemProcessor = HabboScanner.getInstance()
                .getConfigurator().getRoomEntryHandler().getItemProcessor();
        Furni oldestFurni = itemProcessor.getOldestFurni();

        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.enabled"));

        String oldestFurniInRoomMessage = generateOldestFurniInsight(oldestFurni, estimatedDate);

        if (!isBotEnabled) {
            scheduledExecutorService.schedule(() -> HabboActions.whisperMessage(oldestFurniInRoomMessage),
                    1, TimeUnit.SECONDS);

            return;
        }

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String rarestFurniInRoomMessage = generateRarestFurniInsight(itemProcessor);

        HabboActions.sendPrivateMessage(consoleUserId, oldestFurniInRoomMessage);
        HabboActions.sendPrivateMessage(consoleUserId, rarestFurniInRoomMessage);

        this.executeTransactionsAndTerminateSession();
    }

    private String generateOldestFurniInsight(Furni oldestFurni, Date estimatedDate) {
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

    private String generateRarestFurniInsight(ItemProcessor itemProcessor) {
        String rarestFurniName = itemProcessor.getRarestFurniName();
        int highestSeenPieces = itemProcessor.getHighestSeenPieces();

        String rarestFurniInRoomMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties()
                .get("message").getProperty("rarest.furni.in.room.message");

        return rarestFurniInRoomMessage
                .replace("%rarestFurniName%", rarestFurniName)
                .replace("%highestSeenPieces%", Integer.toString(highestSeenPieces));
    }

    private void executeTransactionsAndTerminateSession() {
        performFurniTransactions();

        /*
         Schedule the dispatchGoodbyeMessageAndUpdateAccess method to
         run after all transactions have been processed.
         */
        scheduledExecutorService.schedule(this::dispatchGoodbyeMessageAndUpdateAccess,
                2L * transactions.size() + 1, TimeUnit.SECONDS);
    }

    private void performFurniTransactions() {
        String currentOwnerName = HabboScanner.getInstance()
                .getConfigurator().getRoomDetailsHandlers()
                .getRoomDetails().getCurrentOwnerName();
        int roomId = HabboScanner.getInstance().getConfigurator().getRoomEntryHandler().getRoomId();

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

    private void dispatchGoodbyeMessageAndUpdateAccess() {
        String botGoodbyeMessage = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("message").getProperty("bot.goodbye.message");
        String[] botGoodbyeMessageArray = botGoodbyeMessage.split("---");

        int randomIndex = (int) (Math.random() * botGoodbyeMessageArray.length);
        botGoodbyeMessage = botGoodbyeMessageArray[randomIndex];

        int userId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String finalMessage = botGoodbyeMessage;

        HabboActions.sendPrivateMessage(userId, finalMessage);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.FOLLOW.getKey());

        followConsoleCommand.initiateBotAndRefreshRoomAccess();
    }
}
