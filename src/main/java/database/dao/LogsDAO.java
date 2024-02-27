package database.dao;

import java.io.IOException;
import java.sql.*;

import database.Database;

public class LogsDAO {
    public static void insertLog(String message) throws SQLException, IOException {
        String query = "INSERT INTO `scanner_logs` (`message`) VALUES (?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, message);
            statement.execute();
        }
    }
}
