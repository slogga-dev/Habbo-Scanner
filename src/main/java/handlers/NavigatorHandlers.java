package handlers;

import java.sql.SQLException;

import java.util.*;
import java.util.concurrent.*;

import game.console.commands.start.StartConsoleCommand;
import game.console.commands.start.modes.StartBotInActiveRoomsMode;

import gearth.protocol.*;

import database.dao.*;

import scanner.HabboScanner;

import utils.DateUtils;

public class NavigatorHandlers {
    public void onNavigatorSearchResultBlocks(HMessage message) {
        CompletableFuture.runAsync(() -> {
            StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                    .getInstance().getConsoleHandlers().getCommands().get(":start");
            boolean isBotRunning = startConsoleCommand.getIsBotRunning();

            if (!isBotRunning) return;

            HPacket packet = message.getPacket();

            String category = packet.readString();
            String searchQuery = packet.readString();

            int openState = packet.readInteger();

            StartConsoleCommand startCommand = (StartConsoleCommand) HabboScanner.getInstance()
                    .getConsoleHandlers().getCommands().get(":start");

            StartBotInActiveRoomsMode startBotInActiveRoomsMode = (StartBotInActiveRoomsMode)
                    startCommand.getStartModes().get("bot.in.active.rooms");

            // Checks if the room is open.
            if (openState == 0) {
                startBotInActiveRoomsMode.setIsProcessingActiveRooms(false);

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
                    startBotInActiveRoomsMode.setIsProcessingActiveRooms(false);

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

                if (accessMode == 0) {
                    HabboScanner.getInstance().moveToRoom(roomId);

                    ScheduledFuture<?> future = executorService.schedule(() ->
                            HabboScanner.getInstance().moveToRoom(roomId), 2, TimeUnit.SECONDS);

                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException exception) {
                        throw new RuntimeException(exception);
                    }
                }

                String room = roomId + "<|~|>" + roomName + "<|~|>" + roomDescription +
                        "<|~|>" + ownerName + "<|~|>" + roomUserAmount + "<|~|>" +
                        maximumRoomUserAmount + "<|~|>" + accessMode;

                rooms.add(room);
            }

            startBotInActiveRoomsMode.setIsProcessingActiveRooms(false);

            boolean isStatisticsInsertionActive = Boolean.parseBoolean(HabboScanner.getInstance()
                    .getBotProperties().getProperty("statistics.insertion.active"));

            if (!isStatisticsInsertionActive) return;

            String currentDateTime = DateUtils.getCurrentDateTime();

            try {
                StatsDAO.insertStats(totalRoomUserAmount, roomsFoundAmount, currentDateTime);

                NavigatorRoomsDAO.deleteNavigatorRooms();
                NavigatorRoomsDAO.insertNavigatorData(rooms);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}
