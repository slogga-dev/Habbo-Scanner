package org.slogga.habboscanner.logic.commands.common.follow.actions;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

import org.apache.commons.lang3.tuple.Triple;

import gearth.extensions.parsers.HFloorItem;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.HabboScanner;
import org.slogga.habboscanner.dao.mysql.items.ItemsTimelineDAO;

import org.slogga.habboscanner.handlers.room.RoomEntryHandler;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.ItemProcessor;
import org.slogga.habboscanner.logic.commands.CommandFactory;
import org.slogga.habboscanner.logic.commands.common.follow.IFollower;
import org.slogga.habboscanner.logic.game.console.commands.FollowConsoleCommand;

import org.slogga.habboscanner.models.enums.CommandKeys;
import org.slogga.habboscanner.models.enums.FurnitypeEnum;

import org.slogga.habboscanner.models.furni.Furni;
import org.slogga.habboscanner.models.furni.ItemTimeline;
import org.slogga.habboscanner.utils.DateUtils;

public class BaseFollowingAction implements IFollower {
    protected Date estimatedDate;

    @Override
    public void execute(HMessage message) {
        HFloorItem[] items = HFloorItem.parse(message.getPacket());
        RoomEntryHandler roomEntryHandler = HabboScanner.getInstance().getConfigurator().getRoomEntryHandler();

        ItemProcessor itemProcessor = roomEntryHandler.getItemProcessor();
        int roomId = roomEntryHandler.getRoomId();
        Arrays.stream(items).forEach(item -> itemProcessor.processFloorItem(item, FurnitypeEnum.FLOOR, roomId));

        Triple<Integer, ItemTimeline, ItemTimeline> closestEntries;
        Furni oldestFurni = itemProcessor.getOldestFurni();

        if (oldestFurni.getId() == null) {
            goAway();

            return;
        }

        try {
            closestEntries = ItemsTimelineDAO.selectClosestEntries(FurnitypeEnum.FLOOR.getType(), oldestFurni.getId());
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        estimatedDate = DateUtils.getLinearInterpolatedDate(closestEntries);
    }

    public void goAway() {
        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        String endOfFurniInfoModeMessage = HabboScanner.getInstance().getConfigurator()
                .getProperties().get("message").getProperty("end.of.furni_info.mode.message");

        HabboActions.sendMessage(consoleUserId, endOfFurniInfoModeMessage);

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) CommandFactory.
                commandExecutorInstance.getCommands().get(CommandKeys.FOLLOW.getKey());

        followConsoleCommand.handleEmptyRoom();
    }
}
