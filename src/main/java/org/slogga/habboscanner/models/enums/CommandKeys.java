package org.slogga.habboscanner.models.enums;

import lombok.Getter;

@Getter
public enum CommandKeys {
    START("start"),
    PAUSE("pause"),
    RESUME("resume"),
    FOLLOW("follow"),
    INFO("info"),
    CONVERT("convert"),
    UPDATE("update"),
    SHUTDOWN("shutdown"),
    ENERGY_SAVING("energy_saving"),
    COMMANDS("commands"),
    AUCTION("auction"),
    INFO_FROM_ID("info_from_id");

    private final String key;

    CommandKeys(String key) {
        this.key = key;
    }
}

