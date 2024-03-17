package org.slogga.habboscanner.logic.game.commands;

public interface IExecuteCommand {
    void execute(CommandExecutorProperties properties);
    String getDescription();
}
