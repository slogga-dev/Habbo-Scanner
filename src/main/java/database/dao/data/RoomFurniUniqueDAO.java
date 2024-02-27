package database.dao.data;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import database.Database;

public class RoomFurniUniqueDAO {
    public static ArrayList<HashMap<String, Object>> getTopOwnersByFurniType(String classname) throws SQLException, IOException {
        String query = "SELECT owner, COUNT(*) as furniCount FROM data_unique " +
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
}
