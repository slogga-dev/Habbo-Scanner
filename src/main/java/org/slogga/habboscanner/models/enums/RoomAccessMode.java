package org.slogga.habboscanner.models.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoomAccessMode {
    OPEN(0),
    LOCKED(1),
    UNKNOWN(-1);

    public static RoomAccessMode fromValue(int value) {
        return Arrays.stream(RoomAccessMode.values())
                .filter(mode -> mode.getAccessMode() == value)
                .findFirst()
                .orElse(RoomAccessMode.UNKNOWN);
    }

    private final int accessMode;

    RoomAccessMode(int accessMode) {
        this.accessMode = accessMode;
    }
}
