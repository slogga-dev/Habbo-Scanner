package org.slogga.habboscanner.logic.game.commands;

import java.util.*;

import lombok.Data;

@Data
public abstract class CommandExecutor {
    protected Map<String, IExecuteCommand> commands = new HashMap<>();
    protected CommandExecutorProperties properties;

    public abstract void setCommands();

    public CommandExecutor(CommandExecutorProperties properties){
        this.properties = properties;
    }
}
