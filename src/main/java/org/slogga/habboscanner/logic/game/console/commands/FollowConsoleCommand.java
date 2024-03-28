package org.slogga.habboscanner.logic.game.console.commands;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.commands.CommandFactory;
import org.slogga.habboscanner.logic.commands.common.follow.FollowCommand;
import org.slogga.habboscanner.logic.commands.common.start.StartCommand;
import org.slogga.habboscanner.models.enums.CommandKeys;
import org.slogga.habboscanner.models.enums.FollowingAction;
import org.slogga.habboscanner.models.enums.SourceType;

import java.util.Arrays;
import java.util.Optional;

public class FollowConsoleCommand extends FollowCommand {
    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        sourceType = SourceType.HABBO;

        String[] arguments = properties.getMessageText().split(" ", 2);

        Optional<String> followingActionString = Arrays.stream(arguments)
                .skip(1)
                .findFirst();
        followingAction = FollowingAction.fromValue(followingActionString
                .orElse(FollowingAction.DEFAULT.getAction()));

        super.execute(properties);
    }

    public void handleEmptyRoom() {
        initiateBotAndRefreshRoomAccess();
        sendEmptyRoomMessage();
    }

    public void initiateBotAndRefreshRoomAccess() {
        StartCommand startConsoleCommand = (StartCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.START.getKey());

        startConsoleCommand.setBotRunning(true);
        isFollowing = false;

        HabboScanner.getInstance()
                .getConfigurator()
                .getRoomEntryHandler().refreshLastRoomAccess();
    }

    private void sendEmptyRoomMessage() {
        String botEmptyRoomMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("bot.empty.room.message");
        String[] botMessageEmptyRoomArray = botEmptyRoomMessage.split("---");

        int randomIndex = (int) (Math.random() * botMessageEmptyRoomArray.length);

        botEmptyRoomMessage = botMessageEmptyRoomArray[randomIndex];
        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        HabboActions.sendMessage(consoleUserId, botEmptyRoomMessage);
    }
}
