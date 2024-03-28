package org.slogga.habboscanner.dao.mysql;

import org.slogga.habboscanner.dao.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;


public class StatsDAO {
    private static final Logger logger = LoggerFactory.getLogger(StatsDAO.class);

    public static void insertOrUpdateStats(int onlineAmount, int roomsAmount, String timestamp) {
        String query = "INSERT INTO stats (online_amount, rooms_amount, timestamp) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE online_amount = ?, rooms_amount = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, onlineAmount);
            statement.setInt(2, roomsAmount);
            statement.setString(3, timestamp);
            statement.setInt(4, onlineAmount);
            statement.setInt(5, roomsAmount);

            statement.executeUpdate();
        } catch (SQLException | IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
