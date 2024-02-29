package database.dao;

import database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class NavigatorRoomsDAO {
    private static final Logger logger = LoggerFactory.getLogger(NavigatorRoomsDAO.class);

    public static void deleteNavigatorRooms() throws SQLException {
        String deleteQuery = "DELETE FROM `navigator_rooms`";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.execute();
        } catch (SQLException | IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    public static void insertNavigatorRooms(ArrayList<String> rooms) throws SQLException {
        String query = "INSERT INTO `navigator_rooms` (`id`, `name`, `description`, " +
                "`owner_name`, `users`, `max_users`, `access_type`) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (String room : rooms) {
                String[] data = room.split("<|~|>");

                statement.setInt(1, Integer.parseInt(data[0]));
                statement.setString(2, data[1]);
                statement.setString(3, data[2]);
                statement.setString(4, data[3]);
                statement.setInt(5, Integer.parseInt(data[4]));
                statement.setInt(6, Integer.parseInt(data[5]));
                statement.setString(7, data[6]);

                statement.execute();

                statement.clearParameters();
            }
        } catch (SQLException | IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
