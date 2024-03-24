package org.slogga.habboscanner.logic.game.commands.console.commands;

import org.slogga.habboscanner.logic.game.commands.common.PauseCommand;
import org.slogga.habboscanner.logic.game.commands.*;

public class PauseConsoleCommand extends PauseCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        super.execute(properties);
    }
}
