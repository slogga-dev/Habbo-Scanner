package org.slogga.habboscanner.handlers;

import java.util.*;

import gearth.protocol.HMessage;

import lombok.*;

import org.slogga.habboscanner.logic.game.commands.*;

import org.slogga.habboscanner.logic.game.commands.common.follow.*;
import org.slogga.habboscanner.logic.game.commands.common.follow.actions.BaseFollowingAction;

import org.slogga.habboscanner.models.CommandKeys;

import org.slogga.habboscanner.HabboScanner;

@Data
public class ConsoleHandlers {
    private int userId;

    public void onNewConsole(HMessage message) {
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.enabled"));

        if (!isBotEnabled) return;

        userId = message.getPacket().readInteger();
        String messageText = message.getPacket().readString();

        setCommandExecutorProperties(message, messageText);

        handleFollowGoAway(messageText);

        for (Map.Entry<String, Command> entry : CommandFactory.commandExecutorInstance.getCommands().entrySet()) {
            if (!messageText.startsWith(entry.getKey())) continue;

            entry.getValue().execute(CommandFactory.commandExecutorInstance.getProperties());
        }
    }

    private void setCommandExecutorProperties(HMessage message, String messageText){
        CommandExecutorProperties commandExecutorProperties = new CommandExecutorProperties();

        commandExecutorProperties.setMessage(message);
        commandExecutorProperties.setMessageText(messageText);
        commandExecutorProperties.setUserId(userId);

        CommandFactory.getCommandExecutor(CommandExecutorType.CONSOLE, commandExecutorProperties);
    }

    private void handleFollowGoAway(String messageText){
        FollowCommand followCommand = (FollowCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.FOLLOW.getKey());

        if (followCommand == null) return;

        BaseFollowingAction follower = FollowingActionModeFactory
                .getFollowingActionStrategy(followCommand.getFollowingAction());

        // Execute a specific type of follow by type of action.
        if (follower != null && messageText.equals("go away")) follower.goAway();
    }
}