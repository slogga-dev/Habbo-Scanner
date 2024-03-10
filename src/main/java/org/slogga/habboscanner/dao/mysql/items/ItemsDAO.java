package org.slogga.habboscanner.dao.mysql.items;

import java.io.IOException;
import java.sql.*;

import java.util.*;

import org.slogga.habboscanner.dao.Database;

public class ItemsDAO {
    public static Map<String, Map<String, String>> fetchItems() throws SQLException, IOException {
        Map<String, Map<String, String>> items = new HashMap<>();
        String query = "SELECT * FROM items";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();

                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    String columnName = metaData.getColumnName(columnIndex);
                    String columnValue = resultSet.getString(columnIndex);

                    row.put(columnName, columnValue);
                }

                String classname = resultSet.getString("classname");
                items.put(classname, row);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return items;
    }
}
