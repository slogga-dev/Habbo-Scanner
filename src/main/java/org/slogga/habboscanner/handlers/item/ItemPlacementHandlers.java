package org.slogga.habboscanner.handlers.item;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.lang3.tuple.Triple;

import gearth.extensions.parsers.*;
import gearth.protocol.HMessage;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.dao.mysql.items.ItemsTimelineDAO;
import org.slogga.habboscanner.handlers.room.RoomEntryHandler;
import org.slogga.habboscanner.logic.game.ItemProcessor;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.FollowConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.Console.commands.follow.FollowingActionMode;
import org.slogga.habboscanner.logic.game.furni.FurniInsightsAndTransactionExecutor;
import org.slogga.habboscanner.models.*;
import org.slogga.habboscanner.models.furnitype.*;
import org.slogga.habboscanner.utils.DateUtils;

public class ItemPlacementHandlers {
    private final FurniInsightsAndTransactionExecutor furniInsightsAndTransactionExecutor = new FurniInsightsAndTransactionExecutor();

    public void onFloorItems(HMessage message) {
        HFloorItem[] items = HFloorItem.parse(message.getPacket());

        FurnitypeEnum type = FurnitypeEnum.FLOOR;
        RoomEntryHandler roomEntryHandler = HabboScanner.getInstance().getConfigurator().getRoomEntryHandler();

        ItemProcessor itemProcessor = roomEntryHandler.getItemProcessor();
        int roomId = roomEntryHandler.getRoomId();
        Arrays.stream(items).forEach(item -> itemProcessor.processFloorItem(item, type, roomId));

        Triple<Integer, ItemTimeline, ItemTimeline> closestEntries;

        Furni oldestFurni = itemProcessor.getOldestFurni();

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

        if (!followConsoleCommand.isFollowing()) return;

        if (oldestFurni.getId() == null) {
            followConsoleCommand.handleEmptyRoom();

            return;
        }

        FollowingActionMode actionMode = followConsoleCommand.getActionModes()
                .get(followConsoleCommand.getFollowingAction());

        if (actionMode != null)
            actionMode.handle();

        try {
            closestEntries = ItemsTimelineDAO.selectClosestEntries(type.getType(), oldestFurni.getId());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        Date estimatedDate = DateUtils.getLinearInterpolatedDate(closestEntries);

        if (followConsoleCommand.getFollowingAction() != FollowingAction.DEFAULT) return;

        furniInsightsAndTransactionExecutor.executeTransactionsAndProvideFurniInsights(estimatedDate);
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
