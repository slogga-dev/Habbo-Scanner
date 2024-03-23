package org.slogga.habboscanner.logic.game.commands.common.convert;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.Command;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.models.ConvertFile;

import java.io.*;
import java.util.Properties;

// It's a strategy
public abstract class Converter {
    protected void execute(CommandExecutorProperties properties, ConvertFile convertFile) {
        Properties messageProperties = HabboScanner.getInstance()
                .getConfigurator().getProperties().get("message");

        try {
            convertCSVToSQL(convertFile);

            String actionCompletedMessage = messageProperties.getProperty("action.completed.message");

            Command.sendMessage(actionCompletedMessage, properties);
        } catch (FileNotFoundException exception) {
            String timelineFileNotFoundMessage = messageProperties.getProperty(convertFile.getFile() + ".file.not.found.message");

            Command.sendMessage(timelineFileNotFoundMessage, properties);

            throw new RuntimeException(exception);
        } catch (IOException exception) {
            String ioExceptionMessage = messageProperties.getProperty("io.exception.message");

            Command.sendMessage(ioExceptionMessage, properties);

            throw new RuntimeException(exception);
        }
    }

    protected void convertCSVToSQL(ConvertFile convertFile) throws IOException {
        String cvsSplitBy = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        try (BufferedReader br = new BufferedReader(new FileReader(convertFile.getFile() + ".csv"));
             PrintWriter printWriter = new PrintWriter(new FileWriter("dao/" + convertFile.getFile() + ".sql", false))) {

            printWriter.println("DELETE FROM " + convertFile.getFile());

            br.readLine();

            String line;

            while ((line = br.readLine()) != null)
                processLine(line, cvsSplitBy, printWriter, convertFile);
        }
    }

    protected String processField(String[] field, int index) {
        if (field.length <= index || field[index].isEmpty())
            return "NULL";

        return "'" + field[index].replace("\"", "")
                .replace("'", "\\'") + "'";
    }

    protected abstract void processLine(String line, String cvsSplitBy,
                                        PrintWriter printWriter, ConvertFile convertFile);
}

