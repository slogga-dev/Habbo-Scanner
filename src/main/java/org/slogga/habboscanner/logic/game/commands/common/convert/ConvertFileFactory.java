package org.slogga.habboscanner.logic.game.commands.common.convert;

import java.util.*;

import org.slogga.habboscanner.logic.game.commands.common.convert.files.*;

import org.slogga.habboscanner.models.ConvertFile;

import java.util.function.Supplier;

public class ConvertFileFactory {
    private static final Map<ConvertFile, Supplier<Converter>> fileMap = new HashMap<>();

    static {
        fileMap.put(ConvertFile.ITEMS, ConvertItemsFile::new);
        fileMap.put(ConvertFile.ITEMS_TIMELINE, ConvertItemsTimelineFile::new);
    }

    public static Converter getFollowingActionStrategy(ConvertFile file) {
        Supplier<Converter> fileSupplier = fileMap.get(file);

        if (fileSupplier == null)
            throw new IllegalArgumentException("Invalid follow mode key");

        return fileSupplier.get();
    }
}
