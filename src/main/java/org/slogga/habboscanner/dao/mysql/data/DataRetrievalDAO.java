package org.slogga.habboscanner.dao.mysql.data;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.slogga.habboscanner.dao.Database;

public class DataRetrievalDAO {
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
}
