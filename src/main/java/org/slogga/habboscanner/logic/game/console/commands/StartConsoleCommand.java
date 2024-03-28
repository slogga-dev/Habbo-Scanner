package org.slogga.habboscanner.logic.game.console.commands;

import org.slogga.habboscanner.logic.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.commands.common.start.StartCommand;

public class StartConsoleCommand extends StartCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        super.execute(properties);
    }
}
