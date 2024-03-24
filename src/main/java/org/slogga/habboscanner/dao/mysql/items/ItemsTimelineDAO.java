package org.slogga.habboscanner.dao.mysql.items;

import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.sql.*;

import org.slogga.habboscanner.dao.Database;

import org.slogga.habboscanner.models.furni.ItemTimeline;

public class ItemsTimelineDAO {
    public static Triple<Integer, ItemTimeline, ItemTimeline> selectClosestEntries(String type, Integer searchableId)
            throws SQLException, IOException {

        String query = "SELECT * from items_timeline WHERE type = ? AND id IN "
                + "((SELECT MAX(id) FROM items_timeline WHERE type = ? AND id < ?), "
                + "(SELECT MIN(id) FROM items_timeline WHERE type = ? AND id >= ?))";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, type);
            statement.setString(2, type);
            statement.setInt(3, searchableId);
            statement.setString(4, type);
            statement.setInt(5, searchableId);

            try (ResultSet resultSet = statement.executeQuery()) {
                ItemTimeline minimumEntry = null;
                ItemTimeline maximumEntry = null;

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    Date date = resultSet.getDate("date");

                    ItemTimeline itemTimeline = new ItemTimeline(id, date, type);

                    minimumEntry = (minimumEntry == null) ? itemTimeline : minimumEntry;
                    maximumEntry = itemTimeline;
                }

                return Triple.of(searchableId, minimumEntry, maximumEntry);
            }
        }
    }
}