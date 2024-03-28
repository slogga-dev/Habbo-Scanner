package org.slogga.habboscanner.logic.commands.common.start;

import org.slogga.habboscanner.logic.commands.CommandExecutorProperties;

// It's a strategy
public interface IStarter {
    void execute(CommandExecutorProperties properties);
}
