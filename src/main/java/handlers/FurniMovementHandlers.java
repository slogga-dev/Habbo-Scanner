package handlers;

import java.io.IOException;
import java.sql.*;

import org.apache.commons.lang3.tuple.Triple;

import gearth.protocol.HMessage;

import database.dao.items.ItemsTimelineDAO;

import game.console.commands.follow.FollowConsoleCommand;
import game.furni.FurniInfoProvider;

import models.*;
import models.furnitype.FurnitypeEnum;

import scanner.HabboScanner;

import utils.DateUtils;

public class FurniMovementHandlers {
    private final FurniInfoProvider furniInfoProvider = new FurniInfoProvider();

    private long lastMovedTime = 0;

    private static final int MOVE_DELAY_IN_MILLISECONDS = 15 * 1000; // 15 seconds.

    public void onMoveWallItem(HMessage message) {
        handleMoveItem(message, FurnitypeEnum.WALL);
    }

    public void onMoveFurni(HMessage message) {
        handleMoveItem(message, FurnitypeEnum.FLOOR);
    }

    private void handleMoveItem(HMessage message, FurnitypeEnum type) {
        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance().getConsoleHandlers().getCommands().get(":follow");
        FollowingAction followingAction = followConsoleCommand.getFollowingAction();

        if (followingAction == FollowingAction.AUCTION) return;

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

        if (String.valueOf(estimatedDate).equals("1970-01-01")) {
            String furniDateCalculationErrorMessage = HabboScanner.getInstance()
                    .getMessageProperties().getProperty("furni.date.calculation.error.message");

            HabboScanner.getInstance().whisperMessage(furniDateCalculationErrorMessage);

            return;
        }

        if (estimatedDate == null) return;

        Timestamp timestamp = new Timestamp(estimatedDate.getTime());
        String formattedDate = DateUtils.formatTimestampToDate(timestamp);

        int consoleUserId = HabboScanner.getInstance().getConsoleHandlers().getUserId();

        furniInfoProvider.provideFurniInfo(id, type, formattedDate, consoleUserId);
    }

    public FurniInfoProvider getFurniInfoProvider() {
        return furniInfoProvider;
    }
}

