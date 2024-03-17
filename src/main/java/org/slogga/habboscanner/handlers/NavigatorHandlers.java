package org.slogga.habboscanner.handlers;

import java.sql.SQLException;

import java.util.*;
import java.util.concurrent.*;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.console.commands.start.StartConsoleCommand;
import org.slogga.habboscanner.logic.game.commands.console.commands.start.modes.StartBotInActiveRoomsMode;

import gearth.protocol.*;

import org.slogga.habboscanner.dao.mysql.*;

import org.slogga.habboscanner.HabboScanner;

import org.slogga.habboscanner.models.CommandKeys;
import org.slogga.habboscanner.utils.DateUtils;

public class NavigatorHandlers {
    private static final int ROOM_VISIT_INTERVAL_MILLISECONDS = 15 * 60 * 1000;

    private final Map<Integer, Long> roomTimestamps = new HashMap<>();

    public void onNavigatorSearchResultBlocks(HMessage message) {
        CompletableFuture.runAsync(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());
            boolean isBotRunning = startConsoleCommand.isBotRunning();

            if (!isBotRunning) return;

            HPacket packet = message.getPacket();

            String category = packet.readString();
            String searchQuery = packet.readString();

            int openState = packet.readInteger();

            StartConsoleCommand startCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands().get(CommandKeys.START.getKey());

            StartBotInActiveRoomsMode startBotInActiveRoomsMode = (StartBotInActiveRoomsMode)
                    startCommand.getStartModes().get("bot.in.active.rooms");

            // Checks if the room is open.
            if (openState == 0) {
                startBotInActiveRoomsMode.setProcessingActiveRooms(false);

                return;
            }

            String unknownVariable2 = packet.readString();
            String unknownVariable3 = packet.readString();
            int unknownVariable4 = packet.readInteger();
            int unknownVariable5 = packet.readInteger();
            boolean unknownVariable6 = packet.readBoolean();

            int roomsFoundAmount = packet.readInteger();

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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

                if (roomId < 1)
                    startBotInActiveRoomsMode.setProcessingActiveRooms(false);

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

                if (accessMode == 0 || currentTime - lastRoomTime > ROOM_VISIT_INTERVAL_MILLISECONDS) {
                    roomTimestamps.put(roomId, currentTime);

                    HabboActions.moveToRoom(roomId);
                }

                String room = roomId + "<|~|>" + roomName + "<|~|>" + roomDescription +
                        "<|~|>" + ownerName + "<|~|>" + roomUserAmount + "<|~|>" +
                        maximumRoomUserAmount + "<|~|>" + accessMode;

                rooms.add(room);
            }

            startBotInActiveRoomsMode.setProcessingActiveRooms(false);

            boolean isStatisticsInsertionActive = Boolean.parseBoolean(HabboScanner.getInstance()
                    .getConfigurator()
                    .getProperties()
                    .get("bot").getProperty("statistics.insertion.active"));

            if (!isStatisticsInsertionActive) return;

            String currentDateTime = DateUtils.getCurrentDateTime();

            try {
                StatsDAO.insertStats(totalRoomUserAmount, roomsFoundAmount, currentDateTime);

                NavigatorRoomsDAO.deleteNavigatorRooms();
                NavigatorRoomsDAO.insertNavigatorRooms(rooms);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}
