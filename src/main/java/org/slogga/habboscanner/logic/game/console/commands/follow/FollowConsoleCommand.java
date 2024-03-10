package org.slogga.habboscanner.logic.game.console.commands.follow;

import java.util.*;
import java.util.concurrent.*;

import lombok.Data;
import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.ItemProcessor;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;
import gearth.protocol.*;

import org.slogga.habboscanner.logic.game.console.commands.follow.actions.*;
import org.slogga.habboscanner.logic.game.console.IConsoleCommand;

import org.slogga.habboscanner.handlers.RoomInfoHandlers;

import org.slogga.habboscanner.models.*;

import org.slogga.habboscanner.HabboScanner;

@Data
public class FollowConsoleCommand implements IConsoleCommand {
    private final Map<FollowingAction, FollowingActionMode> actionModes = new HashMap<>();

    private FollowingAction followingAction;

    private boolean isFollowing;
    private SourceType sourceType;

    public FollowConsoleCommand() {
        actionModes.put(FollowingAction.FURNI_INFO, new FurniInfoFollowingActionMode());
        actionModes.put(FollowingAction.AUCTION, new AuctionFollowingActionMode());

        // Is set as false until it reaches the room; if for some reason it doesn't this doesn't work
        this.isFollowing = false;
    }

    @Override
    public void execute(HMessage message, String messageText, int userId) {
        message.setBlocked(true);

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers().getCommands().get(":start");

        if (!startConsoleCommand.getIsBotRunning()) return;

        // set :start command to false, so it cannot be called by another user
        // we must implement a force follow for admin
        startConsoleCommand.setBotRunning(false);

        this.sourceType = SourceType.HABBO;

        String[] arguments = messageText.split(" ", 2);

        Optional<String> followingActionString = Arrays.stream(arguments)
                .skip(1)
                .findFirst();

        followingAction = FollowingAction.fromValue(followingActionString
                .orElse(FollowingAction.FURNI_INFO.getAction()));

        HabboActions.followUser(userId);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance()
                    .getConfigurator()
                    .getRoomInfoHandlers();
            RoomAccessMode roomAccessMode = roomInfoHandlers.getRoomAccessMode();

            switch (roomAccessMode) {
                case OPEN: {
                    // Since the bot reaches the room it is following the habbo that called him with :follow command

                    ItemProcessor itemProcessor = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().getItemProcessor();
                    Furni oldestFurni = itemProcessor.getOldestFurni();

                    if (oldestFurni.getId() == null) {
                        handleEmptyRoom(scheduledExecutorService, startConsoleCommand);

                        break;
                    }

                    this.isFollowing = true;

                    FollowingActionMode actionMode = actionModes.get(followingAction);
                    actionMode.handle();

                    break;
                }

                case LOCKED: {
                    String closedRoomAccessMessage = HabboScanner.getInstance()
                            .getConfigurator().getProperties().get("message").getProperty("closed.room.access.message");

                    HabboActions.sendPrivateMessage(userId, closedRoomAccessMessage);

                    break;
                }

                case UNKNOWN: {
                    String noRoomAccessMessage = HabboScanner.getInstance()
                            .getConfigurator().getProperties().get("message").getProperty("no.room.access.message");

                    HabboActions.sendPrivateMessage(userId, noRoomAccessMessage);

                    break;
                }
            }
        }, 1, TimeUnit.SECONDS);
        this.isFollowing = false;
    }

    @Override
    public void resetForStart() {
        this.isFollowing = false;
    }

    @Override
    public String getDescription() {
        return HabboScanner.getInstance().getConfigurator().getProperties().get("command_description")
                .getProperty("console.follow.command.description");
    }

    private void handleEmptyRoom(ScheduledExecutorService scheduledExecutorService, StartConsoleCommand startConsoleCommand) {
        sendEmptyRoomMessage();

        scheduledExecutorService.schedule(() -> {
            String endOfFurniInfoModeMessage = HabboScanner.getInstance().getConfigurator()
                    .getProperties().get("message").getProperty("end.of.furni_info.mode.message");

            int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();
            HabboActions.sendPrivateMessage(consoleUserId, endOfFurniInfoModeMessage);

            startConsoleCommand.setBotRunning(true);

            HabboScanner.getInstance()
                    .getConfigurator()
                    .getRoomInfoHandlers().refreshLastRoomAccess();
        }, 1, TimeUnit.SECONDS);
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
