package org.slogga.habboscanner.logic.game.commands.console.commands.convert.files;

import java.io.*;
import java.util.Properties;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.console.commands.convert.ConvertFile;

import org.slogga.habboscanner.HabboScanner;

public class ConvertItemsFile implements ConvertFile {
    @Override
    public void handle(int userId) {
        Properties messageProperties = HabboScanner.getInstance()
                .getConfigurator()
                .getProperties().get("message");

        try {
            convertCSVToSQL();

            String actionCompletedMessage = messageProperties.getProperty("action.completed.message");

            HabboActions.sendPrivateMessage(userId, actionCompletedMessage);
        } catch (FileNotFoundException exception) {
            String itemsFileNotFoundMessage = messageProperties.getProperty("items.file.not.found.message");

            HabboActions.sendPrivateMessage(userId, itemsFileNotFoundMessage);

            throw new RuntimeException(exception);
        } catch (IOException exception) {
            String ioExceptionMessage = messageProperties.getProperty("io.exception.message");

            HabboActions.sendPrivateMessage(userId, ioExceptionMessage);

            throw new RuntimeException(exception);
        }
    }

    public void convertCSVToSQL() throws IOException {
        String cvsSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        try (BufferedReader br = new BufferedReader(new FileReader("items.csv"));
             PrintWriter printWriter = new PrintWriter(new FileWriter("dao/items.sql", false))) {

            printWriter.println("DELETE FROM items;");

            br.readLine();

            String line;

            while ((line = br.readLine()) != null)
                processLine(line, cvsSplitBy, printWriter);
        }
    }

    private void processLine(String line, String cvsSplitBy, PrintWriter printWriter) {
        String[] furni = line.split(cvsSplitBy, -1);

        String classname = processField(furni, 2);
        String category = processField(furni, 4);
        String type = processField(furni, 6);
        String seenPieces = processField(furni, 8);

        printWriter.println("INSERT INTO items (classname, category, type, seen_pieces) VALUES ("
                + classname + ", " + category + ", " + type + ", " + seenPieces + ");");
    }

    private String processField(String[] timeline, int index) {
        if (timeline.length <= index || timeline[index].isEmpty())
            return "NULL";

        return "'" + timeline[index].replace("\"", "")
                .replace("'", "\\'") + "'";
    }
}
