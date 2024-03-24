package org.slogga.habboscanner.logic.game.commands.common;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.game.commands.Command;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.console.commands.FollowConsoleCommand;

import org.slogga.habboscanner.models.CommandKeys;

public class ResumeCommand extends Command {
    @Override
    public void execute(CommandExecutorProperties properties) {
        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory
                .commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

        followConsoleCommand.initiateBotAndRefreshRoomAccess();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("resume.command.description");
    }
}
