package org.slogga.habboscanner.handlers.item;

import java.util.Arrays;

import gearth.extensions.parsers.*;
import gearth.protocol.HMessage;

import org.slogga.habboscanner.HabboScanner;


import org.slogga.habboscanner.handlers.room.RoomEntryHandler;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.ItemProcessor;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.common.follow.*;

import org.slogga.habboscanner.models.*;
import org.slogga.habboscanner.models.furnitype.*;

public class ItemPlacementHandlers {
    public void onFloorItems(HMessage message) {
        boolean isBotEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.enabled"));

        IFollower follower;

        if (!isBotEnabled) {
            follower = FollowingActionModeFactory
                    .getFollowingActionStrategy(FollowingAction.DEFAULT);

            follower.execute(message);

            return;
        }

        FollowCommand followCommand = (FollowCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.FOLLOW.getKey());

        // Check if the bot is called by a follow.
        if (followCommand == null || !followCommand.isFollowing()) return;

        follower = FollowingActionModeFactory
                .getFollowingActionStrategy(followCommand.getFollowingAction());

        // Execute a specific type of follow by type of action.
        if (follower != null)
            follower.execute(message);
    }

    public void onWallItems(HMessage message) {
        HWallItem[] items = HWallItem.parse(message.getPacket());
        FurnitypeEnum type = FurnitypeEnum.WALL;

        RoomEntryHandler roomEntryHandler = HabboScanner.getInstance().getConfigurator().getRoomEntryHandler();

        ItemProcessor itemProcessor = roomEntryHandler.getItemProcessor();
        int roomId = roomEntryHandler.getRoomId();

        Arrays.stream(items).forEach(item -> itemProcessor.processWallItem(item, type, roomId));
    }
}
