package org.slogga.habboscanner.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.tuple.Triple;

import java.sql.*;

import java.util.Arrays;

import gearth.extensions.parsers.*;

import gearth.protocol.HPacket;

import gearth.extensions.parsers.HFloorItem;
import gearth.protocol.HMessage;

import lombok.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.dao.mysql.items.ItemsTimelineDAO;

import org.slogga.habboscanner.logic.game.ItemProcessor;
import org.slogga.habboscanner.logic.game.console.commands.follow.*;
import org.slogga.habboscanner.logic.game.furni.FurniTracker;

import org.slogga.habboscanner.models.*;
import org.slogga.habboscanner.models.furnitype.FurnitypeEnum;

import org.slogga.habboscanner.utils.DateUtils;

@Data
public class ItemProcessingHandlers {
    private final FurniTracker furniTracker = new FurniTracker();

    @Getter
    private int lastFurniPlacedId;

    @Getter
    private FurnitypeEnum lastFurniPlacedType;

    public void onFloorItems(HMessage message) {
        HFloorItem[] items = HFloorItem.parse(message.getPacket());

        FurnitypeEnum type = FurnitypeEnum.FLOOR;
        RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers();

        ItemProcessor itemProcessor = roomInfoHandlers.getItemProcessor();
        int roomId = roomInfoHandlers.getRoomId();
        Arrays.stream(items).forEach(item -> itemProcessor.processFloorItem(item, type, roomId));

        Triple<Integer, ItemTimeline, ItemTimeline> closestEntries;

        Furni oldestFurni = itemProcessor.getOldestFurni();

        FollowConsoleCommand followConsoleCommand = (FollowConsoleCommand) HabboScanner.getInstance()
                .getConfigurator().getConsoleHandlers().getCommands().get(CommandKeys.FOLLOW.getKey());

        if (!followConsoleCommand.isFollowing()) return;

        if (oldestFurni.getId() == null)
            followConsoleCommand.handleEmptyRoom();

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

        furniTracker.manageFurniTracking(estimatedDate);
    }

    public void onWallItems(HMessage message) {
        HWallItem[] items = HWallItem.parse(message.getPacket());
        FurnitypeEnum type = FurnitypeEnum.WALL;

        RoomInfoHandlers roomInfoHandlers = HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers();

        ItemProcessor itemProcessor = roomInfoHandlers.getItemProcessor();
        int roomId = roomInfoHandlers.getRoomId();

        Arrays.stream(items).forEach(item -> itemProcessor.processWallItem(item, type, roomId));
    }

    public void onObjectAdd(HMessage message) {
        HPacket packet = message.getPacket();

        int id = packet.readInteger();

        if (id > 999999999)
            return;

        lastFurniPlacedId = id;
        lastFurniPlacedType = FurnitypeEnum.FLOOR;

        int typeId = packet.readInteger();

        int unknownVariable1 = packet.readInteger();
        int unknownVariable2 = packet.readInteger();
        int unknownVariable3 = packet.readInteger();
        String unknownVariable4 = packet.readString();
        String unknownVariable5 = packet.readString();
        int unknownVariable6 = packet.readInteger();

        int extradataCategory = packet.readInteger();
        String extradata = Arrays.toString(HStuff.readData(packet, extradataCategory));

        int unknownVariable7 = packet.readInteger();
        int unknownVariable8 = packet.readInteger();

        int ownerId = packet.readInteger();
        String ownerName = packet.readString();

        extradata = extradata.substring(1, extradata.length() - 1);

        byte[] bytes = extradata.getBytes(StandardCharsets.ISO_8859_1);
        extradata = new String(bytes, StandardCharsets.UTF_8);

        HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers().getItemProcessor()
                .handleFurniAddition(id, typeId, ownerId, ownerName, extradata);
    }

    public void onItemAdd(HMessage message) {
        HPacket packet = message.getPacket();

        int id = Integer.parseInt(packet.readString());

        if (id > 999999999)
            return;

        lastFurniPlacedId = id;
        lastFurniPlacedType = FurnitypeEnum.WALL;

        int typeId = packet.readInteger();

        String location = packet.readString();

        String extradata = packet.readString();

        int unknownVariable1 = packet.readInteger();
        int unknownVariable2 = packet.readInteger();

        int ownerId = packet.readInteger();
        String ownerName = packet.readString();

        byte[] bytes = extradata.getBytes(StandardCharsets.ISO_8859_1);
        extradata = new String(bytes, StandardCharsets.UTF_8);

        HabboScanner.getInstance().getConfigurator().getRoomInfoHandlers()
                .getItemProcessor().handleFurniAddition(id, typeId, ownerId, ownerName, extradata);
    }
}
