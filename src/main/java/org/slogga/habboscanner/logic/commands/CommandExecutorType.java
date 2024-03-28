package org.slogga.habboscanner.logic.commands;

import lombok.Getter;

@Getter
public enum CommandExecutorType {
    CONSOLE("console"),
    DISCORD("discord");

    private final String command;

    CommandExecutorType(String action) {
        this.command = action;
    }
}
