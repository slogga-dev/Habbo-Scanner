package org.slogga.habboscanner.logic.game.furni.info;

import java.sql.*;

import java.util.*;
import java.util.concurrent.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.dao.mysql.data.DataDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;


public class FurniInfoProvider {
    private final FurniInfoHandler furniInfoHandler;

    public FurniInfoProvider() {
        furniInfoHandler = new FurniInfoHandler();
    }

    public void provideFurniInfo(int id, FurnitypeEnum type, String formattedDate, int userId) {
        ArrayList<HashMap<String, Object>> roomFurni;

        try {
            roomFurni = DataDAO.retrieveData(id, type.getType());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (roomFurni.isEmpty()) {
            notifyUserAboutEmptyRoom(formattedDate, userId);

            return;
        }

        processRoomWithFurni(roomFurni, formattedDate, id, userId);
    }

    private void notifyUserAboutEmptyRoom(String formattedDate, int userId) {
        String dateNotification = HabboScanner
                .getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("date.notification.message");

        dateNotification = dateNotification.replace("%formattedDate%", formattedDate);

        HabboActions.sendPrivateMessage(userId, dateNotification);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        String furniJustPlacedMessage = HabboScanner
                .getInstance()
                .getConfigurator()
                .getProperties()
                .get("message")
                .getProperty("furni.just.placed.message");

        HabboActions.sendPrivateMessage(userId, furniJustPlacedMessage);

        scheduledExecutorService.shutdown();
    }

    private void processRoomWithFurni(ArrayList<HashMap<String, Object>> roomFurni,
                                      String formattedDate, int id, int userId) {
        HashMap<String, Object> row = roomFurni.get(0);

        String name = (String) row.get("name");
        String classname = (String) row.get("classname");

        Map<String, String> itemDefinition = HabboScanner.getInstance()
                .getFurnidataConfigurator().getItems().get(classname);

        sendFurniDetailsToUser(itemDefinition, name, formattedDate, classname, userId);
        furniInfoHandler.handleFurniHistory(id, userId);
    }

    private void sendFurniDetailsToUser(Map<String, String> itemDefinition, String name,
                                        String formattedDate, String classname, int userId) {
        String furniNameDateInfoMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message").
                getProperty("furni.name.date.info.message");

        furniNameDateInfoMessage = furniNameDateInfoMessage.replace("%name%", name)
                .replace("%formattedDate%", formattedDate);

        String category = (itemDefinition != null) ? itemDefinition.get("category") : "";

        String itemCategoryMessage = HabboScanner.getInstance().getConfigurator().getProperties().get("message")
                .getProperty("item.category.message");

        itemCategoryMessage = itemCategoryMessage.replace("%category%", category);

        String message = furniNameDateInfoMessage + (category.isEmpty() ? "" : "." + itemCategoryMessage);

        HabboActions.sendPrivateMessage(userId, message);

        assert itemDefinition != null;

        furniInfoHandler.handleUserWithMorePieces(itemDefinition, classname);
    }
}
