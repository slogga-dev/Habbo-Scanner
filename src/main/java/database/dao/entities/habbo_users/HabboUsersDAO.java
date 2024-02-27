package database.dao.entities.habbo_users;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import database.Database;

public class HabboUsersDAO {
    public static ArrayList<HashMap<String, Object>> getUserByID(int id) throws SQLException, IOException {
        String query = "SELECT * FROM habbo_users WHERE id = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();

                ArrayList<HashMap<String, Object>> result = new ArrayList<>();

                while (resultSet.next()) {
                    HashMap<String, Object> row = new HashMap<>();

                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        String columnName = resultSetMetaData.getColumnName(columnIndex);
                        Object columnValue = resultSet.getObject(columnIndex);

                        row.put(columnName, columnValue);
                    }

                    result.add(row);
                }
                return result;
            }
        }
    }

    public static void insertUser(int id, String name, String motto, String gender, String look, int roomID) throws SQLException, IOException {
        String query = "INSERT INTO `habbo_users` (`id`, `name`, `motto`, `gender`, " +
                "`look`, `seen_times`, `room_id`) VALUES (?, ?, ?, ?, ?, 0, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, motto);
            statement.setString(4, gender);
            statement.setString(5, look);
            statement.setInt(6, roomID);

            statement.execute();
        }
    }

    public static void updateUser(int id, String name, String motto, String gender, String look, int seenTimes, int roomID) throws SQLException, IOException {
        String query = "UPDATE `habbo_users` SET `name` = ?, `motto` = ?, `gender` = ?, " +
                "`look` = ?, `seen_times` = ?, `room_id` = ?, `last_seen` = ? WHERE `id` = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, motto);
            statement.setString(3, gender);
            statement.setString(4, look);
            statement.setInt(5, seenTimes);
            statement.setInt(6, roomID);
            statement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            statement.setInt(8, id);

            statement.execute();
        }
    }
}
