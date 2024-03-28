package org.slogga.habboscanner.logic.commands;

import java.util.*;

import lombok.Data;

@Data
public abstract class CommandExecutor {
    protected Map<String, Command> commands = new HashMap<>();
    protected CommandExecutorProperties properties;

    public abstract void setupCommands();

    public CommandExecutor(CommandExecutorProperties properties){
        this.properties = properties;
    }
}
