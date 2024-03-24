package org.slogga.habboscanner.logic.game.furni;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.dao.mysql.data.*;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;
import org.slogga.habboscanner.utils.DateUtils;

public class FurniOwnershipTracker {
    public void trackTopFurniOwners(Map<String, String> itemDefinition, String classname) {
        int seenPieces = Integer.parseInt(itemDefinition.get("seen_pieces"));

        ArrayList<HashMap<String, Object>> topOwnersByFurniType;

        try {
            topOwnersByFurniType = DataDAO.getTopOwnersByFurniType(classname);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        if (topOwnersByFurniType.isEmpty()) return;

        StringJoiner topFurniOwnersReport = new StringJoiner(", ");

        for (HashMap<String, Object> ownerFurniCountRow : topOwnersByFurniType) {
            String ownerName = (String) ownerFurniCountRow.get("owner");
            Long furniCount = (Long) ownerFurniCountRow.get("furniCount");

            topFurniOwnersReport.add(ownerName + " (" + furniCount + ")");
        }

        String piecesObservedMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("pieces.observed.message");

        seenPieces = (seenPieces > 99999 ? Integer.parseInt((seenPieces / 1000) + "k") : seenPieces);

        piecesObservedMessage = piecesObservedMessage.replace("%seenPieces%", String.valueOf(seenPieces));

        String noFurniOwnersMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("no.furni.owners.message");

        String topFurniOwnersMessage = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("top.furni.owners.message");
        topFurniOwnersMessage = topFurniOwnersMessage
                .replace("%topFurniOwners%", topFurniOwnersReport.toString());

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        String message = piecesObservedMessage + (topFurniOwnersReport.length() == 0 ?
                noFurniOwnersMessage : topFurniOwnersMessage);

        HabboActions.sendPrivateMessage(consoleUserId, message);

        StringBuilder aggregatedMessage = HabboScanner.getInstance().getConfigurator()
                .getFurniMovementHandlers().getFurniHistoricalInfoBroadcaster().getAggregatedMessage();

        aggregatedMessage.append(message).append("\n");
    }

    public void manageFurniOwnershipHistory(int id, int userId) {
        ArrayList<HashMap<String, Object>> furniHistory;

        StringBuilder aggregatedMessage = HabboScanner.getInstance().getConfigurator()
                .getFurniMovementHandlers().getFurniHistoricalInfoBroadcaster().getAggregatedMessage();

        try {
            furniHistory = DataDAO.retrieveDataHistory(id, FurnitypeEnum.FLOOR.getType());
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        StringBuilder finalMessage = new StringBuilder();

        if (furniHistory.size() == 1) {
            String noTradeInfoMessage = HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("message")
                    .getProperty("no.trade.info.message");

            finalMessage.append(noTradeInfoMessage);

            HabboActions.sendPrivateMessage(userId, finalMessage.toString());

            aggregatedMessage.append(finalMessage);

            return;
        }

        String furniHistoryMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("furni.history.message");

        finalMessage.append(furniHistoryMessage);

        StringJoiner joiner = generateFurniHistoryReport(furniHistory);
        finalMessage.append(joiner);

        HabboActions.sendPrivateMessage(userId, finalMessage.toString());

        aggregatedMessage.append(finalMessage);
    }

    private StringJoiner generateFurniHistoryReport(ArrayList<HashMap<String, Object>> furniHistory) {
        StringJoiner joiner = new StringJoiner(", ");

        for (int index = furniHistory.size() - 1; index >= 0; index--) {
            HashMap<String, Object> historyRow = furniHistory.get(index);

            String owner = (String) historyRow.get("owner");
            Timestamp timestamp = (Timestamp) historyRow.get("timestamp");

            String formattedDate = DateUtils.formatTimestampToDate(timestamp);

            joiner.add(owner + " (" + formattedDate + ")");
        }

        return joiner;
    }
}
