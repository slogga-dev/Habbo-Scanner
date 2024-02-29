package database.dao;

import database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;


public class StatsDAO {
    private static final Logger logger = LoggerFactory.getLogger(StatsDAO.class);

    public static void insertStats(int onlineAmount, int roomsAmount, String timestamp) throws SQLException {
        String query = "INSERT INTO stats (online_amount, rooms_amount, timestamp) VALUES (?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, onlineAmount);
            statement.setInt(2, roomsAmount);
            statement.setString(3, timestamp);

            statement.executeUpdate();
        } catch (SQLException | IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
