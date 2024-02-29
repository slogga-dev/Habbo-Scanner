package game.furni;

import java.io.IOException;
import java.sql.*;

import java.util.*;
import java.util.concurrent.*;

import database.dao.data.DataUniqueDAO;

import scanner.HabboScanner;

import database.dao.data.DataDAO;

import models.furnitype.FurnitypeEnum;

import utils.DateUtils;

public class FurniInfoProvider {
    public void provideFurniInfo(int id, FurnitypeEnum type, String formattedDate, int userId) {
        ArrayList<HashMap<String, Object>> roomFurni;

        try {
            roomFurni = DataDAO.retrieveData(id, type.getType());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (roomFurni.isEmpty()) {
            handleEmptyRoom(formattedDate, userId);

            return;
        }

        handleRoomWithFurni(roomFurni, formattedDate, id, userId);
    }

    private void handleEmptyRoom(String formattedDate, int userId) {
        String dateNotification = HabboScanner.getInstance().getMessageProperties().getProperty("date.notification.message");

        dateNotification = dateNotification.replace("%formattedDate%", formattedDate);

        HabboScanner.getInstance().sendPrivateMessage(userId, dateNotification);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            String furniJustPlacedMessage = HabboScanner.getInstance()
                    .getMessageProperties().getProperty("furni.just.placed.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, furniJustPlacedMessage);

            scheduledExecutorService.shutdown();
        }, 4, TimeUnit.SECONDS);
    }

    private void handleRoomWithFurni(ArrayList<HashMap<String, Object>> roomFurni,
                                     String formattedDate, int id, int userId) {
        HashMap<String, Object> row = roomFurni.get(0);

        String name = (String) row.get("name");
        String classname = (String) row.get("classname");

        Map<String, String> itemDefinition = HabboScanner.getInstance().getItems().get(classname);

        handleItemDefinition(itemDefinition, name, formattedDate, classname, userId);
        handleFurniHistory(id, userId);
    }

    private void handleItemDefinition(Map<String, String> itemDefinition, String name,
                                      String formattedDate, String classname, int userId) {
        String furniNameDateInfoMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("furni.name.date.info.message");

        furniNameDateInfoMessage = furniNameDateInfoMessage.replace("%name%", name)
                .replace("%formattedDate%", formattedDate);

        String category = (itemDefinition != null) ? itemDefinition.get("category") : "";

        String itemCategoryMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("item.category.message");

        itemCategoryMessage = itemCategoryMessage.replace("%category%", category);

        String message = furniNameDateInfoMessage + (category.isEmpty() ? "" : "." + itemCategoryMessage);

        HabboScanner.getInstance().sendPrivateMessage(userId, message);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            if (itemDefinition == null) return;

            handleUserWithMorePieces(itemDefinition, classname);
        }, 4500, TimeUnit.MILLISECONDS);
    }

    private void handleUserWithMorePieces(Map<String, String> itemDefinition, String classname) {
        int seenPieces = Integer.parseInt(itemDefinition.get("seen_pieces"));

        ArrayList<HashMap<String, Object>> topOwnersByFurniType;

        try {
            topOwnersByFurniType = DataUniqueDAO.getTopOwnersByFurniType(classname);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        if (topOwnersByFurniType.isEmpty()) return;

        StringJoiner topFurniOwnersReport = new StringJoiner(", ");

        for (HashMap<String, Object> ownerFurniCountRow : topOwnersByFurniType) {
            String ownerName = (String) ownerFurniCountRow.get("owner");
            int furniCount = ((Long) ownerFurniCountRow.get("uniqueFurniCount")).intValue();

            topFurniOwnersReport.add(ownerName + " (" + furniCount + ")");
        }

        String piecesObservedMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("pieces.observed.message");

        seenPieces = (seenPieces > 99999 ? Integer.parseInt((seenPieces / 1000) + "k") : seenPieces);

        piecesObservedMessage = piecesObservedMessage.replace("%seenPieces%", String.valueOf(seenPieces));

        String noFurniOwnersMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("no.furni.owners.message");
        String topFurniOwnersMessage = HabboScanner.getInstance()
                .getMessageProperties().getProperty("top.furni.owners.message");

        topFurniOwnersMessage = topFurniOwnersMessage
                .replace("%topFurniOwners%", topFurniOwnersReport.toString());

        String message = piecesObservedMessage + (topFurniOwnersReport.length() == 0 ?
                noFurniOwnersMessage : topFurniOwnersMessage);

        HabboScanner.getInstance().whisperMessage(message);
    }

    private void handleFurniHistory(int id, int userId) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.schedule(() -> {
            ArrayList<HashMap<String, Object>> furniHistory;

            try {
                furniHistory = DataDAO.retrieveDataHistory(id, FurnitypeEnum.FLOOR.getType());
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }

            StringBuilder finalMessage = new StringBuilder();

            if (furniHistory.size() == 1) {
                String noTradeInfoMessage = HabboScanner.getInstance()
                        .getMessageProperties().getProperty("no.trade.info.message");

                finalMessage.append(noTradeInfoMessage);

                return;
            }

            String furniHistoryMessage = HabboScanner.getInstance()
                    .getMessageProperties().getProperty("furni.history.message");

            finalMessage.append(furniHistoryMessage);

            StringJoiner joiner = getFurniHistoryStringJoiner(furniHistory);

            finalMessage.append(joiner);

            HabboScanner.getInstance().sendPrivateMessage(userId, finalMessage.toString());
        }, 5, TimeUnit.SECONDS);
    }

    private static StringJoiner getFurniHistoryStringJoiner(ArrayList<HashMap<String, Object>> furniHistory) {
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
