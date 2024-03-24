package org.slogga.habboscanner.models.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;


@Getter
public enum ConvertFile {
    ITEMS("items"),
    ITEMS_TIMELINE("items_timeline");

    public static ConvertFile fromValue(String value) {
        return Arrays.stream(ConvertFile.values())
                .filter(mode -> Objects.equals(mode.getFile(), value))
                .findFirst()
                .orElse(null);
    }

    private final String file;

    ConvertFile(String file) {
        this.file = file;
    }
}
