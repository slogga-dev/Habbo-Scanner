package scanner.utils;

import java.sql.*;

import java.text.SimpleDateFormat;

import java.time.*;

import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.tuple.Triple;

import scanner.models.ItemTimeline;

public class DateUtils {
    public static Date getLinearInterpolatedDate(Triple<Integer, ItemTimeline, ItemTimeline> closestEntries) {
        int id = closestEntries.getLeft();

        int lowestID = closestEntries.getMiddle().getId();
        Date lowestDate = closestEntries.getMiddle().getDate();

        int highestID = closestEntries.getRight().getId();
        Date highestDate = closestEntries.getRight().getDate();

        long rise = highestDate.getTime() - lowestDate.getTime();
        int run = highestID - lowestID;

        if (run == 0)
            return null;

        long milliseconds = lowestDate.getTime() + (rise / run) * (id - lowestID);

        return new Date(milliseconds);
    }

    public static String formatToStandardDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        return formatter.format(date);
    }

    public static String formatTimestampToDate(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");

        return dateFormat.format(timestamp);
    }

    public static String getCurrentDateTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return now.format(formatter);
    }
}
