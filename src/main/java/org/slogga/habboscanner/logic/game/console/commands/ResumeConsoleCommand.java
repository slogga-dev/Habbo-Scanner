package org.slogga.habboscanner.logic.game.console.commands;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.logic.game.console.IConsoleCommand;
import org.slogga.habboscanner.logic.game.console.commands.follow.FollowConsoleCommand;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.models.CommandKeys;

public class ResumeConsoleCommand implements IConsoleCommand {
    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance().getConfigurator()
                .getConsoleHandlers().getCommands().get(CommandKeys.FOLLOW.getKey());

        followConsoleCommand.initiateBotAndRefreshRoomAccess();
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.resume.command.description");
    }
}
