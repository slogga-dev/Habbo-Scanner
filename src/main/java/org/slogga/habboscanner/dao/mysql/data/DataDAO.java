package org.slogga.habboscanner.dao.mysql.data;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.slf4j.*;

import org.slogga.habboscanner.models.Furni;

import org.slogga.habboscanner.dao.Database;

public class DataDAO {
    private static final Logger logger = LoggerFactory.getLogger(DataDAO.class);

    public static HashMap<String, Object> retrieveData(int id, String type) throws SQLException {
        String query = "SELECT * FROM data WHERE id = ? AND type = ? LIMIT 1";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, type);

            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                int columnCount = resultSetMetaData.getColumnCount();

                HashMap<String, Object> row = new HashMap<>();

                if (resultSet.next()) {
                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        String columnName = resultSetMetaData.getColumnName(columnIndex);
                        Object columnValue = resultSet.getObject(columnIndex);

                        row.put(columnName, columnValue);
                    }
                }

                return row;
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static List<String> retrieveDataTransactions(String ownerName, int roomId) {
        List<String> transactions = new ArrayList<>();

        String query = "WITH transaction_data AS (" +
                "SELECT " +
                "CASE " +
                "WHEN current_owner = ? THEN CONCAT(name, ' ricevuto da ', previous_owner, ' il ', DATE_FORMAT(timestamp, '%e %M %Y')) " +
                "WHEN current_owner != ? AND previous_owner = ? THEN CONCAT(name, ' dato a ', current_owner, ' il ', DATE_FORMAT(timestamp, '%e %M %Y')) " +
                "ELSE NULL " +
                "END AS transaction, " +
                "timestamp " +
                "FROM (" +
                "SELECT " +
                "name, " +
                "owner AS current_owner, " +
                "timestamp, " +
                "LAG(owner) OVER (PARTITION BY id ORDER BY timestamp) AS previous_owner " +
                "FROM data " +
                "WHERE " +
                "(id, type) IN (SELECT id, type FROM data WHERE room_id = ?) " +
                "AND id IN (SELECT id FROM data GROUP BY id, type HAVING COUNT(*) > 1)" +
                ") AS subquery " +
                "WHERE previous_owner IS NOT NULL" +
                ") " +
                "SELECT transaction " +
                "FROM transaction_data " +
                "WHERE transaction IS NOT NULL " +
                "ORDER BY timestamp DESC " +
                "LIMIT 3";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ownerName);
            statement.setString(2, ownerName);
            statement.setString(3, ownerName);
            statement.setInt(4, roomId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String transaction = resultSet.getString("transaction");

                    transactions.add(transaction);
                }
            }
        } catch (SQLException | IOException exception) {
            logger.error(exception.getMessage());
        }

        return transactions;
    }

    public static ArrayList<HashMap<String, Object>> getTopOwnersByFurniType(String classname) throws SQLException, IOException {
        String query = "SELECT owner, COUNT(*) as furniCount FROM data " +
                "WHERE classname = ? GROUP BY owner ORDER BY furniCount  DESC LIMIT 3";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, classname);

            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData setMetaData = resultSet.getMetaData();
                int columnCount = setMetaData.getColumnCount();

                ArrayList<HashMap<String, Object>> result = new ArrayList<>();

                while (resultSet.next()) {
                    HashMap<String, Object> row = new HashMap<>();

                    for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                        String columnName = setMetaData.getColumnName(columnIndex);
                        Object columnValue = resultSet.getObject(columnIndex);

                        row.put(columnName, columnValue);
                    }

                    result.add(row);
                }

                return result;
            }
        }
    }

    public static ArrayList<HashMap<String, Object>> retrieveDataHistory(int id, String type) throws SQLException {
        String query = "SELECT owner, timestamp from data WHERE id IN " +
                "(SELECT id FROM data WHERE (id,type) = (?,?)) ORDER BY timestamp DESC";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, type);

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
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void insertData(Furni furni) throws SQLException, IOException {
        String query = "INSERT IGNORE INTO data (id, classname, name, owner, type, "
                + "room_id, extra_data, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE extra_data = IF(extra_data IS NULL OR extra_data = '', VALUES(extra_data), extra_data);";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, furni.getId());
            statement.setString(2, furni.getClassname());
            statement.setString(3, furni.getName());
            statement.setString(4, furni.getOwner());
            statement.setString(5, furni.getType().getType());
            statement.setInt(6, furni.getRoomId());
            statement.setString(7, furni.getExtraData());
            statement.setTimestamp(8, furni.getTimestamp());

            statement.execute();
        }
    }

    public static void updateOwner(String oldOwner, String newOwner) throws SQLException, IOException {
        String updateQuery = "UPDATE IGNORE data SET owner = ? WHERE owner = ?";
        String deleteQuery = "DELETE FROM data WHERE owner = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, newOwner);
            updateStatement.setString(2, oldOwner);
            updateStatement.execute();
        }

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setString(1, oldOwner);
            deleteStatement.execute();
        }
    }
}
