package game.console.commands.follow;

import java.util.*;
import java.util.concurrent.*;

import game.console.commands.start.StartConsoleCommand;
import gearth.protocol.*;

import game.console.commands.follow.actions.*;
import game.console.ConsoleCommand;

import handlers.RoomInfoHandlers;

import models.*;

import scanner.HabboScanner;

public class FollowConsoleCommand implements ConsoleCommand {
    private final Map<FollowingAction, FollowingActionMode> actionModes = new HashMap<>();

    private FollowingAction followingAction;

    private boolean isFollowingFriend = false;
    private SourceType sourceType;

    public FollowConsoleCommand() {
        actionModes.put(FollowingAction.FURNI_INFO, new FurniInfoFollowingActionMode());
        actionModes.put(FollowingAction.AUCTION, new AuctionFollowingActionMode());
    }

    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":start");

        if (!startConsoleCommand.getIsBotRunning()) return;

        startConsoleCommand.setIsBotRunning(false);

        this.sourceType = SourceType.HABBO;

        String[] arguments = messageText.split(" ", 2);

        Optional<String> followingActionString = Arrays.stream(arguments)
                .skip(1)
                .findFirst();

        followingAction = FollowingAction.fromValue(followingActionString
                .orElse(FollowingAction.FURNI_INFO.getAction()));

        HabboScanner.getInstance().followUser(userId);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getRoomInfoHandlers();
            RoomAccessMode roomAccessMode = roomInfoHandlers.getRoomAccessMode();

            switch (roomAccessMode) {
                case OPEN: {
                    isFollowingFriend = true;

                    FollowingActionMode actionMode = actionModes.get(followingAction);

                    actionMode.handle();

                    break;
                }

                case LOCKED: {
                    String closedRoomAccessMessage = HabboScanner.getInstance().getMessageProperties().getProperty("closed.room.access.message");

                    HabboScanner.getInstance().sendPrivateMessage(userId, closedRoomAccessMessage);

                    break;
                }

                case UNKNOWN: {
                    String noRoomAccessMessage = HabboScanner.getInstance().getMessageProperties().getProperty("no.room.access.message");

                    HabboScanner.getInstance().sendPrivateMessage(userId, noRoomAccessMessage);

                    break;
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getCommandDescriptionProperties()
                .getProperty("console.follow.command.description");
    }

    public Map<FollowingAction, FollowingActionMode> getActionModes() {
        return actionModes;
    }

    public FollowingAction getFollowingAction() {
        return followingAction;
    }

    public boolean getIsFollowingFriend() {
        return isFollowingFriend;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setIsFollowingFriend(boolean isFollowingFriend) {
        this.isFollowingFriend = isFollowingFriend;
    }

    public void setFollowingAction(FollowingAction followingAction) {
        this.followingAction = followingAction;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }
}
