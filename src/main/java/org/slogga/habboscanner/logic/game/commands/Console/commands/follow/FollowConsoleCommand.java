package org.slogga.habboscanner.logic.game.commands.Console.commands.follow;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import gearth.protocol.*;

import lombok.Data;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandExecutorProperties;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.actions.AuctionFollowingActionMode;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.actions.FurniInfoFollowingActionMode;
import org.slogga.habboscanner.logic.game.commands.Console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.logic.game.commands.IExecuteCommand;
import org.slogga.habboscanner.logic.game.commands.Console.IConsoleCommand;

import org.slogga.habboscanner.models.*;

import org.slogga.habboscanner.HabboScanner;

@Data
public class FollowConsoleCommand implements IExecuteCommand {
    private final Map<FollowingAction, FollowingActionMode> actionModes = new HashMap<>();

    private FollowingAction followingAction;

    private boolean isFollowing;
    private SourceType sourceType;

    public FollowConsoleCommand() {
        actionModes.put(FollowingAction.FURNI_INFO, new FurniInfoFollowingActionMode());
        actionModes.put(FollowingAction.AUCTION, new AuctionFollowingActionMode());

        // Is set as false until it reaches the room; If for some reason it doesn't this doesn't work
        this.isFollowing = false;
    }

    @Override
    public void execute(CommandExecutorProperties properties) {
        properties.getMessage().setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());

        if (!startConsoleCommand.isBotRunning() || isFollowing) return;

        // Set :start command to false, so it cannot be called by another user.
        startConsoleCommand.setBotRunning(false);

        sourceType = SourceType.HABBO;
        System.out.println(properties.getMessageText());
        String[] arguments = properties.getMessageText().split(" ", 2);

        Optional<String> followingActionString = Arrays.stream(arguments)
                .skip(1)
                .findFirst();
        followingAction = FollowingAction.fromValue(followingActionString.orElse(FollowingAction.DEFAULT.getAction()));

        isFollowing = true;

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> HabboActions.followUser(properties.getUserId()), 1, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.follow.command.description");
    }

    public void handleEmptyRoom() {
        sendEmptyRoomMessage();

        String endOfFurniInfoModeMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("end.of.furni_info.mode.message");

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        HabboActions.sendPrivateMessage(consoleUserId, endOfFurniInfoModeMessage);

        this.initiateBotAndRefreshRoomAccess();
    }

    public void initiateBotAndRefreshRoomAccess() {
        Map<String, IExecuteCommand> commands = CommandFactory.commandExecutorInstance.getCommands();
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) commands.get(CommandKeys.START.getKey());

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

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
        botEmptyRoomMessage = botMessageEmptyRoomArray[randomIndex];

        HabboActions.sendPrivateMessage(consoleUserId, botEmptyRoomMessage);
    }
}
