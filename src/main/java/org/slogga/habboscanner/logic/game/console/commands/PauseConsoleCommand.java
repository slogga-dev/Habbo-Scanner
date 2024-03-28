package org.slogga.habboscanner.logic.game.console.commands;

import org.slogga.habboscanner.logic.commands.common.PauseCommand;
import org.slogga.habboscanner.logic.commands.*;

public class PauseConsoleCommand extends PauseCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        super.execute(properties);
    }
}
