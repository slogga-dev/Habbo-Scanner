package services;

import java.io.IOException;
import java.sql.SQLException;

import database.dao.entities.BotsDAO;

import gearth.extensions.parsers.HEntity;
import gearth.extensions.parsers.HEntityType;

public class BotService {
    public static void insertBot(HEntity entity, String extradata,  int roomId) {
        int id = entity.getId();
        String name = entity.getName();
        String motto = entity.getMotto();
        String look = entity.getFigureId();
        boolean isOldBot = entity.getEntityType() == HEntityType.OLD_BOT;

        try {
            BotsDAO.insertBot(id, name, motto, look, roomId, isOldBot, extradata);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
