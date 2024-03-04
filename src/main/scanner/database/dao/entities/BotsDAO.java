package scanner.database.dao.entities;

import java.io.IOException;
import java.sql.*;

import scanner.database.Database;

public class BotsDAO {
    public static void insertBot(int id, String name, String motto, String look,
                                 int roomID, boolean isOldBot, String extraData) throws SQLException, IOException {
        String query = "INSERT IGNORE INTO `bots` (`id`, `name`, `motto`, `look`, " +
                "`room_id`, `old_bot`, `extra_data`) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, motto);
            statement.setString(4, look);
            statement.setInt(5, roomID);
            statement.setBoolean(6, isOldBot);
            statement.setString(7, extraData);

            statement.execute();
        }
    }
}
