package org.slogga.habboscanner.logic.game.commands.console.commands;

import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;

import org.slogga.habboscanner.logic.game.commands.common.ResumeCommand;

public class ResumeConsoleCommand extends ResumeCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        super.execute(properties);
    }
}
