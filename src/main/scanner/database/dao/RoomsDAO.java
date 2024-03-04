package scanner.database.dao;

import scanner.database.Database;

import java.io.IOException;

import java.sql.*;

public class RoomsDAO {
    public static void insertRoom(int id, String name, int ownerId, String ownerName) throws SQLException, IOException {
        String query = "INSERT IGNORE INTO `rooms` (`id`, `name`, `owner_id`, `owner_name`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `owner_name` = VALUES(`owner_name`)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setInt(3, ownerId);
            statement.setString(4, ownerName);

            statement.execute();
        }
    }
}
