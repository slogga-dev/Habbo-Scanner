package org.slogga.habboscanner.dao.mysql.data;

import java.io.IOException;
import java.sql.*;

import org.slogga.habboscanner.dao.Database;
import org.slogga.habboscanner.models.Furni;

public class DataModificationDAO {
    public static void insertData(Furni furni) throws SQLException, IOException {
        String query = "INSERT IGNORE INTO data (id, classname, name, owner, type, "
                + "room_id, extra_data, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE extra_data = IF(extra_data IS NULL OR extra_data = '', VALUES(extra_data), extra_data);";

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

    public static void updateOwner(String oldOwner, String newOwner) throws SQLException, IOException {
        String updateQuery = "UPDATE IGNORE data SET owner = ? WHERE owner = ?";
        String deleteQuery = "DELETE FROM data WHERE owner = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, newOwner);
            updateStatement.setString(2, oldOwner);
            updateStatement.execute();
        }

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setString(1, oldOwner);
            deleteStatement.execute();
        }
    }
}
