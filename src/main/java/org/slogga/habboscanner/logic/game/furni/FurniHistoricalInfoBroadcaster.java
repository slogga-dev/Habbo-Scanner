package org.slogga.habboscanner.logic.game.furni;

import java.sql.*;

import java.util.*;
import java.util.concurrent.*;

import lombok.Getter;
import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.dao.mysql.data.DataRetrievalDAO;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;


public class FurniHistoricalInfoBroadcaster {
    private final FurniOwnershipTracker furniOwnershipTracker;

    @Getter
    private StringBuilder aggregatedMessage;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public FurniHistoricalInfoBroadcaster() {
        furniOwnershipTracker = new FurniOwnershipTracker();
    }

    public void broadcastFurniHistoryDetails(int id, FurnitypeEnum type, String formattedDate, int userId) {
        HashMap<String, Object> furni;

        aggregatedMessage = new StringBuilder();

        try {
            furni = DataRetrievalDAO.retrieveData(id, type.getType());
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

        if (furni.isEmpty()) {
            notifyUserOfEmptyRoom(formattedDate, userId);

            return;
        }

        processFurniInRoom(furni, formattedDate, id, userId);
    }

    private void notifyUserOfEmptyRoom(String formattedDate, int userId) {
        String dateNotification = HabboScanner
                .getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("date.notification.message");

        dateNotification = dateNotification.replace("%formattedDate%", formattedDate);

        aggregatedMessage.append(dateNotification).append("\n");

        HabboActions.sendPrivateMessage(userId, dateNotification);

        String furniJustPlacedMessage = HabboScanner
                .getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("furni.just.placed.message");

        HabboActions.sendPrivateMessage(userId, furniJustPlacedMessage);

        aggregatedMessage.append(furniJustPlacedMessage).append("\n");
    }

    private void processFurniInRoom(HashMap<String, Object> furni,
                                    String formattedDate, int id, int userId) {
        String name = (String) furni.get("name");
        String classname = (String) furni.get("classname");

        Map<String, String> itemDefinition = HabboScanner.getInstance()
                .getFurnidataConfigurator().getItems().get(classname);

        broadcastDetailedFurniInfo(itemDefinition, name, formattedDate, id, classname, userId);
    }

    private void broadcastDetailedFurniInfo(Map<String, String> itemDefinition, String name,
                                            String formattedDate, int id, String classname, int userId) {
        String furniNameDateInfoMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message").
                getProperty("furni.name.date.info.message");
        furniNameDateInfoMessage = furniNameDateInfoMessage.replace("%name%", name)
                .replace("%formattedDate%", formattedDate);

        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.enabled"));

        if (!isBotEnabled) {
            HabboActions.whisperMessage(furniNameDateInfoMessage);

            return;
        }

        String category = (itemDefinition != null) ? itemDefinition.get("category") : "";
        String itemCategoryMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("item.category.message");
        itemCategoryMessage = itemCategoryMessage.replace("%category%", category);

        String message = furniNameDateInfoMessage + (category.isEmpty() ? "" : "." + itemCategoryMessage);

        HabboActions.sendPrivateMessage(userId, message);

        aggregatedMessage.append(message).append("\n");

        assert itemDefinition != null;

        scheduledExecutorService.schedule(() -> furniOwnershipTracker.trackTopFurniOwners(itemDefinition, classname),
                1, TimeUnit.SECONDS);

        scheduledExecutorService.schedule(() -> furniOwnershipTracker.manageFurniOwnershipHistory(id, userId),
                2, TimeUnit.SECONDS);
    }
}
