package game.console.commands.convert.files;

import java.io.*;
import java.util.Properties;

import game.console.commands.convert.ConvertFile;

import scanner.HabboScanner;

public class ConvertTimelineFile implements ConvertFile {
    @Override
    public void handle(int userId) {
        Properties messageProperties = HabboScanner.getInstance().getMessageProperties();

        try {
            convertCSVToSQL();

            String actionCompletedMessage = messageProperties.getProperty("action.completed.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, actionCompletedMessage);
        } catch (FileNotFoundException exception) {
            String timelineFileNotFoundMessage = messageProperties.getProperty("timeline.file.not.found.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, timelineFileNotFoundMessage);

            throw new RuntimeException(exception);
        } catch (IOException exception) {
            String ioExceptionMessage = messageProperties.getProperty("io.exception.message");

            HabboScanner.getInstance().sendPrivateMessage(userId, ioExceptionMessage);

            throw new RuntimeException(exception);
        }
    }

    private void convertCSVToSQL() throws IOException {
        String cvsSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("timeline.csv"));
             PrintWriter printWriter = new PrintWriter(new FileWriter("database/items_timeline.sql", false))) {

            printWriter.println("DELETE FROM items_timeline;");

            bufferedReader.readLine();

            String line;

            while ((line = bufferedReader.readLine()) != null)
                processLine(line, cvsSplitBy, printWriter);
        }
    }

    private void processLine(String line, String cvsSplitBy, PrintWriter printWriter) {
        String[] timeLine = line.split(cvsSplitBy, -1);

        String floorDate = processField(timeLine, 0);
        String floorId = processField(timeLine, 1);

        if (!floorDate.equals("NULL") && !floorId.equals("NULL") && !floorId.isEmpty()) {
            printWriter.println("INSERT INTO items_timeline (date, id, type) VALUES ("
                    + floorDate + ", " + floorId + ", 'Floor');");
        }

        String wallDate = processField(timeLine, 4);
        String wallId = processField(timeLine, 5);

        if (!wallDate.equals("NULL") && !wallId.equals("NULL") && !wallId.isEmpty()) {
            printWriter.println("INSERT INTO items_timeline (date, id, type) VALUES ("
                    + wallDate + ", " + wallId + ", 'Wall');");
        }
    }

    private String processField(String[] timeLine, int index) {
        if (timeLine.length <= index || timeLine[index].isEmpty())
            return "NULL";

        return "'" + timeLine[index].replace("\"", "")
                .replace("'", "\\'") + "'";
    }
}
