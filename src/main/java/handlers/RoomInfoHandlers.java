package handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.slf4j.*;

import gearth.protocol.*;

import discord.DiscordBot;

import database.dao.RoomsDAO;

import game.console.commands.start.StartConsoleCommand;

import models.RoomAccessMode;

import database.dao.data.*;

import game.ItemProcessor;

import scanner.HabboScanner;

public class RoomInfoHandlers {
    private static final Logger logger = LoggerFactory.getLogger(RoomInfoHandlers.class);

    private ItemProcessor itemProcessor;

    private int roomId;

    private long lastRoomAccess;

    private String currentOwnerName;

    private RoomAccessMode roomAccessMode = RoomAccessMode.UNKNOWN;

    public void onRoomReady(HMessage message) {
        HPacket packet = message.getPacket();

        packet.setReadIndex(15);

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();
        boolean criticalAirCrashWarning = HabboScanner.getInstance().getCriticalAirCrashWarning();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConsoleHandlers().getCommands().get(":start");
        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        if (discordBot != null && criticalAirCrashWarning && isBotRunning) {
            discordBot.sendMessageToFeedChannel("ma... che è successo @everyone? boo forse ero ubriaca perché ora il mio client funziona non so perché agaga ho ripreso a lavorare!");

            HabboScanner.getInstance().setCriticalAirCrashWarning(false);
        }

        roomId = packet.readInteger();

        itemProcessor = new ItemProcessor();

        lastRoomAccess = System.currentTimeMillis();

        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getBotProperties().getProperty("room_furni_active.enabled"));

        if (discordBot != null)
            discordBot.updateActivity("Sto archiviando la stanza ID: " + roomId);

        try {
            if (!isRoomFurniActiveEnabled) return;

            DataActiveDAO.deleteActiveData(roomId);
        } catch (SQLException exception) {
            logger.error(exception.getMessage());
        }
    }

    public void onGetGuestRoomResult(HMessage message) {
        boolean unknownVariable = message.getPacket().readBoolean();

        int guestRoomId = message.getPacket().readInteger();
        String roomName = message.getPacket().readString();

        byte[] bytes = roomName.getBytes(StandardCharsets.ISO_8859_1);
        roomName = new String(bytes, StandardCharsets.UTF_8);

        int ownerId = message.getPacket().readInteger();
        currentOwnerName = message.getPacket().readString();

        bytes = currentOwnerName.getBytes(StandardCharsets.ISO_8859_1);

        currentOwnerName = new String(bytes, StandardCharsets.UTF_8);

        try {
            RoomsDAO.insertRoom(guestRoomId, roomName, ownerId, currentOwnerName);
        } catch (SQLException | IOException exception) {
            throw new RuntimeException(exception);
        }

        roomAccessMode = RoomAccessMode.fromValue(message.getPacket().readInteger());
    }

    public void onRoomVisualizationSettings(HMessage message) {
        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner
                .getInstance().getConsoleHandlers().getCommands().get(":start");

        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        boolean isBotInActiveRooms = Boolean.parseBoolean(HabboScanner.getInstance()
                .getBotProperties().getProperty("bot.in.active.rooms"));

        if (!isBotRunning & !isBotInActiveRooms) return;

        HabboScanner.getInstance().goToHotelView();
    }

    public void refreshLastRoomAccess() {
        this.lastRoomAccess = System.currentTimeMillis();
    }

    public int getRoomId() {
        return roomId;
    }

    public long getLastRoomAccess() {
        return lastRoomAccess;
    }

    public ItemProcessor getItemProcessor() {
        return itemProcessor;
    }

    public String getCurrentOwnerName() {
        return currentOwnerName;
    }

    public RoomAccessMode getRoomAccessMode() {
        return roomAccessMode;
    }
}
