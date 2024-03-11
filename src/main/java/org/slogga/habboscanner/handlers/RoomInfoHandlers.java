package org.slogga.habboscanner.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import lombok.Data;
import org.slf4j.*;

import gearth.protocol.*;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.dao.mysql.RoomsDAO;

import org.slogga.habboscanner.logic.game.HabboActions;
import org.slogga.habboscanner.logic.game.console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.models.CommandKeys;
import org.slogga.habboscanner.models.RoomAccessMode;

import org.slogga.habboscanner.dao.mysql.data.*;

import org.slogga.habboscanner.logic.game.ItemProcessor;

import org.slogga.habboscanner.HabboScanner;
@Data
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

        roomId = packet.readInteger();

        itemProcessor = new ItemProcessor();

        lastRoomAccess = System.currentTimeMillis();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        boolean criticalAirCrashWarning = HabboScanner.getInstance().isCriticalAirCrashWarning();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) HabboScanner.getInstance()
                .getConfigurator()
                .getConsoleHandlers()
                .getCommands()
                .get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        if (discordBot != null && criticalAirCrashWarning && isBotRunning) {
            discordBot.sendMessageToFeedChannel("ma... che è successo @everyone? boo forse ero ubriaca perché ora il mio client funziona non so perché agaga ho ripreso a lavorare!");

            HabboScanner.getInstance().setCriticalAirCrashWarning(false);
        }

        boolean isRoomFurniActiveEnabled = Boolean.parseBoolean(HabboScanner.getInstance()
                .getConfigurator()
                .getProperties()
                .get("bot")
                .getProperty("room_furni_active.enabled"));

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
                .getInstance()
                .getConfigurator()
                .getConsoleHandlers().getCommands().get(CommandKeys.START.getKey());

        boolean isBotRunning = startConsoleCommand.getIsBotRunning();

        boolean isBotInActiveRooms = Boolean.parseBoolean(HabboScanner.getInstance()
                        .getConfigurator()
                        .getProperties()
                        .get("bot")
                        .getProperty("bot.in.active.rooms"));

        if (!isBotRunning & !isBotInActiveRooms) return;

        HabboActions.goToHotelView();
    }

    public void refreshLastRoomAccess() {
        this.lastRoomAccess = System.currentTimeMillis();
    }
}
