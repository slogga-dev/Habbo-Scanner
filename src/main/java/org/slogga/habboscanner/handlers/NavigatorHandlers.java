package org.slogga.habboscanner.handlers;

import java.sql.SQLException;

import java.util.*;
import java.util.concurrent.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.commands.CommandFactory;
import org.slogga.habboscanner.logic.commands.common.start.StartCommand;
import org.slogga.habboscanner.logic.commands.common.start.StartModeFactory;
import org.slogga.habboscanner.logic.commands.common.start.modes.StartBotInActiveRooms;

import gearth.protocol.*;

import org.slogga.habboscanner.dao.mysql.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.models.enums.CommandKeys;
import org.slogga.habboscanner.models.enums.RoomAccessMode;
import org.slogga.habboscanner.utils.DateUtils;

public class NavigatorHandlers {
    private static final int ROOM_VISIT_INTERVAL_IN_MILLISECONDS = 15 * 60 * 1000; // 15 minutes.

    private final Map<Integer, Long> roomTimestamps = new HashMap<>();

    public void onNavigatorSearchResultBlocks(HMessage message) {
        CompletableFuture.runAsync(() -> {
            StartCommand startConsoleCommand = (StartCommand) CommandFactory.commandExecutorInstance
                    .getCommands().get(CommandKeys.START.getKey());

            if (!startConsoleCommand.isBotRunning()) return;

            HPacket packet = message.getPacket();

            String category = packet.readString();
            String searchQuery = packet.readString();
            int openState = packet.readInteger();

            boolean isActiveRoomsModeEnabled = checkIfIsActiveRoomsModeEnabled();

            // Checks if the room is open.
            if (openState == RoomAccessMode.OPEN.getAccessMode()) return;

            String code = packet.readString();
            String data = packet.readString();
            int action = packet.readInteger();
            int closed = packet.readInteger();
            boolean mode = packet.readBoolean();

            int roomsFoundAmount = packet.readInteger();

            int totalRoomUserAmount = 0;

            ArrayList<String> rooms = new ArrayList<>();

            for (int roomIndex = 1; roomIndex <= roomsFoundAmount; roomIndex++) {
                int roomId = packet.readInteger();

                String roomName = packet.readString();

                int ownerId = packet.readInteger();
                String ownerName = packet.readString();

                int accessMode = packet.readInteger();
                int roomUserAmount = packet.readInteger();

                totalRoomUserAmount = totalRoomUserAmount + roomUserAmount;

                if (roomUserAmount == 0) roomsFoundAmount--;

                int maximumRoomUserAmount = packet.readInteger();
                String roomDescription = packet.readString();

                int tradeMode = packet.readInteger();
                int score = packet.readInteger();
                int ranking = packet.readInteger();
                int categoryId = packet.readInteger();
                int tagAmount = message.getPacket().readInteger();

                if (tagAmount > 0) {
                    String tag1 = message.getPacket().readString();

                    if (tagAmount == 2) {
                        String tag2 = message.getPacket().readString();
                    }
                }

                int multiUse = message.getPacket().readInteger();

                if ((multiUse & 1) > 0) {
                    String officialRoomPicRef = message.getPacket().readString();
                }

                if ((multiUse & 2) > 0) {
                    int groupId = message.getPacket().readInteger();
                    String groupName = message.getPacket().readString();
                    String groupBadgeCode = message.getPacket().readString();
                }

                if ((multiUse & 4) > 0) {
                    String roomAdName = message.getPacket().readString();
                    String roomAdDescription = message.getPacket().readString();
                    int roomAdExpiresInMin = message.getPacket().readInteger();
                }

                long currentTime = System.currentTimeMillis();
                long lastRoomTime = roomTimestamps.getOrDefault(roomId, 0L);

                boolean isRoomOpen = accessMode == RoomAccessMode.OPEN.getAccessMode();
                boolean isTimeForRoomVisit = currentTime - lastRoomTime > ROOM_VISIT_INTERVAL_IN_MILLISECONDS;

                if (isRoomOpen && (isActiveRoomsModeEnabled || isTimeForRoomVisit)) {
                    roomTimestamps.put(roomId, currentTime);

                    HabboActions.moveToRoom(roomId);
                }

                String room = roomId + "<|~|>" + roomName + "<|~|>" + roomDescription +
                        "<|~|>" + ownerName + "<|~|>" + roomUserAmount + "<|~|>" +
                        maximumRoomUserAmount + "<|~|>" + accessMode;

                rooms.add(room);
            }

            boolean isStatisticsInsertionActive = Boolean.parseBoolean(HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("bot").getProperty("statistics.insertion.active"));

            if (!isStatisticsInsertionActive) return;

            String currentDateTime = DateUtils.getCurrentDateTime();

            StatsDAO.insertOrUpdateStats(totalRoomUserAmount, roomsFoundAmount, currentDateTime);

            try {
                NavigatorRoomsDAO.deleteNavigatorRooms();
                NavigatorRoomsDAO.insertNavigatorRooms(rooms);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    private boolean checkIfIsActiveRoomsModeEnabled(){
        boolean isActiveRoomsModeEnabled = Boolean.parseBoolean(HabboScanner.getInstance().getConfigurator()
                .getProperties().get("bot").getProperty("bot.in.active.rooms"));

        StartBotInActiveRooms startBotInActiveRoomsMode = (StartBotInActiveRooms)
                StartModeFactory.getStartModeStrategy("bot.in.active.rooms");

        return isActiveRoomsModeEnabled && startBotInActiveRoomsMode != null;
    }
}
