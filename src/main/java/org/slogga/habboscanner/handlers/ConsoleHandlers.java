package org.slogga.habboscanner.handlers;

import java.util.*;

import gearth.protocol.HMessage;

import lombok.*;

import org.slogga.habboscanner.logic.game.commands.*;

import org.slogga.habboscanner.logic.game.commands.common.follow.FollowingActionModeFactory;
import org.slogga.habboscanner.logic.game.commands.common.follow.actions.FurniInfoFollowingAction;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.models.FollowingAction;

@Setter
@Getter
public class ConsoleHandlers {
    private int userId;

    public void onNewConsole(HMessage message) {
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator().getProperties().get("bot").getProperty("bot.enabled"));

        if (!isBotEnabled) return;

        userId = message.getPacket().readInteger();
        String messageText = message.getPacket().readString();

        setCommandExecutorProperties(message, messageText);

        FurniInfoFollowingAction furniInfoFollowingAction = (FurniInfoFollowingAction) FollowingActionModeFactory
                .getFollowingActionStrategy(FollowingAction.FURNI_INFO);

        if (messageText.equals("go away")) furniInfoFollowingAction.goAway();

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
}