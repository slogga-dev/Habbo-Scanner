package org.slogga.habboscanner.logic.game.commands.common.start;

import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;

// It's a strategy
public interface IStarter {
    void execute(CommandExecutorProperties properties);
}
