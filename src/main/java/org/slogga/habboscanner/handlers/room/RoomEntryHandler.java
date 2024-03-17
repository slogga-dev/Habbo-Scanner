package org.slogga.habboscanner.handlers.room;

import org.slf4j.*;

import java.sql.SQLException;

import lombok.Data;

import gearth.protocol.*;

import org.slogga.habboscanner.discord.DiscordBot;

import org.slogga.habboscanner.logic.game.commands.CommandFactory;
import org.slogga.habboscanner.logic.game.commands.console.commands.start.StartConsoleCommand;

import org.slogga.habboscanner.models.CommandKeys;

import org.slogga.habboscanner.dao.mysql.data.*;

import org.slogga.habboscanner.logic.game.ItemProcessor;

import org.slogga.habboscanner.HabboScanner;
@Data
public class RoomEntryHandler {
    private static final Logger logger = LoggerFactory.getLogger(RoomEntryHandler.class);

    private ItemProcessor itemProcessor;

    private int roomId;

    private long lastRoomAccess;

    public void onRoomReady(HMessage message) {
        HPacket packet = message.getPacket();

        packet.setReadIndex(15);

        roomId = packet.readInteger();
        itemProcessor = new ItemProcessor();
        lastRoomAccess = System.currentTimeMillis();

        DiscordBot discordBot = HabboScanner.getInstance().getDiscordBot();

        boolean criticalAirCrashWarning = HabboScanner.getInstance().isCriticalAirCrashWarning();

        StartConsoleCommand startConsoleCommand = (StartConsoleCommand) CommandFactory.commandExecutorInstance.getCommands()
                .get(CommandKeys.START.getKey());
        boolean isBotRunning = startConsoleCommand.isBotRunning();

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

    public void refreshLastRoomAccess() {
        lastRoomAccess = System.currentTimeMillis();
    }
}
