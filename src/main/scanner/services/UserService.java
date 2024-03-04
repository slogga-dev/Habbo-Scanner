package scanner.services;

import java.io.IOException;
import java.sql.SQLException;

import gearth.extensions.parsers.HEntity;

import scanner.database.dao.entities.habbo_users.HabboUsersDAO;

public class UserService {
    public static void insertUser(HEntity entity, int roomId) {
        int id = entity.getId();
        String name = entity.getName();
        String motto = entity.getMotto();
        String gender = entity.getGender().toString();
        String look = entity.getFigureId();

        try {
            HabboUsersDAO.insertUser(id, name, motto, gender, look, roomId);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void updateUser(HEntity entity, String motto, int seenTimes, int roomId) {
        int id = entity.getId();
        String name = entity.getName();
        String gender = entity.getGender().toString();
        String look = entity.getFigureId();

        try {
            HabboUsersDAO.updateUser(id, name, motto, gender, look, seenTimes, roomId);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
