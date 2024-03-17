package org.slogga.habboscanner.dao.mysql.data;

import java.io.IOException;
import java.sql.*;

import org.slogga.habboscanner.dao.Database;

import org.slogga.habboscanner.models.Furni;

public class DataActiveDAO {
    public static void insertData(Furni furni) throws SQLException, IOException {
        String query = "INSERT IGNORE INTO data_active (id, classname, name, owner, type, " +
                "room_id, extra_data, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, furni.getId());
            statement.setString(2, furni.getClassname());
            statement.setString(3, furni.getName());
            statement.setString(4, furni.getOwner());
            statement.setString(5, furni.getType().getType());
            statement.setInt(6, furni.getRoomId());
            statement.setString(7, furni.getExtraData());
            statement.setTimestamp(8, furni.getTimestamp());

            statement.execute();
        }
    }

    public static void deleteActiveData(int roomID) throws SQLException {
        String query = "DELETE FROM data_active WHERE room_ID = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, roomID);
            statement.execute();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
