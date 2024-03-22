package org.slogga.habboscanner.logic.game.commands.console.commands;

import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.common.follow.FollowConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;
import org.slogga.habboscanner.models.CommandKeys;

public class ResumeConsoleCommand implements IExecuteCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory
                .commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

        followConsoleCommand.initiateBotAndRefreshRoomAccess();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.resume.command.description");
    }
}
