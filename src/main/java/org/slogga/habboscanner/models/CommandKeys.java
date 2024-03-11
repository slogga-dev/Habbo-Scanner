package org.slogga.habboscanner.models;

import lombok.Getter;

@Getter
public enum CommandKeys {
    START(":start"),
    PAUSE(":pause"),
    RESUME(":resume"),
    FOLLOW(":follow"),
    INFO(":info"),
    CONVERT(":convert"),
    UPDATE(":update"),
    MAKESAY(":makesay"),
    LOGOUT(":logout"),
    ENERGY_SAVING(":energy_saving"),
    COMMANDS(":commands");

    private final String key;

    CommandKeys(String key) {
        this.key = key;
    }
}

