package org.slogga.habboscanner.logic.game.commands.common.convert.files;

import java.io.*;
;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.common.convert.Converter;

import org.slogga.habboscanner.models.enums.ConvertFile;

public class ConvertItemsFile extends Converter {
    public void execute(CommandExecutorProperties properties) {
        super.execute(properties, ConvertFile.ITEMS);
    }

    public void processLine(String line, String cvsSplitBy,
                            PrintWriter printWriter, ConvertFile convertFile) {
        String[] furni = line.split(cvsSplitBy, -1);

        String classname = processField(furni, 2);
        String category = processField(furni, 4);
        String type = processField(furni, 6);
        String seenPieces = processField(furni, 8);

        printWriter.println("INSERT INTO " + convertFile.getFile() + " (classname, category, type, seen_pieces) VALUES ("
                + classname + ", " + category + ", " + type + ", " + seenPieces + ");");
    }
}
