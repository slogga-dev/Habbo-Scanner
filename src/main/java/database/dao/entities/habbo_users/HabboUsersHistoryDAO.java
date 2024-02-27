package database.dao.entities.habbo_users;

import java.io.IOException;
import java.sql.*;

import database.Database;

public class HabboUsersHistoryDAO {
    public static void addUserToHistory(int id, String name, String motto, String gender, String look) throws SQLException, IOException {
        String query = "INSERT INTO `habbo_users_history` (`id`, `name`, `motto`, `gender`, `look`) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = Database.getInstance().getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, motto);
            statement.setString(4, gender);
            statement.setString(5, look);

            statement.execute();
        }
    }

}
