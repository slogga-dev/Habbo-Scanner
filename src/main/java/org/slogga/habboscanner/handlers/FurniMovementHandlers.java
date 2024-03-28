package org.slogga.habboscanner.handlers;

import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.sql.*;

import lombok.Getter;

import gearth.protocol.HMessage;

import org.slogga.habboscanner.dao.mysql.items.ItemsTimelineDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.commands.CommandFactory;
import org.slogga.habboscanner.logic.commands.common.follow.FollowCommand;
import org.slogga.habboscanner.logic.game.furni.FurniHistoricalInfoBroadcaster;

import org.slogga.habboscanner.models.enums.CommandKeys;
import org.slogga.habboscanner.models.enums.FollowingAction;
import org.slogga.habboscanner.models.enums.FurnitypeEnum;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.models.furni.ItemTimeline;
import org.slogga.habboscanner.utils.DateUtils;

public class FurniMovementHandlers {
    @Getter
    private final FurniHistoricalInfoBroadcaster furniHistoricalInfoBroadcaster = new FurniHistoricalInfoBroadcaster();

    private long lastMovedTime = 0;

    private static final int MOVE_DELAY_IN_MILLISECONDS = 2000;

    public void onMoveWallItem(HMessage message) {
        handleMoveItem(message, FurnitypeEnum.WALL);
    }

    public void onMoveFurni(HMessage message) {
        handleMoveItem(message, FurnitypeEnum.FLOOR);
    }

    private void handleMoveItem(HMessage message, FurnitypeEnum type) {
        FollowCommand followCommand = (FollowCommand) CommandFactory.commandExecutorInstance
                .getCommands().get(CommandKeys.FOLLOW.getKey());

        if (followCommand != null && followCommand.getFollowingAction() == FollowingAction.DEFAULT) return;

        int id = message.getPacket().readInteger();

        if (id > 999999999) return;

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastMovedTime < MOVE_DELAY_IN_MILLISECONDS) return;

        lastMovedTime = currentTime;

        Triple<Integer, ItemTimeline, ItemTimeline> closestEntries;

        try {
            closestEntries = ItemsTimelineDAO.selectClosestEntries(type.getType(), id);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        Date estimatedDate = DateUtils.getLinearInterpolatedDate(closestEntries);

        int consoleUserId = HabboScanner.getInstance().getConfigurator().getConsoleHandlers().getUserId();

        if (String.valueOf(estimatedDate).equals("1970-01-01")) {
            String furniDateCalculationErrorMessage = HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("message")
                    .getProperty("furni.date.calculation.error.message");

            HabboActions.sendMessage(consoleUserId, furniDateCalculationErrorMessage);

            return;
        }

        if (estimatedDate == null) return;

        Timestamp timestamp = new Timestamp(estimatedDate.getTime());
        String formattedDate = DateUtils.formatTimestampToDate(timestamp);

        furniHistoricalInfoBroadcaster.broadcastFurniHistoryDetails(id, type, formattedDate, consoleUserId);
    }
}

