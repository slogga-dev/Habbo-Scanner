package org.slogga.habboscanner.logic.commands.common;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.logic.commands.Command;
import org.slogga.habboscanner.logic.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.console.commands.FollowConsoleCommand;

import org.slogga.habboscanner.models.enums.CommandKeys;

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
