package org.slogga.habboscanner.logic.game.commands;

import java.util.*;

import lombok.Getter;

@Getter
public enum CommandExecutorType {
    CONSOLE("console"),
    DISCORD("discord");

    private final String command;

    public static CommandExecutorType fromValue(String value) {
        return Arrays.stream(CommandExecutorType.values())
                .filter(mode -> Objects.equals(mode.getCommand(), value))
                .findFirst()
                .orElse(null);
    }

    CommandExecutorType(String action) {
        this.command = action;
    }
}
