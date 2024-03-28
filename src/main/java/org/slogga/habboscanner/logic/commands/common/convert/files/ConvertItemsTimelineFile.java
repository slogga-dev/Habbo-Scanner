package org.slogga.habboscanner.logic.commands.common.convert.files;

import java.io.*;

import org.slogga.habboscanner.logic.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.commands.common.convert.Converter;

import org.slogga.habboscanner.models.enums.ConvertFile;

public class ConvertItemsTimelineFile extends Converter {
    public void execute(CommandExecutorProperties properties) {
        super.execute(properties, ConvertFile.ITEMS_TIMELINE);
    }

    public void processLine(String line, String cvsSplitBy, PrintWriter printWriter, ConvertFile convertFile) {
        String[] timeLine = line.split(cvsSplitBy, -1);

        String floorDate = processField(timeLine, 0);
        String floorId = processField(timeLine, 1);

        if (!floorDate.equals("NULL") && !floorId.equals("NULL") && !floorId.isEmpty()) {
            printWriter.println("INSERT INTO " + convertFile.getFile() + " (date, id, type) VALUES ("
                    + floorDate + ", " + floorId + ", 'Floor');");
        }

        String wallDate = processField(timeLine, 4);
        String wallId = processField(timeLine, 5);

        if (!wallDate.equals("NULL") && !wallId.equals("NULL") && !wallId.isEmpty()) {
            printWriter.println("INSERT INTO " + convertFile.getFile() + " (date, id, type) VALUES ("
                    + wallDate + ", " + wallId + ", 'Wall');");
        }
    }
}
