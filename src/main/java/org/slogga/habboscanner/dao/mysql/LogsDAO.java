package org.slogga.habboscanner.dao.mysql;

import java.io.IOException;
import java.sql.*;

import org.slogga.habboscanner.dao.Database;

public class LogsDAO {
    public static void insertLog(String message) throws SQLException, IOException {
        String query = "INSERT INTO `logs` (`message`) VALUES (?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, message);
            statement.execute();
        }
    }
}
