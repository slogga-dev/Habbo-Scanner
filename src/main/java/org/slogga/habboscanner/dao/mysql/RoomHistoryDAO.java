package org.slogga.habboscanner.dao.mysql;

import java.io.IOException;

import java.sql.*;

import org.slogga.habboscanner.dao.Database;

public class RoomHistoryDAO {
    public static void insertRoomHistory(int userID, int roomID) throws SQLException, IOException {
        String query = "INSERT IGNORE INTO `room_history` (`user_id`, `room_id`) VALUES (?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            statement.setInt(2, roomID);

            statement.execute();
        }
    }
}
