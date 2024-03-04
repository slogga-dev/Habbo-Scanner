package scanner.database.dao.entities;

import java.io.IOException;
import java.sql.*;

import scanner.database.Database;

public class PetsDAO {
    public static void insertPet(int id, String name, int roomID, String extraData) throws SQLException, IOException {
        String query = "INSERT IGNORE INTO `pets` (`id`, `name`, `room_id`, `extra_data`) VALUES (?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setInt(3, roomID);
            statement.setString(4, extraData);

            statement.execute();
        }
    }

}
